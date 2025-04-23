package hr.spring.web.sinewave.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
