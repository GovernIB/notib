package es.caib.notib.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.annotation.PostConstruct;
import java.util.List;

@Profile("test")
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
//@Order(1)
public class InMemorySecurityConfig extends GlobalMethodSecurityConfiguration {

    private static final String ROLE_PREFIX = "";

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder().username("user").password("user").authorities("tothom").build();
        UserDetails adm = User.builder().username("admin").password("admin").authorities("tothom", "NOT_ADMIN").build();
        UserDetails sup = User.builder().username("super").password("super").authorities("tothom", "NOT_SUPER").build();
        UserDetails apl = User.builder().username("apl").password("apl").authorities("tothom", "NOT_APL").build();
        return new InMemoryUserDetailsManager(user, adm, sup, apl);
    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(ROLE_PREFIX);
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        AffirmativeBased accessDecisionManager = (AffirmativeBased)super.accessDecisionManager();
        for(AccessDecisionVoter voter: accessDecisionManager.getDecisionVoters()){
            if(voter instanceof RoleVoter){
                ((RoleVoter) voter).setRolePrefix("");
            }
        }
        return accessDecisionManager;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = (DefaultMethodSecurityExpressionHandler) super.createExpressionHandler();
        expressionHandler.setDefaultRolePrefix("");
        return expressionHandler;
    }

    @PostConstruct
    public void inicialitzaExpressionHandler() {
        setMethodSecurityExpressionHandler(List.of(createExpressionHandler()));
    }

}
