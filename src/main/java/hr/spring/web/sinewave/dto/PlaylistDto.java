package hr.spring.web.sinewave.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {
    private Integer id;
    private String name;
    private UserDto createdBy;
    private Instant createdAt;
    private Boolean isPublic;
    private Integer songCount;
}