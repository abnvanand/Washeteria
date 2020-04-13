package github.abnvanand.washeteria.models.pojo;

public class LoginRequestBody {
    String username;
    String password;

    public LoginRequestBody(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
