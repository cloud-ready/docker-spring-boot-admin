package top.infra.cloudready.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin(); //allow use of frame to same origin urls for h2-console
        http
            .formLogin()
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .permitAll();
        http
            .logout().logoutUrl("/logout");
        http
            .csrf().ignoringAntMatchers("/h2-console/**").disable();
        http
            .authorizeRequests()
            .antMatchers("/h2-console/**").hasRole("ADMIN") // allow h2 console access to admins only
            .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**")
            .permitAll();
        http
            .authorizeRequests()
            .antMatchers("/**")
            .authenticated();
        http.httpBasic();
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
        //return new BCryptPasswordEncoder(5);
        return new StandardPasswordEncoder();
    }

    UserDetails admin() {
        final SecurityProperties.User userProperties = this.securityProperties.getUser();
        final Collection<? extends GrantedAuthority> authorities = userProperties.getRole().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
        final UserDetails admin = new User( //
            userProperties.getName(), //
            this.passwordEncoder().encode(userProperties.getPassword()), //
            true, true, true, true, //
            authorities //
        );
        return admin;
    }
}
