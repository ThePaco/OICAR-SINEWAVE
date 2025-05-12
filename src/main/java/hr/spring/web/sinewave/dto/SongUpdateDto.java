package hr.spring.web.sinewave.dto;

import lombok.Data;

@Data
public class SongUpdateDto {
    private String title;
    private Integer albumId;
    private Integer genreId;
    private Integer duration;
    private String filepath;
}
