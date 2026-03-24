package dk.easv.eventticketapp.be;

public class User {
    private int id;
    private String email;
    private UserRole role;
    private String name;
    private String surname;
    private String password;
    private String username;

    public User(String email, UserRole role, String name, String surname, String password, String username) {
        //this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

}
