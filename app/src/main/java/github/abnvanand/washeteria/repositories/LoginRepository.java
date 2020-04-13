package github.abnvanand.washeteria.repositories;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.models.pojo.LoginRequestBody;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private MutableLiveData<Result> loginResultObservable = new MutableLiveData<>();

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    private LoginRepository() {
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public void login(String username, String password) {

        // handle login
        WebService webService = RetrofitSingleton.getRetrofitInstance()
                .create(WebService.class);

        LoginRequestBody requestBody = new LoginRequestBody(username, password);

        webService
                .login(requestBody)
                .enqueue(new Callback<LoggedInUser>() {
                    @Override
                    public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                        if (!response.isSuccessful()) {
                            loginResultObservable.postValue(new Result.Error(new IOException(response.message())));
                            return;
                        }

                        loginResultObservable.postValue(new Result.Success<LoggedInUser>(response.body()));
                    }

                    @Override
                    public void onFailure(Call<LoggedInUser> call, Throwable t) {
                        loginResultObservable.postValue(new Result.Error(new IOException(t.getLocalizedMessage())));

                    }
                });
    }

    public MutableLiveData<Result> getLoginResultObservable() {
        return loginResultObservable;
    }

}
