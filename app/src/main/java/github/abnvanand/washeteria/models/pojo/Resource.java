package github.abnvanand.washeteria.models.pojo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {
    private Status status;
    private T data;
    private String msg;

    public Resource() {

    }


    private Resource(@NonNull Status status, @Nullable T data, @Nullable String msg) {
        this.status = status;
        if (data != null)
            this.data = data;
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public Resource<T> error(String msg, T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public Resource<T> loading(T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

}
