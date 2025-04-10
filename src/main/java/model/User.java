package model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_id_gen")
    @SequenceGenerator(name = "User_id_gen", sequenceName = "User_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
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
    private Set<model.Album> albums = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<model.Favouritesong> favouritesongs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdby")
    private Set<model.Playlist> playlists = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<model.Song> songs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<model.Userfriend> sentFriendRequests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "friendid")
    private Set<model.Userfriend> receivedFriendRequests = new LinkedHashSet<>();

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

    public Set<model.Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<model.Album> albums) {
        this.albums = albums;
    }

    public Set<model.Favouritesong> getFavouritesongs() {
        return favouritesongs;
    }

    public void setFavouritesongs(Set<model.Favouritesong> favouritesongs) {
        this.favouritesongs = favouritesongs;
    }

    public Set<model.Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Set<model.Playlist> playlists) {
        this.playlists = playlists;
    }

    public Set<model.Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<model.Song> songs) {
        this.songs = songs;
    }

    public Set<model.Userfriend> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setSentFriendRequests(Set<model.Userfriend> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
    }

    public Set<model.Userfriend> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public void setReceivedFriendRequests(Set<model.Userfriend> receivedFriendRequests) {
        this.receivedFriendRequests = receivedFriendRequests;
    }

}