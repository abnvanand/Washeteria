package github.abnvanand.washeteria.ui.login;

import android.app.Activity;
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
import github.abnvanand.washeteria.shareprefs.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private TextView textViewUsername;
    private TextView textViewToken;
    private TextView textViewExpiresAt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        View loginForm = findViewById(R.id.loginForm);
        View logoutForm = findViewById(R.id.logoutForm);


        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewToken = findViewById(R.id.textViewToken);
        textViewExpiresAt = findViewById(R.id.textViewExpiresAt);
        final Button logoutButton = findViewById(R.id.btnLogout);

        loginViewModel.getLoggedInStatus()
                .observe(this, isLoggedIn -> {
                    if (isLoggedIn) {
                        loginForm.setVisibility(View.INVISIBLE);
                        logoutForm.setVisibility(View.VISIBLE);

                        updateLogoutPageWithLoggedInUser();

                    } else {
                        logoutForm.setVisibility(View.INVISIBLE);
                        loginForm.setVisibility(View.VISIBLE);
                    }
                });


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

        loginViewModel.getLoginResult()
                .observe(this, loginResult -> {
                    if (loginResult == null) {
                        return;
                    }
                    loadingProgressBar.setVisibility(View.GONE);

                    if (loginResult.getError() != null) {
                        showLoginFailed(loginResult.getError());
                        return;
                    }

                    if (loginResult.getSuccess() != null) {
                        updateUiWithUser(loginResult.getSuccess());
                    }

                    setResult(Activity.RESULT_OK);

                    //Complete and destroy login activity once successful
                    finish();
                });

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

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
