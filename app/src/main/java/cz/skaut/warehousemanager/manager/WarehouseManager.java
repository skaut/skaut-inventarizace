package cz.skaut.warehousemanager.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;

import cz.skaut.warehousemanager.entity.Warehouse;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.SortUtils;
import cz.skaut.warehousemanager.rx.RealmObservable;
import cz.skaut.warehousemanager.soap.SkautApiManager;
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

    public Warehouse getWarehouse(long id) {
        return realmUI.where(Warehouse.class).equalTo("id", id).findFirst();
    }

    private Observable<List<Warehouse>> getAllWarehousesSorted() {
        RealmResults<Warehouse> warehouses = realmUI.allObjectsSorted(Warehouse.class, "id", RealmResults.SORT_ORDER_ASCENDING);
        return Observable.just(new SortUtils().sort(warehouses));
    }

    public Observable<List<Warehouse>> getWarehouses() {
        if (prefs.getBoolean(C.WAREHOUSES_LOADED, false)) {
            return getAllWarehousesSorted();
        } else {
            WarehouseAll request = new WarehouseAll();
            return SkautApiManager.getWarehouseApi().getAllWarehouses(request)
                    .flatMap(warehouseAllResult -> RealmObservable.work(context, realm -> {
                        realm.allObjects(Warehouse.class).clear();
                        List<Warehouse> realmWarehouses = realm.copyToRealm(warehouseAllResult.getWarehouseList());
                        for (Warehouse w : realmWarehouses) {
                            if (w.getIdParentWarehouse() != 0) {
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
