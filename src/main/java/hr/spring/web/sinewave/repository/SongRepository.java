package hr.spring.web.sinewave.repository;

import hr.spring.web.sinewave.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    List<Song> findByUserid_Id(Integer userId);

    List<Song> findByAlbumid_Id(Integer albumId);

    List<Song> findByGenreid_Id(Integer genreId);

    List<Song> findByTitle(String title);
}
