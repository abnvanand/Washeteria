package github.abnvanand.washeteria.ui.login;

import github.abnvanand.washeteria.models.LoggedInUser;

public class LoggedInStatus {
    private boolean isLoggedIn;

    private LoggedInUser user;



    public LoggedInStatus(boolean isLoggedIn, LoggedInUser user) {
        this.isLoggedIn = isLoggedIn;
        this.user = user;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public LoggedInUser getUser() {
        return user;
    }
}
