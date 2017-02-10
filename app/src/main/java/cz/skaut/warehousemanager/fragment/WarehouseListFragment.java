package cz.skaut.warehousemanager.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.adapters.WarehouseAdapter;
import cz.skaut.warehousemanager.entity.Warehouse;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.DividerItemDecoration;
import cz.skaut.warehousemanager.helper.EmptyRecyclerView;
import cz.skaut.warehousemanager.manager.ItemManager;
import cz.skaut.warehousemanager.manager.WarehouseManager;
import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class WarehouseListFragment extends BaseFragment {

	@BindView(R.id.warehouseList) EmptyRecyclerView warehouseList;
	@BindView(R.id.noWarehouseText) TextView noWarehouseText;
	@BindView(R.id.progressWheel) ProgressWheel progressWheel;
	@BindView(R.id.syncButton) FloatingActionButton syncButton;

	private WarehouseAdapter adapter;

	private RxLoader<Object> syncLoader;

	private static final String SYNC_TAG = "SYNC_LOADER";
	private static final String WAREHOUSE_TAG = "WAREHOUSE_LOADER";

	public WarehouseListFragment() {
		// Required empty public constructor
	}

	public static WarehouseListFragment newInstance() {
		return new WarehouseListFragment();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		hideUpButton();
		setHasOptionsMenu(true);

		// configure RecyclerView
		warehouseList.setLayoutManager(new LinearLayoutManager(getActivity()));
		warehouseList.setHasFixedSize(true);
		warehouseList.setEmptyView(noWarehouseText);
		warehouseList.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider), false, false));

		WarehouseManager warehouseManager = WarehouseApplication.getWarehouseManager();
		ItemManager itemManager = WarehouseApplication.getItemManager();

		// configure adapter
		adapter = new WarehouseAdapter(Collections.<Warehouse>emptyList());
		warehouseList.setAdapter(adapter);

		setTitle(R.string.warehouse_list);
		setSubtitle(prefs.getString(C.USER_ROLE, ""));

		adapter.clicks()
				.doOnNext(view1 -> Timber.d("got smth"))
				.map(adapter::getItem)
				.subscribe(warehouse -> {
					getActivity().getSupportFragmentManager().beginTransaction()
							.replace(R.id.container, ItemListFragment.newInstance(warehouse.getId()))
							.addToBackStack(null).commit();
					//Timber.d("id: " + warehouse.getId());
				});

		RxLoaderManager loaderManager = RxLoaderManagerCompat.get(this);

		// create warehouse loader and starts loading
		loaderManager.create(
				WAREHOUSE_TAG,
				warehouseManager.getWarehouses(),
				new RxLoaderObserver<List<Warehouse>>() {
					@Override
					public void onStarted() {
						progressWheel.setVisibility(View.VISIBLE);
						warehouseList.setVisibility(View.GONE);
						noWarehouseText.setVisibility(View.GONE);
					}

					@Override
					public void onNext(List<Warehouse> warehouses) {
						adapter.setData(warehouses);
					}

					@Override
					public void onCompleted() {
						progressWheel.setVisibility(View.GONE);
						warehouseList.setVisibility(View.VISIBLE);

						if (prefs.getBoolean(C.SYNC_NEEDED, false)) {
							syncButton.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onError(Throwable e) {
						// TODO: handle errors
						Timber.e(e, "Error while fetching warehouses");
					}
				}).start();

		// create sync loader
		syncLoader = loaderManager.create(
				SYNC_TAG,
				itemManager.synchronize(),
				new RxLoaderObserver<Object>() {
					@Override
					public void onStarted() {
						progressWheel.setVisibility(View.VISIBLE);
						warehouseList.setVisibility(View.GONE);
						noWarehouseText.setVisibility(View.GONE);
					}

					@Override
					public void onNext(Object value) {
					}

					@Override
					public void onCompleted() {
						progressWheel.setVisibility(View.GONE);
						warehouseList.setVisibility(View.VISIBLE);
						syncButton.setVisibility(View.GONE);
					}

					@Override
					public void onError(Throwable e) {
						Timber.e(e, "Sync error");
						progressWheel.setVisibility(View.GONE);
						warehouseList.setVisibility(View.VISIBLE);
						Snackbar.make(view, R.string.sync_error, Snackbar.LENGTH_LONG).show();
					}
				});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_menu, menu);
	}

	@OnClick(R.id.syncButton) void sync() {
		syncLoader.restart();
	}

	@Override
	protected int getFragmentLayout() {
		return R.layout.fragment_warehouse_list;
	}
}
