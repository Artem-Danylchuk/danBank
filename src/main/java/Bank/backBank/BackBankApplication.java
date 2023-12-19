package Bank.backBank;

import Bank.backBank.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackBankApplication {

	@Autowired
	private EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(BackBankApplication.class, args);
	}


//	@EventListener(ApplicationReadyEvent.class)
//	public void triigerMail() throws MessagingException {
//		Context context = new Context();
//		context.setVariable("code", "035894");
//
//		emailService.sendPasswordResetEmail("artemiouberwarszawa@gmail.com","1","email.html",context);
//	}

}
