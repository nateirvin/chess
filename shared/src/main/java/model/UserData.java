package model;

public class UserData
{
    private int id;
    private final String username;
    private String password;
    private final String email;

    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserData(RegisterRequest registration) {
        this(registration.username(), registration.password(), registration.email());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
}
