package hr.spring.web.sinewave.repository;

import hr.spring.web.sinewave.model.Playlist;
import hr.spring.web.sinewave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByCreatedby(User user);
    List<Playlist> findByIspublicTrue();
    List<Playlist> findByCreatedbyOrIspublicTrue(User user);
}