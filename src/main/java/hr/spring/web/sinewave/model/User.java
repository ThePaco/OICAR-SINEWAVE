package hr.spring.web.sinewave.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, insertable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "firstname", nullable = false, length = 60)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 60)
    private String lastname;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "passwordhash", nullable = false, length = Integer.MAX_VALUE)
    private String passwordhash;

    @Column(name = "passwordsalt", nullable = false, length = Integer.MAX_VALUE)
    private String passwordsalt;

    @Column(name = "profilepicture", length = Integer.MAX_VALUE)
    private String profilepicture;

    @OneToMany(mappedBy = "userid")
    private Set<Album> albums = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Favouritesong> favouritesongs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdby")
    private Set<Playlist> playlists = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Song> songs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Userfriend> sentFriendRequests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "friendid")
    private Set<Userfriend> receivedFriendRequests = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }

    public String getPasswordsalt() {
        return passwordsalt;
    }

    public void setPasswordsalt(String passwordsalt) {
        this.passwordsalt = passwordsalt;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public Set<Favouritesong> getFavouritesongs() {
        return favouritesongs;
    }

    public void setFavouritesongs(Set<Favouritesong> favouritesongs) {
        this.favouritesongs = favouritesongs;
    }

    public Set<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Set<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public Set<Userfriend> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setSentFriendRequests(Set<Userfriend> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
    }

    public Set<Userfriend> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public void setReceivedFriendRequests(Set<Userfriend> receivedFriendRequests) {
        this.receivedFriendRequests = receivedFriendRequests;
    }

}