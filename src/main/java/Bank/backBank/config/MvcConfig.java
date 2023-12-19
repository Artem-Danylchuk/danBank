package Bank.backBank.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/registerStepTwo").setViewName("registerStepTwo");
        registry.addViewController("/account").setViewName("account");
        registry.addViewController("/cardsPage").setViewName("accountPage/cardsPage");
        registry.addViewController("/myAccount").setViewName("accountPage/myAccount");
        registry.addViewController("/exchange").setViewName("accountPage/exchange");
        registry.addViewController("/history").setViewName("accountPage/history");
        registry.addViewController("/resetPasswordStepOne").setViewName("resetPassword/resetPasswordStepOne");
        registry.addViewController("/resetPasswordStepTwo").setViewName("resetPassword/resetPasswordStepTwo");
        registry.addViewController("/admin").setViewName("adminPages/admin");
        registry.addViewController("/unauthorized").setViewName("unauthorized");
        registry.addViewController("/contact").setViewName("contact");
        registry.addViewController("/system").setViewName("accountPage/system");
        registry.addViewController("/adminAllClients").setViewName("adminPages/adminAllClients");
        registry.addViewController("/adminDetailsClients").setViewName("adminPages/adminDetailsClients");
        registry.addViewController("/historyInformation").setViewName("accountPage/historyInformation");
    }


}