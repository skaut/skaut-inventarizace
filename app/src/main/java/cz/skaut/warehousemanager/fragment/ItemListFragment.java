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
import cz.skaut.warehousemanager.adapters.ItemAdapter;
import cz.skaut.warehousemanager.entity.Item;
import cz.skaut.warehousemanager.entity.Warehouse;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.DividerItemDecoration;
import cz.skaut.warehousemanager.helper.EmptyRecyclerView;
import cz.skaut.warehousemanager.manager.ItemManager;
import cz.skaut.warehousemanager.manager.WarehouseManager;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class ItemListFragment extends BaseFragment {

    @InjectView(R.id.itemList)
    EmptyRecyclerView itemList;

    @InjectView(R.id.noItemText)
    TextView noItemText;

    @InjectView(R.id.progressWheel)
    ProgressWheel progressWheel;

    private ItemAdapter adapter;

    private Warehouse warehouse;

    public static ItemListFragment newInstance(long warehouseId) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putLong(C.WAREHOUSE_INDEX, warehouseId);
        fragment.setArguments(args);
        return fragment;
    }

    public ItemListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        WarehouseManager warehouseManager = WarehouseApplication.getWarehouseManager();
        ItemManager itemManager = WarehouseApplication.getItemManager();

        Bundle bundle = this.getArguments();
        warehouse = warehouseManager.getWarehouse(bundle.getLong(C.WAREHOUSE_INDEX));

        setTitle(warehouse.getName());

        itemList.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemList.setHasFixedSize(true);
        itemList.setEmptyView(noItemText);
        itemList.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        adapter = new ItemAdapter(getActivity(), Collections.<Item>emptyList());
        itemList.setAdapter(adapter);

        ItemClickSupport clickSupport = ItemClickSupport.addTo(itemList);
        clickSupport.setOnItemClickListener((recyclerView, view1, position, l) -> {
            Item item = adapter.getItem(position);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ItemDetailFragment.newInstance(item.getId()))
                    .addToBackStack(null).commit();
        });

        final RxLoaderManager loaderManager = RxLoaderManagerCompat.get(this);

        loaderManager.create(
                itemManager.getItems(warehouse.getId()),
                new RxLoaderObserver<List<Item>>() {
                    @Override
                    public void onStarted() {
                        progressWheel.setVisibility(View.VISIBLE);
                        itemList.setVisibility(View.GONE);
                        noItemText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(List<Item> items) {
                        adapter.setData(items);
                    }

                    @Override
                    public void onCompleted() {
                        progressWheel.setVisibility(View.GONE);
                        itemList.setVisibility(View.VISIBLE);

                        // show inventorize option
                        getActivity().supportInvalidateOptionsMenu();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO: handle errors
                        Timber.e(e.getMessage());
                        e.printStackTrace();
                    }
                }).start();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_item_list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (prefs.getBoolean(C.ITEMS_LOADED, false) && adapter.getItemCount() > 0) {
            inflater.inflate(R.menu.item_list_menu, menu);
        }
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.warehouse_inventorize) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, InventorizeFragment.newInstance(warehouse.getId()))
                    .addToBackStack(null).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
