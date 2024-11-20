package Model;

public class passApps {
    private String id;
    private String appName;
    private String email;
    private String userName;
    private String password;
    private String userEmail;
    private String notas;

    public void passApp() {}

    public void passApp(String id, String appName, String email, String userName, String password, String userEmail, String notas) {
        this.id = id;
        this.appName = appName;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
        this.notas = notas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
