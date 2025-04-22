package hr.spring.web.sinewave.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_gen")
    @SequenceGenerator(name = "song_id_gen", sequenceName = "song_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "albumid", nullable = false)
    private Album albumid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "genreid", nullable = false)
    private Genre genreid;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "filepath", nullable = false, length = Integer.MAX_VALUE)
    private String filepath;

    @ColumnDefault("now()")
    @Column(name = "createdat", nullable = false)
    private Instant createdat;

    @OneToMany(mappedBy = "songid")
    private Set<Favouritesong> favouritesongs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "songid")
    private Set<Playlistsong> playlistsongs = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public Album getAlbumid() {
        return albumid;
    }

    public void setAlbumid(Album albumid) {
        this.albumid = albumid;
    }

    public Genre getGenreid() {
        return genreid;
    }

    public void setGenreid(Genre genreid) {
        this.genreid = genreid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Instant getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Instant createdat) {
        this.createdat = createdat;
    }

    public Set<Favouritesong> getFavouritesongs() {
        return favouritesongs;
    }

    public void setFavouritesongs(Set<Favouritesong> favouritesongs) {
        this.favouritesongs = favouritesongs;
    }

    public Set<Playlistsong> getPlaylistsongs() {
        return playlistsongs;
    }

    public void setPlaylistsongs(Set<Playlistsong> playlistsongs) {
        this.playlistsongs = playlistsongs;
    }

}