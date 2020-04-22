package github.abnvanand.washeteria.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.models.pojo.APIError;

public class LoggedInStatus {
    @NonNull
    private boolean isLoggedIn;

    @Nullable
    private LoggedInUser user;

    @Nullable
    private APIError error;


    public LoggedInStatus(@NonNull boolean isLoggedIn,
                          @Nullable LoggedInUser user,
                          @Nullable APIError error) {
        this.isLoggedIn = isLoggedIn;
        this.user = user;
        this.error = error;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public LoggedInUser getUser() {
        return user;
    }

    @Nullable
    public APIError getError() {
        return error;
    }
}
