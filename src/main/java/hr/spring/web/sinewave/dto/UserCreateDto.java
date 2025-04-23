package hr.spring.web.sinewave.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank
    private String username;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String profilepicture;
}
