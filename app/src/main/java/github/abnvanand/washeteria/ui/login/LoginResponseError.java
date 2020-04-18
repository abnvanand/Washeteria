package github.abnvanand.washeteria.ui.login;

public class LoginResponseError {
    private String errorCode;
    private String messsage;

    public LoginResponseError(String errorCode, String messsage) {
        this.errorCode = errorCode;
        this.messsage = messsage;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
