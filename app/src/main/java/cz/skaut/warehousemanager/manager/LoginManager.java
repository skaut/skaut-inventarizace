package cz.skaut.warehousemanager.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.skaut.warehousemanager.entity.Role;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.soap.LoginResponse;
import cz.skaut.warehousemanager.soap.RoleAll;
import cz.skaut.warehousemanager.soap.RoleUpdate;
import cz.skaut.warehousemanager.soap.SkautApiManager;
import cz.skaut.warehousemanager.soap.UserDetail;
import cz.skaut.warehousemanager.soap.UserDetailResult;
import rx.Observable;
import timber.log.Timber;

public class LoginManager {

    private final SharedPreferences prefs;

    public LoginManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public Observable<List<Role>> login(final String username, final String password) {
        final SkautApiManager.SkautUserApi userApi = SkautApiManager.getUserApi();

        Map<String, String> params = prepareLoginData(username, password);

        return SkautApiManager.getLoginApi().login(C.APPLICATION_ID, params)
                .flatMap((LoginResponse response) -> {
                    Timber.d("Token: " + response.getToken());
                    Timber.d("Role: " + response.getRole());
                    Timber.d("Unit ID: " + response.getUnit());

                    prefs.edit().putString(C.USER_NAME, username)
                            .putString(C.USER_PASSWORD, password)
                            .putString(C.USER_TOKEN, response.getToken())
                            .putLong(C.USER_ROLE_ID, response.getRole())
                            .putLong(C.USER_UNIT_ID, response.getUnit())
                            .putBoolean(C.WAREHOUSES_LOADED, false)
                            .putBoolean(C.ITEMS_LOADED, false)
                            .putBoolean(C.SYNC_NEEDED, false)
                            .apply();
                    Timber.d("Login successful");
                    return userApi.getUserDetail(new UserDetail());
                })
                .flatMap((UserDetailResult userDetailResult) -> {
                    prefs.edit().putString(C.USER_ID, userDetailResult.getUserID())
                            .putString(C.USER_PERSON_ID, userDetailResult.getPersonID())
                            .putString(C.USER_PERSON_NAME, userDetailResult.getPersonName()).apply();
                    Timber.d("User ID: " + userDetailResult.getUserID());
                    Timber.d("Person ID: " + userDetailResult.getPersonID());
                    Timber.d("Person name: " + userDetailResult.getPersonName());
                    return userApi.getRoles(new RoleAll());
                })
                .flatMap(roleAllResult -> Observable.from(roleAllResult.getRoleList()))
                .filter(role -> C.ALLOWED_ROLES.contains(role.getRoleID()))
                .toList();
    }

    public Observable<Object> refreshLogin() {
        Map<String, String> params = prepareLoginData(prefs.getString(C.USER_NAME, ""), prefs.getString(C.USER_PASSWORD, ""));

        // persist this in case we need to switch to old role
        long oldRoleId = prefs.getLong(C.USER_ROLE_ID, 0);

        return SkautApiManager.getLoginApi().login(C.APPLICATION_ID, params)
                .flatMap((LoginResponse response) -> {
                    //LoginResponse loginResponse = parseUserData(response);

                    prefs.edit().putString(C.USER_TOKEN, response.getToken()).apply();

                    return refreshRole(oldRoleId, response.getRole());
                });
    }

    private Observable<Object> refreshRole(long oldRoleId, long newRoleId) {
        if (oldRoleId == newRoleId) {
            Timber.d("refreshing, but same role, doing nothing");
            return Observable.just(new Object());
        } else {
            Timber.d("refreshing, switching to new role");
            RoleUpdate request = new RoleUpdate(oldRoleId);
            return SkautApiManager.getUserApi().changeRole(request)
                    .flatMap(roleUpdateResult -> {
                        Timber.d("refreshing, Role update success");
                        return Observable.just(new Object());
                    });
        }
    }

    public Observable<Object> chooseRole(Role role) {
        long currentRoleId = prefs.getLong(C.USER_ROLE_ID, 0);
        if (role.getId() == currentRoleId) {
            Timber.d("same role, doing nothing");
            prefs.edit().putBoolean(C.USER_IS_LOGGED, true).apply();
            return Observable.empty();
        } else {
            RoleUpdate request = new RoleUpdate(role.getId());
            return SkautApiManager.getUserApi().changeRole(request)
                    .flatMap(roleUpdateResult -> {
                        Timber.d("Role update success");
                        prefs.edit().putBoolean(C.USER_IS_LOGGED, true)
                                .putLong(C.USER_UNIT_ID, roleUpdateResult.getUnitID())
                                .putString(C.USER_ROLE, role.getUnitName()).apply();
                        return Observable.empty();
                    });
        }
    }

    public void logout() {
        prefs.edit().putBoolean(C.USER_IS_LOGGED, false).apply();
    }

    private Map<String, String> prepareLoginData(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("ctl00$txtUserName", username);
        params.put("ctl00$txtPassword", password);
        params.put("ctl00$btnLogin", C.LOGINDATA_1);
        params.put("__EVENTVALIDATION", C.LOGINDATA_3);
        params.put("__VIEWSTATE", C.LOGINDATA_4);
        params.put("__VIEWSTATEGENERATOR", C.LOGINDATA_2);

        return params;
    }
}
