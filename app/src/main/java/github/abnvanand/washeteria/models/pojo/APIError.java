package github.abnvanand.washeteria.models.pojo;

public class APIError {
    private String errorCode;
    private int httpStatusCode;
    private String messsage;


    public APIError(String errorCode, int httpStatusCode, String messsage) {
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.messsage = messsage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
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

    @Override
    public String toString() {
        return "APIError{" +
                "errorCode='" + errorCode + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", messsage='" + messsage + '\'' +
                '}';
    }
}
