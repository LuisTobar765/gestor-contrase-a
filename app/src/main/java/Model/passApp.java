package Model;

public class passApp {
    private String appName;
    private String email;
    private String id;
    private String notas;
    private String password;
    private String userEmail;

    public passApp(String appName, String email, String id, String notas, String password, String userEmail) {
        this.appName = appName;
        this.email = email;
        this.id = id;
        this.notas = notas;
        this.password = password;
        this.userEmail = userEmail;
    }

    public passApp() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "passApp{" +
                "appName='" + appName + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", notas='" + notas + '\'' +
                ", password='" + password + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
