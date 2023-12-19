package Bank.backBank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name="clients_login")
@Getter
@Setter
@NoArgsConstructor
public class ClientsLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String login;

    private String password;

    @Transient
    private String confirmPassword;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private ClientsAccount clientsAccount;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private ClientsInformation clientsInformation;

    @OneToMany(mappedBy = "clientsLogin",cascade = CascadeType.ALL)
    private List<ClientsHistory> clientsHistoryList;

    public ClientsLogin(String login, String password, String email, UserRole role) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.role = role;
    }

}
