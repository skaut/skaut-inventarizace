package cz.skaut.warehousemanager;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.leakcanary.LeakCanary;

import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.manager.ItemManager;
import cz.skaut.warehousemanager.manager.LoginManager;
import cz.skaut.warehousemanager.manager.WarehouseManager;
import timber.log.Timber;


public class WarehouseApplication extends Application {

    private static SharedPreferences preferences;
    private static Context context;
    private static LoginManager loginManager;
    private static WarehouseManager warehouseManager;
    private static ItemManager itemManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            LeakCanary.install(this);

            /*Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(
                                    Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(
                                    RealmInspectorModulesProvider.builder(this).build())
                            .build());*/
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        context = getApplicationContext();

        // debug options
        //Realm.deleteRealmFile(this);
        //preferences.edit().putBoolean(C.USER_IS_LOGGED, false).commit();
        //preferences.edit().putBoolean(C.ITEMS_LOADED, false).commit();

        loginManager = new LoginManager(context);
        warehouseManager = new WarehouseManager(context);
        itemManager = new ItemManager(context);
    }

    public static Context getContext() {
        return context;
    }

    public static SharedPreferences getPrefs() {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }
        return preferences;
    }

    public static LoginManager getLoginManager() {
        if (loginManager == null) {
            loginManager = new LoginManager(getContext());
        }
        return loginManager;
    }

    public static WarehouseManager getWarehouseManager() {
        if (warehouseManager == null) {
            warehouseManager = new WarehouseManager(getContext());
        }
        return warehouseManager;
    }

    public static ItemManager getItemManager() {
        if (itemManager == null) {
            itemManager = new ItemManager(getContext());
        }
        return itemManager;
    }

}
