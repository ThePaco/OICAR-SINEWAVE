package hr.spring.web.sinewave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SongCreateDto {
    @NotBlank
    private String title;

    @NotNull
    private Integer userId;

    private Integer albumId;
    private Integer genreId;
}
