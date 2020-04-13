package github.abnvanand.washeteria.ui.login;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.repositories.LoginRepository;
import github.abnvanand.washeteria.repositories.Result;
import github.abnvanand.washeteria.shareprefs.SessionManager;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private MediatorLiveData<Result> loginResultObservable = new MediatorLiveData<>();

    private MutableLiveData<Boolean> loggedInStatus = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private SessionManager sessionManager;
    private boolean firstLoad = true;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        this.loginRepository = LoginRepository.getInstance();

        loginResultObservable.addSource(loginRepository.getLoginResultObservable(),
                result -> {
//                    loginResultObservable.setValue(result);
                    if (result instanceof Result.Success) {
                        LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUsername(), data.getToken(), data.getExpiresAt())));
                        loggedInStatus.setValue(true);
                        sessionManager.saveSession(data);
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                        loggedInStatus.setValue(false);
                    }
                });
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<Boolean> getLoggedInStatus() {
        if (firstLoad) {
            firstLoad = false;
            loggedInStatus.setValue(sessionManager.isLoggedIn());
        }

        return loggedInStatus;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password);
    }

    public void logout() {
        // TODO: Send logout request
        loginResult.setValue(new LoginResult(R.string.logout_success));

        sessionManager.clearSession();
        loggedInStatus.setValue(false);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null;
    }
}
