package top.infra.cloudready.springboot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.security.enabled:false}")
    private Boolean enabled;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (this.enabled) {
            http = http //
                .authorizeRequests() //
                // .requestMatchers(EndpointRequest.to("health", "info")).permitAll() //
                // .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR") //
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll() //
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() //
                .antMatchers("/h2-console/**").hasRole("ADMIN") // allow h2 console access to admins only
                // .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**").permitAll()
                .antMatchers("/**/hystrix.stream").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().loginPage("/login.html")
                .loginProcessingUrl("/login.html").permitAll()
                .and() //
                .logout().logoutUrl("/logout").permitAll()
                .and() //
                .httpBasic()
                .and() //
            ;
        } else {
            http = http //
                .authorizeRequests() //
                .anyRequest().permitAll() //
                .and() //
            ;
        }
        http.headers().frameOptions().sameOrigin() // allow use of frame to same origin urls for h2-console
            .and() //
            .csrf()
            // .ignoringAntMatchers("/h2-console/**")
            .disable() //
        ;
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/assets/**/*");
        web.httpFirewall(this.allowUrlEncodedSlashHttpFirewall());
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication()
            .dataSource(this.dataSource)
            .passwordEncoder(this.passwordEncoder());
    }

    @Bean
    @Override
    public JdbcUserDetailsManager userDetailsService() {
        final JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setJdbcTemplate(this.jdbcTemplate);

        final UserDetails admin = this.admin();
        manager.deleteUser(admin.getUsername());
        manager.createUser(admin);

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        final String idForEncode = "bcrypt";
        final Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());

        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    UserDetails admin() {
        final SecurityProperties.User userProperties = this.securityProperties.getUser();
        final Collection<? extends GrantedAuthority> authorities = userProperties.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
        final String encodedPassword = this.passwordEncoder().encode(userProperties.getPassword());
        final UserDetails admin = new User( //
            userProperties.getName(), //
            encodedPassword, //
            true, true, true, true, //
            authorities //
        );
        return admin;
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        // allow urls like "/eureka//apps/SERVICE-FILTER-TEST-APPLICATION"
        // see: https://stackoverflow.com/questions/48453980/spring-5-0-3-requestrejectedexception-the-request-was-rejected-because-the-url
        final DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        // final StrictHttpFirewall firewall = new StrictHttpFirewall();
        // firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }
}
