package github.abnvanand.washeteria.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.models.LoggedInUser;
import github.abnvanand.washeteria.shareprefs.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private View loginForm;
    private EditText usernameEditText, passwordEditText;
    private ProgressBar loadingProgressBar;
    private Button loginButton;

    private View logoutForm;
    private TextView textViewUsername, textViewToken, textViewExpiresAt;
    private Button logoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginForm = findViewById(R.id.loginForm);
        logoutForm = findViewById(R.id.logoutForm);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewToken = findViewById(R.id.textViewToken);
        textViewExpiresAt = findViewById(R.id.textViewExpiresAt);
        logoutButton = findViewById(R.id.btnLogout);

        setupUIListeners();
        initViewModel();

//        loginViewModel.getLoginResult()
//                .observe(this, loginResult -> {
//                    Timber.d("Here1");
//                    if (loginResult == null) {
//                        Timber.d("Here2");
//                        return;
//                    }
//                    Timber.d("Here3");
//                    loadingProgressBar.setVisibility(View.GONE);
//
//                    if (loginResult.getError() != null) {
//                        Timber.d("Here4");
//                        showLoginFailed(loginResult.getError());
//                        return;
//                    }
//                    Timber.d("Here5");
//
//                    if (loginResult.getSuccess() != null) {
//                        Timber.d("Here6");
//                        updateUiWithUser(loginResult.getSuccess());
//                    }
//
//                    setResult(Activity.RESULT_OK);
//
//                    //Complete and destroy login activity once successful
//                    finish();
//                });


    }

    private void initViewModel() {
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        loginViewModel.getLoginFormState()
                .observe(this, loginFormState -> {
                    if (loginFormState == null) {
                        return;
                    }
                    loginButton.setEnabled(loginFormState.isDataValid());
                    if (loginFormState.getUsernameError() != null) {
                        usernameEditText.setError(getString(loginFormState.getUsernameError()));
                    }
                    if (loginFormState.getPasswordError() != null) {
                        passwordEditText.setError(getString(loginFormState.getPasswordError()));
                    }
                });

        loginViewModel.getLoggedInStatusObservable().observe(this,
                loggedInStatus -> {
                    if (loggedInStatus == null)
                        return;

                    if (loggedInStatus.isLoggedIn()) {
                        updateUIAfterLogin(loggedInStatus.getUser());
                    } else {
                        updateUIAfterLogout();
                    }
                });
    }

    private void updateUIAfterLogout() {
        loadingProgressBar.setVisibility(View.GONE);
        textViewUsername.setText("");
        textViewToken.setText("");
        textViewExpiresAt.setText("");
        logoutForm.setVisibility(View.INVISIBLE);
        loginForm.setVisibility(View.VISIBLE);
    }

    private void updateUIAfterLogin(LoggedInUser user) {
        loadingProgressBar.setVisibility(View.GONE);
        loginForm.setVisibility(View.INVISIBLE);
        logoutForm.setVisibility(View.VISIBLE);
        textViewUsername.setText(user.getUsername());
        textViewToken.setText(user.getToken());
        textViewExpiresAt.setText(user.getExpiresAt());
    }


    private void setupUIListeners() {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        logoutButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.logout();
        });
    }

    private void updateLogoutPageWithLoggedInUser() {
        SessionManager manager = new SessionManager(getApplication());
        textViewUsername.setText(manager.getUsername());
        textViewToken.setText(manager.getToken());
        textViewExpiresAt.setText(manager.getExpiresAt());
    }


//    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
//        // TODO : initiate successful logged in experience
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
