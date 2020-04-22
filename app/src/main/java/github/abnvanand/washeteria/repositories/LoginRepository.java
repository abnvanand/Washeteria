package github.abnvanand.washeteria.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.models.pojo.APIError;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.shareprefs.SessionManager;
import github.abnvanand.washeteria.ui.login.LoggedInStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
// If user credentials will be cached in local storage, it is recommended it be encrypted
// @see https://developer.android.com/training/articles/keystore
public class LoginRepository {
    private static volatile LoginRepository instance;

    private MutableLiveData<LoggedInStatus> loggedInStatusObservable = new MutableLiveData<>();

    //    private MutableLiveData<Result> loginResultObservable = new MutableLiveData<>();
    private SessionManager mSessionManager;
    private WebService webService;


    // TODO: Make sure a single executor is being used to access SharedPrefs
    private Executor executor = Executors.newSingleThreadExecutor();

    private LoginRepository(Context context) {
        mSessionManager = new SessionManager(context);
        webService = RetrofitSingleton
                .getRetrofitInstance()
                .create(WebService.class);
    }

    public static LoginRepository getInstance(Context applicationContext) {
        if (instance == null) {
            instance = new LoginRepository(applicationContext);
        }
        return instance;
    }

    public void login(String username, String password) {
        webService
                .login(username, password).enqueue(new Callback<LoggedInUser>() {
            @Override
            public void onResponse(@NotNull Call<LoggedInUser> call,
                                   @NotNull Response<LoggedInUser> response) {
                if (!response.isSuccessful()) {
                    clearFromSharedPrefs();

                    String errorMessage = response.code() == HttpURLConnection.HTTP_UNAUTHORIZED ? "Invalid username/password." : String.valueOf(response.code());
                    loggedInStatusObservable
                            .postValue(new LoggedInStatus(false,
                                    null,
                                    new APIError(String.valueOf(response.code()), response.code(), errorMessage)));

                    return;
                }
                LoggedInUser loggedInUser = response.body();
                if (loggedInUser != null) {
                    // API Does not return username so fill it here on success
                    loggedInUser.setUsername(username);
                    addUserToSharedPrefs(loggedInUser);

                    // Reset retrofit instance
                    RetrofitSingleton.reset();
                }

//                        loginResultObservable.postValue(new Result.Success<LoggedInUser>(response.body()));
            }

            @Override
            public void onFailure(Call<LoggedInUser> call, Throwable t) {
//                        loginResultObservable.postValue(new Result.Error(new IOException(t.getLocalizedMessage())));
                clearFromSharedPrefs();
                loggedInStatusObservable.postValue(new LoggedInStatus(false,
                        null,
                        new APIError("N/W error", 0, t.getLocalizedMessage())));
            }
        });
    }

    public void logout() {
        // TODO: Send API request to logout API

        // Clear sharedPrefs
        clearFromSharedPrefs();
        loggedInStatusObservable.postValue(new LoggedInStatus(false, null, null));

        // reset retrofit instance
        RetrofitSingleton.reset();
    }

    private void clearFromSharedPrefs() {
        executor.execute(() -> {
            mSessionManager.clearSession();
        });
    }

    private void showError(String message) {
        Timber.e(message);
    }


//    public MutableLiveData<Result> getLoginResultObservable() {
//        return loginResultObservable;
//    }

    public void fetchLoggedInStatus() {
        loadFromSharedPreferences();
    }

    private void addUserToSharedPrefs(LoggedInUser loggedInUser) {
        executor.execute(() -> {
            mSessionManager.saveSession(loggedInUser);
            loadFromSharedPreferences();
        });
    }

    private void loadFromSharedPreferences() {
        // Perform Disk IO on separate thread
        executor.execute(() -> {
            if (mSessionManager.isLoggedIn()) {
                LoggedInUser loggedInUser = new LoggedInUser();
                loggedInUser.setUsername(mSessionManager.getUsername());
                loggedInUser.setToken(mSessionManager.getToken());
                loggedInUser.setExpiresAt(mSessionManager.getExpiresAt());

                // Notify observers
                loggedInStatusObservable.postValue(
                        new LoggedInStatus(
                                true,
                                loggedInUser,
                                null));

            }
        });
    }

    public MutableLiveData<LoggedInStatus> getLoggedInStatusObservable() {
        return loggedInStatusObservable;
    }


}
