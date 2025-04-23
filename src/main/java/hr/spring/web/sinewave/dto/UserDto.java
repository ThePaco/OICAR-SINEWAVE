package hr.spring.web.sinewave.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String profilepicture;
}
