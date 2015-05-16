package cz.skaut.warehousemanager.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
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

    @InjectView(R.id.userName)
    EditText userNameText;

    @InjectView(R.id.password)
    EditText passwordText;

    @InjectView(R.id.loginButton)
    Button loginButton;

    @InjectView(R.id.progressWheel)
    ProgressWheel progressWheel;

    @InjectView(R.id.loginBox)
    RelativeLayout loginBox;

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
        setTitle(getString(R.string.login_title));

        String prefUserName = WarehouseApplication.getPrefs().getString(C.USER_NAME, "");
        if (!TextUtils.isEmpty(prefUserName)) {
            userNameText.setText(prefUserName);
        }

        String prefUserPassword = WarehouseApplication.getPrefs().getString(C.USER_PASSWORD, "");
        if (!TextUtils.isEmpty(prefUserPassword)) {
            passwordText.setText(prefUserPassword);
        }

        loginLoader = RxLoaderManagerCompat.get(this).create(
                (username, password) -> WarehouseApplication.getLoginManager().login(username, password),
                new RxLoaderObserver<List<Role>>() {
                    @Override
                    public void onNext(List<Role> roles) {
                        Timber.d("got roles: " + roles);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, RoleFragment.newInstance(new ArrayList<>(roles)))
                                .commit();
                    }

                    @Override
                    public void onStarted() {
                        loginBox.setVisibility(View.GONE);
                        progressWheel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO: handle errors
                        Timber.e(e.toString());
                        Toast.makeText(getActivity(), getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                        loginBox.setVisibility(View.VISIBLE);
                        progressWheel.setVisibility(View.GONE);
                    }
                });

    }

    @OnClick(R.id.loginButton)
    public void login() {
        if (TextUtils.isEmpty(userNameText.getText()) || TextUtils.isEmpty(passwordText.getText())) {
            Toast.makeText(getActivity(), getString(R.string.login_info_needed), Toast.LENGTH_SHORT).show();
        } else {
            loginLoader.restart(userNameText.getText().toString(), passwordText.getText().toString());
        }
    }
}
