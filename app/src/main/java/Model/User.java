package Model;

public class User{
    private String email;
    private String nameUser;

    public User(String email, String nameUser) {
        this.email = email;
        this.nameUser = nameUser;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", nameUser='" + nameUser + '\'' +
                '}';
    }
}

