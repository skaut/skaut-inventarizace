package cz.skaut.warehousemanager.fragment;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.entity.Role;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.manager.LoginManager;
import me.tatarka.rxloader.RxLoader1;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class RoleFragment extends BaseFragment {

	@Bind(R.id.roleSpinner) Spinner roleSpinner;
	@Bind(R.id.progressWheel) ProgressWheel progressWheel;
	@Bind(R.id.roleBox) RelativeLayout roleBox;

	private RxLoader1<Role, Object> roleLoader;

	public RoleFragment() {
		// Required empty public constructor
	}

	public static RoleFragment newInstance(List<Role> roles) {
		RoleFragment fragment = new RoleFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList(C.ROLES_INDEX, new ArrayList<>(roles));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		hideUpButton();
		setTitle(R.string.choose_role);

		Bundle bundle = this.getArguments();
		List<Role> roleList = bundle.getParcelableArrayList(C.ROLES_INDEX);
		if (roleList == null) {
			throw new AssertionError("RoleFragment created without list of roles");
		}

		LoginManager loginManager = WarehouseApplication.getLoginManager();

		// configure adapter
		ArrayAdapter<Role> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, roleList);
		adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		roleSpinner.setAdapter(adapter);

		// if role list contains previous role, select it
		int selected = -1;
		long roleId = prefs.getLong(C.USER_ROLE_ID, 0);
		for (int i = 0; i < roleList.size(); i++) {
			if (roleList.get(i).getId() == roleId) {
				selected = i;
				break;
			}
		}

		if (selected != -1) {
			roleSpinner.setSelection(selected);
		}

		roleLoader = RxLoaderManagerCompat.get(this).create(
				loginManager::chooseRole,
				new RxLoaderObserver<Object>() {
					@Override
					public void onStarted() {
						progressWheel.setVisibility(View.VISIBLE);
						roleBox.setVisibility(View.GONE);
					}

					@Override
					public void onNext(Object o) {

					}

					@Override
					public void onCompleted() {
						getActivity().getSupportFragmentManager().beginTransaction()
								.replace(R.id.container, WarehouseListFragment.newInstance()).commit();
					}

					@Override
					public void onError(Throwable e) {
						// TODO: handle errors
						Timber.e(e.toString());
						progressWheel.setVisibility(View.GONE);
						roleBox.setVisibility(View.VISIBLE);
					}
				});
	}

	@OnClick(R.id.chooseButton)
	public void selectRole() {
		Role selectedRole = (Role) roleSpinner.getSelectedItem();

		if (selectedRole != null) {
			roleLoader.restart(selectedRole);
		}
	}

	@Override
	protected int getFragmentLayout() {
		return R.layout.fragment_role;
	}
}
