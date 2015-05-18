package cz.skaut.warehousemanager.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.skaut.warehousemanager.entity.Role;
import cz.skaut.warehousemanager.helper.C;
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
                .flatMap((String response) -> {
                    prefs.edit().putString(C.USER_NAME, username).putString(C.USER_PASSWORD, password).commit();
                    LoginResponse loginResponse = parseUserData(response);
                    prefs.edit().putString(C.USER_TOKEN, loginResponse.getToken())
                            .putLong(C.USER_ROLE_ID, loginResponse.getRole())
                            .putLong(C.USER_UNIT_ID, loginResponse.getUnit())
                            .putBoolean(C.USER_IS_LOGGED, true)
                            .putBoolean(C.WAREHOUSES_LOADED, false)
                            .putBoolean(C.ITEMS_LOADED, false)
                            .commit();
                    Timber.d("Login successful");
                    return userApi.getUserDetail(new UserDetail());
                })
                .flatMap((UserDetailResult userDetailResult) -> {
                    prefs.edit().putString(C.USER_ID, userDetailResult.getUserID())
                            .putString(C.USER_PERSON_ID, userDetailResult.getPersonID())
                            .putString(C.USER_PERSON_NAME, userDetailResult.getPersonName()).commit();
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
                .flatMap((String response) -> {
                    LoginResponse loginResponse = parseUserData(response);

                    prefs.edit().putString(C.USER_TOKEN, loginResponse.getToken())
                            .commit();

                    return refreshRole(oldRoleId, loginResponse.getRole());
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
            return Observable.empty();
        } else {
            RoleUpdate request = new RoleUpdate(role.getId());
            return SkautApiManager.getUserApi().changeRole(request)
                    .flatMap(roleUpdateResult -> {
                        Timber.d("Role update success");
                        prefs.edit().putLong(C.USER_UNIT_ID, roleUpdateResult.getUnitID())
                                .putString(C.USER_ROLE, role.getUnitName()).commit();
                        return Observable.empty();
                    });
        }
    }

    public void logout() {
        prefs.edit().putBoolean(C.USER_IS_LOGGED, false).commit();
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

    private LoginResponse parseUserData(String data) {

        Serializer ser = new Persister();
        LoginResponse response;
        try {
            response = ser.read(LoginResponse.class, data);
        } catch (Exception e) {
            throw new RuntimeException("Login deserialization failed", e);
        }

        Timber.d("Token: " + response.getToken());
        Timber.d("Role: " + response.getRole());
        Timber.d("Unit ID: " + response.getUnit());

        return response;
    }

    @Root(name = "html", strict = false)
    private static class LoginResponse {

        @Path("body")
        @ElementList(name = "form", entry = "input")
        private List<Input> data;

        public String getToken() {
            return data.get(0).getValue();
        }

        public long getRole() {
            return Long.valueOf(data.get(1).getValue());
        }

        public long getUnit() {
            return Long.valueOf(data.get(2).getValue());
        }
    }

    @Root(strict = false)
    private static class Input {

        @Attribute
        private String value;

        public String getValue() {
            return value;
        }
    }
}
