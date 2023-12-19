package Bank.backBank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clients_history")
public class ClientsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;
    private Long cardNumberSender;
    private String senderName;
    private String senderSurname;
    private String recipientName;
    private String recipientSurname;
    private Long cardNumberRecipient;
    private String amount;
    private Double rates;

    @ManyToOne
    @JoinColumn(name = "clients_login_id")
    private ClientsLogin clientsLogin;

}
