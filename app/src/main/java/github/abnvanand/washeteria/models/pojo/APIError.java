package github.abnvanand.washeteria.models.pojo;

import com.google.gson.annotations.SerializedName;

public class APIError {
    @SerializedName("errorCode")
    private String errorCode;
    private int httpStatusCode;

    @SerializedName("message")
    private String message;


    public APIError(String errorCode, int httpStatusCode, String message) {
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
                ", messsage='" + message + '\'' +
                '}';
    }
}
