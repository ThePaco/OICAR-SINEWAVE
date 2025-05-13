package hr.spring.web.sinewave.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongDto {
    @NotNull(message = "Song ID is required")
    private Integer songId;

    @NotNull(message = "Playlist ID is required")
    private Integer playlistId;
}
