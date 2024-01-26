package Bank.backBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddInformationUserDTO {

    private String country;
    private String city;
    private String street;
    private String zipCode;
    private String name;
    private String surname;
    private String number;
    private String secretWord;

}
