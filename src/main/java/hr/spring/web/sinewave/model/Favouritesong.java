package hr.spring.web.sinewave.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "favouritesongs")
public class Favouritesong {
    @SequenceGenerator(name = "favouritesongs_id_gen", sequenceName = "genre_id_seq", allocationSize = 1)
    @EmbeddedId
    private FavouritesongId id;

    @MapsId("userid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    @MapsId("songid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "songid", nullable = false)
    private Song songid;

    @ColumnDefault("now()")
    @Column(name = "addedat", nullable = false)
    private Instant addedat;

    public FavouritesongId getId() {
        return id;
    }

    public void setId(FavouritesongId id) {
        this.id = id;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public Song getSongid() {
        return songid;
    }

    public void setSongid(Song songid) {
        this.songid = songid;
    }

    public Instant getAddedat() {
        return addedat;
    }

    public void setAddedat(Instant addedat) {
        this.addedat = addedat;
    }

}