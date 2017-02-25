package cz.skaut.warehousemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;
import cz.skaut.warehousemanager.fragment.LoginFragment;
import cz.skaut.warehousemanager.fragment.SettingsFragment;
import cz.skaut.warehousemanager.fragment.WarehouseListFragment;
import cz.skaut.warehousemanager.helper.C;

import io.realm.Realm;
//import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;
	private Unbinder unbinder;
	Realm realm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		unbinder = ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		toolbar.setNavigationOnClickListener(v -> onBackPressed());

		if (savedInstanceState == null) {
			boolean prefIsLogged = WarehouseApplication.getPrefs().getBoolean(C.USER_IS_LOGGED, false);
			Fragment fragment;
			if (prefIsLogged) {
				// user is logged in, show warehouse list
				fragment = WarehouseListFragment.newInstance();
			} else {
				// login needed, show login screen
				fragment = LoginFragment.Companion.newInstance();
			}
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, fragment)
					.commit();
		}

		Realm.init(this);
		realm = Realm.getDefaultInstance();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.logout:
				WarehouseApplication.getLoginManager().logout();
				FragmentManager manager = getSupportFragmentManager();
				manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				manager.beginTransaction()
						.replace(R.id.container, LoginFragment.Companion.newInstance())
						.commit();
				return true;
			case R.id.settings:
				getSupportFragmentManager().beginTransaction()
						.addToBackStack(null)
						.replace(R.id.container, SettingsFragment.newInstance())
						.commit();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		if (!realm.isClosed()) {
			realm.close();
		}
		super.onDestroy();
		unbinder.unbind();
	}
}
