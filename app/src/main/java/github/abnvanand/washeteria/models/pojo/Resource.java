package github.abnvanand.washeteria.models.pojo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {
    private Status status;
    private T data;
    private APIError error;

    public Resource() {

    }


    public Resource(@NonNull Status status, @Nullable T data, @Nullable APIError error) {
        this.status = status;
        if (data != null)
            this.data = data;
        this.error = error;
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

    public APIError getError() {
        return error;
    }

    public void setError(APIError error) {
        this.error = error;
    }

    public Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public Resource<T> error(APIError error, T data) {
        return new Resource<>(Status.ERROR, data, error);
    }

    public Resource<T> loading(T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

}
