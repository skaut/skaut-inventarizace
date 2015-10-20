package cz.skaut.warehousemanager;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
		}

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		context = getApplicationContext();

		// debug options
		//Realm.deleteRealmFile(this);
		//preferences.edit().putBoolean(C.USER_IS_LOGGED, true).apply();
		//preferences.edit().putBoolean(C.ITEMS_LOADED, false).apply();
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
