package Bank.backBank.config;

import Bank.backBank.dto.CreateUserDTO;
import Bank.backBank.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class AppConfig extends GlobalMethodSecurityConfiguration {

    public static final String ADMIN_LOGIN = "admin";

    @Bean
    public CommandLineRunner demo(final UserService userService,
                                  final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
//                userService.addAdmin(ADMIN_LOGIN, "password", "");
 //               userService.addUser(new CreateUserDTO("2", "2", ""));
 //               userService.addUser(new CreateUserDTO("1","1","1"));
            }
        };
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}