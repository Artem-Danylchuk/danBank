package Bank.backBank.controllers;

import Bank.backBank.dto.AddInformationUserDTO;
import Bank.backBank.dto.CreateUserDTO;
import Bank.backBank.entity.ClientsHistory;
import Bank.backBank.entity.ClientsLogin;
import Bank.backBank.repository.ClientsHistoryRepository;
import Bank.backBank.repository.ClientsLoginRepository;
import Bank.backBank.services.EmailService;
import Bank.backBank.services.SecurityService;
import Bank.backBank.services.UserService;
import Bank.backBank.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;
import java.util.Random;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MyController {

    private final EmailService emailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ClientsLoginRepository clientsLoginRepository;
    private final ClientsHistoryRepository clientsHistoryRepository;

    private String resetPasswordEmail;
    private String resetPasswordCode;

    @Autowired
    private SecurityService securityService;

    @ExceptionHandler(SessionAuthenticationException.class)
    public String handleSessionAuthenticationException(SessionAuthenticationException e) {
        return "login";
    }


    @GetMapping("/account")
    public String index1(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        User user = SecurityUtils.getCurrentUser();

        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);


        Page<ClientsHistory> historyPage = clientsHistoryRepository.findAllByClientsLoginId(
                dbUser.getId(),
                PageRequest.of(page, 8, Sort.by(Sort.Order.desc("id")))
        );
        model.addAttribute("historyPage", historyPage);


        model.addAttribute("login", login);
        model.addAttribute("roles", user.getAuthorities());
        model.addAttribute("admin", isAdmin(user));
        model.addAttribute("dol", dbUser.getClientsAccount().getUsd());
        model.addAttribute("eur", dbUser.getClientsAccount().getEur());
        model.addAttribute("uah", dbUser.getClientsAccount().getUah());
        model.addAttribute("lastNumberCardUah", getLastNDigits(dbUser.getClientsAccount().getCardUah(), 4));
        model.addAttribute("lastNumberCardUsd", getLastNDigits(dbUser.getClientsAccount().getCardUsd(), 4));
        model.addAttribute("lastNumberCardEur", getLastNDigits(dbUser.getClientsAccount().getCardEur(), 4));
        try {
            emailService.sendEmailFromGuest("-","-", "new visitor", "-");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "account";
    }


    @PostMapping(value = "/updateInfo")
    public String updateInformation(@RequestParam(required = false) String country, @RequestParam(required = false) String city,
                                    @RequestParam(required = false) String street, @RequestParam(required = false) String zipCode,
                                    @RequestParam(required = false) String name, @RequestParam(required = false) String surname,
                                    @RequestParam(required = false) String number) {
        User user = SecurityUtils.getCurrentUser();

        String login = user.getUsername();
        userService.updateInformation(login, country, city, street, zipCode, name, surname, number);

        return "redirect:/account";
    }

    @PostMapping("/add")
    public String registerNewClient(@ModelAttribute("createUserDTO") CreateUserDTO createUserDTO, @RequestParam("copyPassword") String copyPassword, Model model) {

        log.info("Creating new user with login {}", createUserDTO.getLogin());
        if (clientsLoginRepository.existsByLogin(createUserDTO.getLogin())) {
            log.info("Пользователь с таким логином уже существует. {}", createUserDTO.getLogin());
            model.addAttribute("errorLogin", "A user with this login already exists.");
            return "registerStepOne";
        }
        if (!copyPassword.equals(createUserDTO.getPassword())) {
            log.info("Пароли не совподают. ");
            model.addAttribute("errorPassword", "The passwords don't match.");
            return "registerStepOne";
        }
        userService.addUser(createUserDTO);
        securityService.autoLogin(createUserDTO.getLogin(), createUserDTO.getPassword());

        return "redirect:/registerStepTwo";

    }

    @GetMapping("/registerStepTwo")
    public String registerStepTwo(Model model) {
        model.addAttribute("registerStepTwo", new AddInformationUserDTO());
        return "registerStepTwo";
    }

    @PostMapping("/registerStepTwo")
    public String registerStepTwo(@ModelAttribute("registerStepTwo") AddInformationUserDTO addInformationUserDTO, Model model) {
        User user = SecurityUtils.getCurrentUser();

        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);
        userService.addInformation(login, addInformationUserDTO);
        return "redirect:/account";
    }

    @GetMapping("/login")
    public String loginPage(Principal principal){
        if (principal != null) {
            return "redirect:/account";
        } else {
            return "login";
        }
    }

    @GetMapping("/registerStepOne")
    public String register(Model model, Principal principal) {
       if (principal != null) {
            return "redirect:/account";
        } else {
            model.addAttribute("createUserDTO", new CreateUserDTO());
            return "registerStepOne";
        }
    }


    @GetMapping("/unauthorized")
    public String unauthorized(Model model) {
        User user = SecurityUtils.getCurrentUser();
        model.addAttribute("login", user.getUsername());
        return "unauthorized";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    private static Long getLastNDigits(Long number, int n) {
        long modulo = (long) Math.pow(10, n);
        return number % modulo;
    }

    private boolean isAdmin(User user) {
        Collection<GrantedAuthority> roles = user.getAuthorities();

        for (GrantedAuthority auth : roles) {
            if ("ROLE_ADMIN".equals(auth.getAuthority()))
                return true;
        }

        return false;
    }

    @PostMapping(value = "/resetPassword")
    public String resetPassword(@RequestParam(required = false) String email, Model model) throws MessagingException {

        if (clientsLoginRepository.existsByEmail(email)) {

            Context context = new Context();
            context.setVariable("code", randomNumberForEmail());
            resetPasswordEmail = email;
            emailService.sendPasswordResetEmail(resetPasswordEmail, "email.html", context);

            model.addAttribute("done", "done");


            return "resetPassword/resetPasswordStepTwo";
        } else {
            System.out.println("error reset password");
            model.addAttribute("error", "error");
        }
        return "resetPassword/resetPasswordStepOne";
    }

    @GetMapping("/resetPassword")
    public String resetPasswordGet() {
        return "resetPassword/resetPasswordStepOne";
    }

    @GetMapping("/resetPasswordStepTwo")
    public String resetPasswordGetTwo() {
        return "resetPassword/resetPasswordStepTwo";
    }

    @PostMapping(value = "/resetPasswordStepTwo")
    public String resetPasswordTwo(@RequestParam(required = false) String code, @RequestParam("copyPassword") String copyPassword, @RequestParam("password") String password, Model model) throws MessagingException {

        if (!copyPassword.equals(password)) {
            model.addAttribute("errorPassword", "The passwords don't match.");
            return "resetPassword/resetPasswordStepTwo";
        }
        if (!code.equals(resetPasswordCode)) {
            model.addAttribute("errorCode", "The code don't match.");
            return "resetPassword/resetPasswordStepTwo";
        }
        ClientsLogin user = clientsLoginRepository.findByEmail(resetPasswordEmail);
        user.setPassword(passwordEncoder.encode(password));
        clientsLoginRepository.save(user);
        return "/login";
    }

    @PostMapping("/sendEmail")
    public String sendEmail(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String message, Model model
    ) throws MessagingException {
        emailService.sendEmailFromGuest(name, email, subject, message);
        return "redirect:/contact";
    }

    private String randomNumberForEmail() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        String randomString = String.valueOf(randomNumber);
        resetPasswordCode = randomString;
        System.out.println(randomString);
        return randomString;
    }
}
