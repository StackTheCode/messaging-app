package com.rkchat.demo.models;

import jakarta.persistence.*;




@Entity

@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique=true)
    private String username;

    @Column(nullable = true)
    private String password;


    private  String email;

    private  String role;

//    Fields for Oauth 2

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;


    public enum AuthProvider {
        LOCAL, GOOGLE
    }

    public User() {

    }

    public User(Long id) {
        this.id = id;
    }
    public User(String username, String password, String email, String role) {
        this.username = username;
         this.password = password;
         this.email = email;
         this.role = role;
    }
    public User(String username, String email, String role, String googleId, String profilePictureUrl) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.googleId = googleId;
        this.provider = AuthProvider.GOOGLE;
        this.profilePictureUrl = profilePictureUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
