package cz.skaut.warehousemanager.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.lucasr.twowayview.ItemClickSupport;

import java.util.Collections;
import java.util.List;

import butterknife.InjectView;
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

    @InjectView(R.id.warehouseList)
    EmptyRecyclerView warehouseList;

    @InjectView(R.id.noWarehouseText)
    TextView noWarehouseText;

    @InjectView(R.id.progressWheel)
    ProgressWheel progressWheel;

    private WarehouseAdapter adapter;

    private RxLoader<List<Object>> syncLoader;

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

        warehouseList.setLayoutManager(new LinearLayoutManager(getActivity()));
        warehouseList.setHasFixedSize(true);
        warehouseList.setEmptyView(noWarehouseText);
        warehouseList.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        WarehouseManager warehouseManager = WarehouseApplication.getWarehouseManager();
        ItemManager itemManager = WarehouseApplication.getItemManager();

        adapter = new WarehouseAdapter(getActivity(), Collections.<Warehouse>emptyList());
        warehouseList.setAdapter(adapter);

        setTitle(getString(R.string.warehouse_list));
        setSubtitle(prefs.getString(C.USER_ROLE, ""));

        ItemClickSupport clickSupport = ItemClickSupport.addTo(warehouseList);

        clickSupport.setOnItemClickListener((recyclerView, view1, position, l) -> {
            Warehouse warehouse = adapter.getItem(position);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ItemListFragment.newInstance(warehouse.getId()))
                    .addToBackStack(null).commit();
        });

        RxLoaderManager loaderManager = RxLoaderManagerCompat.get(this);

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
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO: handle errors
                        Timber.e("ERROR: " + e);
                    }
                }).start();

        syncLoader = loaderManager.create(
                SYNC_TAG,
                itemManager.synchronize(),
                new RxLoaderObserver<List<Object>>() {
                    @Override
                    public void onStarted() {
                        progressWheel.setVisibility(View.VISIBLE);
                        warehouseList.setVisibility(View.GONE);
                        noWarehouseText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(List<Object> value) {
                    }

                    @Override
                    public void onCompleted() {
                        progressWheel.setVisibility(View.GONE);
                        warehouseList.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.getMessage());
                        e.printStackTrace();
                        progressWheel.setVisibility(View.GONE);
                        warehouseList.setVisibility(View.VISIBLE);
                        showToast(R.string.sync_error);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (prefs.getBoolean(C.ITEMS_LOADED, false)) {
            inflater.inflate(R.menu.warehouse_list_menu, menu);
        }
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_synchronize) {
            syncLoader.restart();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_warehouse_list;
    }
}
