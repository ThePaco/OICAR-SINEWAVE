package hr.spring.web.sinewave.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "userfriends")
public class Userfriend {
    @SequenceGenerator(name = "userfriends_id_gen", sequenceName = "song_id_seq", allocationSize = 1)
    @EmbeddedId
    private UserfriendId id;

    @MapsId("userid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    @MapsId("friendid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "friendid", nullable = false)
    private User friendid;

    @ColumnDefault("now()")
    @Column(name = "addedat", nullable = false)
    private Instant addedat;

    public UserfriendId getId() {
        return id;
    }

    public void setId(UserfriendId id) {
        this.id = id;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public User getFriendid() {
        return friendid;
    }

    public void setFriendid(User friendid) {
        this.friendid = friendid;
    }

    public Instant getAddedat() {
        return addedat;
    }

    public void setAddedat(Instant addedat) {
        this.addedat = addedat;
    }

}