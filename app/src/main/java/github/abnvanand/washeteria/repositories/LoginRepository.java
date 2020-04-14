package github.abnvanand.washeteria.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.models.pojo.LoginRequestBody;
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
        // handle login
        LoginRequestBody requestBody = new LoginRequestBody(username, password);

        webService
                .login(requestBody)
                .enqueue(new Callback<LoggedInUser>() {
                    @Override
                    public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                        if (!response.isSuccessful()) {
//                            loginResultObservable.postValue(new Result.Error(new IOException(response.message())));
                            showError(response.message());
                            return;
                        }
                        LoggedInUser loggedInUser = response.body();
                        if (loggedInUser != null) {
                            addUserToSharedPrefs(loggedInUser);

                            // TODO: Reset retrofit instance
                        }

//                        loginResultObservable.postValue(new Result.Success<LoggedInUser>(response.body()));
                    }

                    @Override
                    public void onFailure(Call<LoggedInUser> call, Throwable t) {
//                        loginResultObservable.postValue(new Result.Error(new IOException(t.getLocalizedMessage())));
                        clearFromSharedPrefs();
                    }
                });
    }

    public void logout() {
        // TODO: Send API request to logout API

        // Clear sharedPrefs
        clearFromSharedPrefs();
        loggedInStatusObservable.postValue(new LoggedInStatus(false, null));

        // TODO: reset retrofit instance

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
                loggedInStatusObservable.postValue(new LoggedInStatus(true, loggedInUser));

            }
        });
    }

    public MutableLiveData<LoggedInStatus> getLoggedInStatusObservable() {
        return loggedInStatusObservable;
    }


}
