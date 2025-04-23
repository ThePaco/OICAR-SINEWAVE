package hr.spring.web.sinewave.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "playlistsongs")
public class Playlistsong {
    @SequenceGenerator(name = "playlistsongs_id_gen", sequenceName = "playlist_id_seq", allocationSize = 1)
    @EmbeddedId
    private PlaylistsongId id;

    @MapsId("playlistid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "playlistid", nullable = false)
    private Playlist playlistid;

    @MapsId("songid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "songid", nullable = false)
    private Song songid;

    @ColumnDefault("now()")
    @Column(name = "addedat", nullable = false)
    private Instant addedat;

    public PlaylistsongId getId() {
        return id;
    }

    public void setId(PlaylistsongId id) {
        this.id = id;
    }

    public Playlist getPlaylistid() {
        return playlistid;
    }

    public void setPlaylistid(Playlist playlistid) {
        this.playlistid = playlistid;
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