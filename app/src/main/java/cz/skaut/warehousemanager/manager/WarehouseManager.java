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
import io.realm.Sort;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WarehouseManager {

	private final Realm realm;
	private final SharedPreferences prefs;
	private final Context context;

//	private Realm realm;
//	private Context context;
//	private SharedPreferences prefs;

	public WarehouseManager(Context ctx) {
		context = ctx.getApplicationContext();
		realm = Realm.getDefaultInstance();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Selects the warehouse from database
	 *
	 * @param id id of the warehouse
	 * @return Warehouse
	 */
	public Warehouse getWarehouse(long id) {
		Timber.i(">>> Vybran sklad " + id);
		return realm.where(Warehouse.class).equalTo("id", id).findFirst();
	}

	/**
	 * Gets all warehouses sorted by their parent-child relationships
	 *
	 * @return sorted list of Warehouses
	 */
	private Observable<List<Warehouse>> getAllWarehousesSorted() {
		RealmResults<Warehouse> warehouses = realm.where(Warehouse.class).findAll();
		warehouses = warehouses.sort("id");
		warehouses = warehouses.sort("id", Sort.ASCENDING);
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
						// remove are existing warehouses from database
						realm.where(Warehouse.class).findAll().deleteAllFromRealm();
						// copy returned warehouses to database
						List<Warehouse> realmWarehouses = realm.copyToRealmOrUpdate(warehouseAllResult.getWarehouseList());
						for (Warehouse w : realmWarehouses) {
							if (w.getIdParentWarehouse() != 0) {
								// create relationship between child and parent warehouses
								Warehouse parentWarehouse = realm.where(Warehouse.class).equalTo("id", w.getIdParentWarehouse()).findFirst();
								w.setParentWarehouse(parentWarehouse);
							}
						}

						prefs.edit().putBoolean(C.WAREHOUSES_LOADED, true).apply();
						return Observable.empty();
					}))
					.observeOn(AndroidSchedulers.mainThread()) // move to main thread to fetch results
					.flatMap(warehouseAllResult -> getAllWarehousesSorted());
		}
	}
}
