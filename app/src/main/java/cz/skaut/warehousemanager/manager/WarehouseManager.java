package cz.skaut.warehousemanager.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;

import cz.skaut.warehousemanager.entity.Warehouse;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.SortUtils;
import cz.skaut.warehousemanager.rx.RealmObservable;
import cz.skaut.warehousemanager.soap.SkautISApiManager;
import cz.skaut.warehousemanager.soap.WarehouseAll;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WarehouseManager {

	private final Realm realmUI;
	private final SharedPreferences prefs;
	private final Context context;

	public WarehouseManager(Context ctx) {
		context = ctx.getApplicationContext();
		realmUI = Realm.getInstance(context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Selects the warehouse from database
	 *
	 * @param id id of the warehouse
	 * @return Warehouse
	 */
	public Warehouse getWarehouse(long id) {
		return realmUI.where(Warehouse.class).equalTo("id", id).findFirst();
	}

	/**
	 * Gets all warehouses sorted by their parent-child relationships
	 *
	 * @return sorted list of Warehouses
	 */
	private Observable<List<Warehouse>> getAllWarehousesSorted() {
		RealmResults<Warehouse> warehouses = realmUI.allObjectsSorted(Warehouse.class, "id", RealmResults.SORT_ORDER_ASCENDING);
		return Observable.just(new SortUtils().sort(warehouses));
	}

	/**
	 * Gets all warehouses either from database of from remote server
	 *
	 * @return sorted list of warehouses
	 */
	public Observable<List<Warehouse>> getWarehouses() {
		// return warehouses from database if they are already loaded
		if (prefs.getBoolean(C.WAREHOUSES_LOADED, false)) {
			return getAllWarehousesSorted();
		} else {
			WarehouseAll request = new WarehouseAll();
			return SkautISApiManager.getWarehouseApi().getAllWarehouses(request)
					.flatMap(warehouseAllResult -> RealmObservable.work(context, realm -> {
						// removes are existing warehouses from database
						realm.allObjects(Warehouse.class).clear();

						// copies returned warehouses to database
						List<Warehouse> realmWarehouses = realm.copyToRealm(warehouseAllResult.getWarehouseList());
						// creates relationship between child and parent warehouses
						realmWarehouses.stream().filter(w -> w.getIdParentWarehouse() != 0).forEach(w -> {
							// creates relationship between child and parent warehouses
							Warehouse parentWarehouse = realm.where(Warehouse.class).equalTo("id", w.getIdParentWarehouse()).findFirst();
							w.setParentWarehouse(parentWarehouse);
						});
						prefs.edit().putBoolean(C.WAREHOUSES_LOADED, true).apply();
						return Observable.empty();
					}))
					.observeOn(AndroidSchedulers.mainThread()) // move to main thread to fetch results
					.flatMap(warehouseAllResult -> getAllWarehousesSorted());
		}
	}
}
