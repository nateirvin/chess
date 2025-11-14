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

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String email() {
        return email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public void password(String password) {
        this.password = password;
    }
}
