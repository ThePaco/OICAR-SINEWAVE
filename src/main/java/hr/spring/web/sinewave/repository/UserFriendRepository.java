package hr.spring.web.sinewave.repository;

import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.model.Userfriend;
import hr.spring.web.sinewave.model.UserfriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFriendRepository extends JpaRepository<Userfriend, UserfriendId> {
    List<Userfriend> findByUserid(User user);
    List<Userfriend> findByFriendid(User user);
    Optional<Userfriend> findByUseridAndFriendid(User user, User friend);
    boolean existsByUseridAndFriendid(User user, User friend);
}