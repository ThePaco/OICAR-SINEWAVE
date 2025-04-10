package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserfriendId implements Serializable {
    private static final long serialVersionUID = 7691333561341749691L;
    @Column(name = "userid", nullable = false)
    private Integer userid;

    @Column(name = "friendid", nullable = false)
    private Integer friendid;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getFriendid() {
        return friendid;
    }

    public void setFriendid(Integer friendid) {
        this.friendid = friendid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserfriendId entity = (UserfriendId) o;
        return Objects.equals(this.friendid, entity.friendid) &&
                Objects.equals(this.userid, entity.userid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendid, userid);
    }

}