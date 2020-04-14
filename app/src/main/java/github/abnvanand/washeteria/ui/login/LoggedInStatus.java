package github.abnvanand.washeteria.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import github.abnvanand.washeteria.models.LoggedInUser;

public class LoggedInStatus {
    @NonNull
    private boolean isLoggedIn;

    @Nullable
    private LoggedInUser user;

    @Nullable
    private Exception error;


    public LoggedInStatus(@NonNull boolean isLoggedIn,
                          @Nullable LoggedInUser user,
                          @Nullable Exception error) {
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
    public Exception getError() {
        return error;
    }
}
