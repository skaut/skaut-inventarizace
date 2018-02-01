package cz.skaut.warehousemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.skaut.warehousemanager.fragment.LoginFragment;
import cz.skaut.warehousemanager.fragment.WarehouseListFragment;
import cz.skaut.warehousemanager.helper.C;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (savedInstanceState == null) {
            boolean prefIsLogged = WarehouseApplication.getPrefs().getBoolean(C.USER_IS_LOGGED, false);
            Fragment fragment;
            if (prefIsLogged) {
                fragment = WarehouseListFragment.newInstance();
            } else {
                fragment = LoginFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                WarehouseApplication.getLoginManager().logout();
                FragmentManager manager = getSupportFragmentManager();
                manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                manager.beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance())
                        .commit();
                return true;
            /*case R.id.settings:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container, SettingsFragment.newInstance())
                        .commit();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
