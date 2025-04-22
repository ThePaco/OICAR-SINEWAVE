package hr.spring.web.sinewave.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_id_gen")
    @SequenceGenerator(name = "playlist_id_gen", sequenceName = "playlist_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "createdby", nullable = false)
    private User createdby;

    @ColumnDefault("now()")
    @Column(name = "createdat", nullable = false)
    private Instant createdat;

    @Column(name = "ispublic", nullable = false)
    private Boolean ispublic = false;

    @OneToMany(mappedBy = "playlistid")
    private Set<Playlistsong> playlistsongs = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreatedby() {
        return createdby;
    }

    public void setCreatedby(User createdby) {
        this.createdby = createdby;
    }

    public Instant getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Instant createdat) {
        this.createdat = createdat;
    }

    public Boolean getIspublic() {
        return ispublic;
    }

    public void setIspublic(Boolean ispublic) {
        this.ispublic = ispublic;
    }

    public Set<Playlistsong> getPlaylistsongs() {
        return playlistsongs;
    }

    public void setPlaylistsongs(Set<Playlistsong> playlistsongs) {
        this.playlistsongs = playlistsongs;
    }

}