package cz.skaut.warehousemanager.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.entity.Role;
import cz.skaut.warehousemanager.helper.C;
import me.tatarka.rxloader.RxLoader2;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class LoginFragment extends BaseFragment {

    @Bind(R.id.userName)
    EditText userNameText;

    @Bind(R.id.password)
    EditText passwordText;

    @Bind(R.id.progressWheel)
    ProgressWheel progressWheel;

    @Bind(R.id.loginBox)
    LinearLayout loginBox;

    @Bind(R.id.userNameLayout)
    TextInputLayout userNameLayout;

    @Bind(R.id.passwordLayout)
    TextInputLayout passwordLayout;

    @Bind(R.id.loginButton)
    Button loginButton;

    private RxLoader2<String, String, List<Role>> loginLoader;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_login;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hideUpButton();
        setTitle(R.string.login_title);

        String prefUserName = WarehouseApplication.getPrefs().getString(C.USER_NAME, "");
        if (!TextUtils.isEmpty(prefUserName)) {
            userNameText.setText(prefUserName);
        }

        String prefUserPassword = WarehouseApplication.getPrefs().getString(C.USER_PASSWORD, "");
        if (!TextUtils.isEmpty(prefUserPassword)) {
            passwordText.setText(prefUserPassword);
        }

        // create login loader
        loginLoader = RxLoaderManagerCompat.get(this).create(
                (username, password) -> WarehouseApplication.getLoginManager().login(username, password),
                new RxLoaderObserver<List<Role>>() {
                    @Override
                    public void onNext(List<Role> roles) {
                        Timber.d("got roles: " + roles);
                        if (roles.size() == 0) {
                            loginBox.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                            Snackbar.make(view, R.string.login_no_roles, Snackbar.LENGTH_LONG).show();
                        } else {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, RoleFragment.newInstance(roles))
                                    .commit();
                        }
                    }

                    @Override
                    public void onStarted() {
                        loginBox.setVisibility(View.GONE);
                        progressWheel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Failed to login");
                        loginBox.setVisibility(View.VISIBLE);
                        progressWheel.setVisibility(View.GONE);
                        Snackbar.make(view, R.string.login_failed, Snackbar.LENGTH_LONG).show();
                    }
                });

    }

    @OnClick(R.id.loginButton)
    public void login() {
        boolean isNameEmpty = TextUtils.isEmpty(userNameText.getText());
        boolean isPasswordEmpty = TextUtils.isEmpty(passwordText.getText());

        if (isNameEmpty || isPasswordEmpty) {
            if (isNameEmpty) {
                userNameLayout.setError(getString(R.string.error_name_empty));
            }
            if (isPasswordEmpty) {
                passwordLayout.setError(getString(R.string.error_password_empty));
            }
        } else {
            userNameLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);
            loginLoader.restart(userNameText.getText().toString(), passwordText.getText().toString());
        }
    }
}
