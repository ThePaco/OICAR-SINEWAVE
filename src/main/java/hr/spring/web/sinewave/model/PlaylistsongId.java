package hr.spring.web.sinewave.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlaylistsongId implements Serializable {
    private static final long serialVersionUID = 2630881357819333980L;
    @Column(name = "playlistid", nullable = false)
    private Integer playlistid;

    @Column(name = "songid", nullable = false)
    private Integer songid;

    public Integer getPlaylistid() {
        return playlistid;
    }

    public void setPlaylistid(Integer playlistid) {
        this.playlistid = playlistid;
    }

    public Integer getSongid() {
        return songid;
    }

    public void setSongid(Integer songid) {
        this.songid = songid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PlaylistsongId entity = (PlaylistsongId) o;
        return Objects.equals(this.playlistid, entity.playlistid) &&
                Objects.equals(this.songid, entity.songid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistid, songid);
    }

}