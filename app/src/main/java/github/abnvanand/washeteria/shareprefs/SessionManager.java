package github.abnvanand.washeteria.shareprefs;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import github.abnvanand.washeteria.models.LoggedInUser;

public class SessionManager {
    private static final String PREF_USERNAME = "username";
    private static final String PREF_TOKEN = "token";
    private static final String PREF_EXPIRES_AT = "expiresAt";
    private Context context;

    public SessionManager(Application application) {
        context = application.getApplicationContext();
    }


    public boolean isLoggedIn() {
        // TODO: Perform validation of token expiry time
        return !TextUtils.isEmpty(getToken());
    }

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveSession(LoggedInUser loggedInUser) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USERNAME, loggedInUser.getUsername());
        editor.putString(PREF_TOKEN, loggedInUser.getToken());
        editor.putString(PREF_EXPIRES_AT, loggedInUser.getExpiresAt());
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public boolean clearSession() {
        return getSharedPreferences(context)
                .edit()
                .clear()
                .commit();
    }

    public String getUsername() {
        return getSharedPreferences(context)
                .getString(PREF_USERNAME, null);
    }

    public String getToken() {
        return getSharedPreferences(context)
                .getString(PREF_TOKEN, null);
    }

    public String getExpiresAt() {
        return getSharedPreferences(context).getString(PREF_EXPIRES_AT, null);
    }
}
