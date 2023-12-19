package Bank.backBank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends MvcConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .sessionManagement()
                    .maximumSessions(1)
                    .expiredUrl("/login")
                    .and()
                .and()
                .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/account", "/updateInformation", "/myInformation","/testAccount","/cardsPage","/myAccount","/transfers","/exchange","/history","/system","/historyInformation").hasAnyRole("USER", "ADMIN")
                    .antMatchers("/admin","/adminAllClients","/adminDetailsClients").hasRole("ADMIN")
                    .antMatchers("/register", "/", "/login", "/home", "/add", "/registerStepTwo","/resetPasswordStepOne","/resetPasswordStepTwo","/unauthorized","/contact").permitAll()
                .and()
                    .exceptionHandling()
                .accessDeniedPage("/unauthorized")
                .and()
                 .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/account")
                    .loginProcessingUrl("/login")
                    .failureUrl("/login?error")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/");

        return http.build();
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }



}