package Bank.backBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInformationDTO {

    private Long cardUah;
    private Long cardEur;
    private Long cardUsd;

}
