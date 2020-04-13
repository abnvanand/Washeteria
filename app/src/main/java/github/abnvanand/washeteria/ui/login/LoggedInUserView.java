package github.abnvanand.washeteria.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private String token;
    private String expiresAt;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String token, String expiresAt) {
        this.displayName = displayName;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    String getDisplayName() {
        return displayName;
    }

    public String getToken() {
        return token;
    }

    public String getExpiresAt() {
        return expiresAt;
    }
}
