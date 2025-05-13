package hr.spring.web.sinewave.repository;

import hr.spring.web.sinewave.model.Playlistsong;
import hr.spring.web.sinewave.model.PlaylistsongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistSongRepository extends JpaRepository<Playlistsong, PlaylistsongId> {
    List<Playlistsong> findByPlaylistid_Id(Integer playlistId);
    List<Playlistsong> findBySongid_Id(Integer songId);
    void deleteByPlaylistid_IdAndSongid_Id(Integer playlistId, Integer songId);
    boolean existsByPlaylistid_IdAndSongid_Id(Integer playlistId, Integer songId);
}
