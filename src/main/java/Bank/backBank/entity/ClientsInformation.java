package Bank.backBank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "clients_information")
@Getter
@Setter
@NoArgsConstructor
public class ClientsInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String city;
    private String street;
    private String zipCode;

    private String name;
    private String surname;

    private String number;
    private String secretWord;

    @OneToOne(mappedBy = "clientsInformation")
    private ClientsLogin clientsLogin;

    public ClientsInformation(String name, String surname, String secretWord,String number,String street, String country, String city,  String zipCode  ) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
        this.name = name;
        this.surname = surname;
        this.number = number;
        this.secretWord = secretWord;
    }

}
