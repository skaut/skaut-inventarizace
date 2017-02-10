package cz.skaut.warehousemanager.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collections;

import butterknife.BindView;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.adapters.InventorizeAdapter;
import cz.skaut.warehousemanager.entity.Item;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.DividerItemDecoration;
import cz.skaut.warehousemanager.manager.ItemManager;
import cz.skaut.warehousemanager.scanner.ZBarScannerView;
import timber.log.Timber;

public class InventorizeFragment extends BaseFragment {

	@BindView(R.id.scanner) ZBarScannerView scannerView;
	@BindView(R.id.itemInventorizeList) RecyclerView itemList;

	private InventorizeAdapter adapter;

	private ItemManager itemManager;

	public static InventorizeFragment newInstance(long warehouseId) {
		InventorizeFragment fragment = new InventorizeFragment();
		Bundle args = new Bundle();
		args.putLong(C.WAREHOUSE_INDEX, warehouseId);
		fragment.setArguments(args);
		return fragment;
	}

	public InventorizeFragment() {
		// Required empty public constructor
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setHasOptionsMenu(true);

		Bundle bundle = this.getArguments();
		long warehouseId = bundle.getLong(C.WAREHOUSE_INDEX);

		itemManager = WarehouseApplication.getItemManager();

		// configure RecyclerView
		itemList.setLayoutManager(new LinearLayoutManager(getActivity()));
		itemList.setHasFixedSize(true);
		itemList.addItemDecoration(new DividerItemDecoration(getActivity(), null));

		// configure adapter
		adapter = new InventorizeAdapter(Collections.<Item>emptyList());
		itemList.setAdapter(adapter);
		adapter.setData(itemManager.getAllUninventorizedItems(warehouseId));

		// subscribe for barcode events
		scannerView.getBarcodes().subscribe(barcode -> {
			Timber.d("Got code: " + barcode);
			adapter.inventorize(barcode);
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		scannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		scannerView.stopCamera();

		// send inventorized items to manager
		// TODO: check what happens if onPause is called twice on the same fragment!!
		itemManager.updateInventorizeStatus(adapter.getInventorizedItems());
	}

	@Override
	protected int getFragmentLayout() {
		return R.layout.fragment_inventorize;
	}
}
