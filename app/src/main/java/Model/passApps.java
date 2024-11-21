package Model;

public class passApps {
    private String id;
    private String appName;
    private String email;
    private String userName;
    private String password;
    private String notas;
    private String userEmail;

    // Constructor vacío necesario para Firebase
    public passApps() {}

    // Constructor con parámetros
    public passApps(String id, String appName, String email, String userName, String password, String notas, String userEmail) {
        this.id = id;
        this.appName = appName;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.notas = notas;
        this.userEmail = userEmail;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
