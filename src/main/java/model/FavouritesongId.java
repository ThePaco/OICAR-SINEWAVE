package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavouritesongId implements Serializable {
    private static final long serialVersionUID = 7265345345077500634L;
    @Column(name = "userid", nullable = false)
    private Integer userid;

    @Column(name = "songid", nullable = false)
    private Integer songid;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
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
        FavouritesongId entity = (FavouritesongId) o;
        return Objects.equals(this.userid, entity.userid) &&
                Objects.equals(this.songid, entity.songid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, songid);
    }

}