package hr.spring.web.sinewave.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class SongDto {
    private Integer id;
    private String title;
    private Integer userId;
    private Integer albumId;
    private Integer genreId;
    private Long duration;
    private String filepath;
    private Instant createdAt;
}
