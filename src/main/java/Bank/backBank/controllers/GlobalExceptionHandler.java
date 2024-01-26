package Bank.backBank.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@SpringBootApplication
public class GlobalExceptionHandler implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "404";
    }

    public String getErrorPath() {
        return "/error";
    }
}
