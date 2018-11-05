package top.infra.sample.admin;

import com.google.common.collect.ImmutableList;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersAndGroupsInitializer implements InitializingBean {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private IdentityService identityService;

    @Override
    public void afterPropertiesSet() throws Exception {
        final List<String> roles = ImmutableList.copyOf(this.securityProperties.getUser().getRole());
        for (final String groupId : roles) {
            this.identityService.deleteGroup(groupId);

            final Group group = this.identityService.newGroup(groupId);
            group.setName(groupId);
            group.setType("security-role");
            this.identityService.saveGroup(group);
        }

        final String userId = this.securityProperties.getUser().getName();
        this.identityService.deleteUser(userId);
        //final List<String> userInfoKeys = this.identityService.getUserInfoKeys(userId);
        //if (userInfoKeys.isEmpty()) {
        final User admin = this.identityService.newUser(userId);
        admin.setEmail(userId + "@example.com");
        admin.setPassword(this.securityProperties.getUser().getPassword());
        this.identityService.saveUser(admin);
        this.identityService.setUserInfo(userId, "set", "true");
        //}

        for (final String groupId : roles) {
            this.identityService.createMembership(userId, groupId);
        }
    }
}
