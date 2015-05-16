package cz.skaut.warehousemanager.helper;


import android.graphics.Bitmap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cz.skaut.warehousemanager.BuildConfig;

@SuppressWarnings("SpellCheckingInspection")
public class C {

    private C() {
    }

    private static final String BASE_URL_DEV = "http://test-is.skaut.cz/";
    private static final String BASE_URL_PRODUCTION = "http://test-is.skaut.cz/"; //TODO: change production URL
    public static final String BASE_URL = BuildConfig.DEBUG ? BASE_URL_DEV : BASE_URL_PRODUCTION;

    private static final String SERVICE_NAME = "/JunakWebservice";
    public static final String MATERIAL_SERVICE = SERVICE_NAME + "/Material.asmx";
    public static final String USER_SERVICE = SERVICE_NAME + "/UserManagement.asmx";

    public static final String REQUEST_XML_HEADER = "Content-Type: text/xml; ";
    public static final String REQUEST_FORM_HEADER = "Content-Type: application/x-www-form-urlencoded; charset=utf-8";

    public static final String APPLICATION_ID = "cd5f12c7-4fde-481a-9fbb-b167b1d33152";
    public static final String LOGINDATA_1 = "Přihlásit";
    public static final String LOGINDATA_2 = "659C1D5D";
    public static final String LOGINDATA_3 = "/wEdAAal6dB3E/Azt4dIDEgFcVc6e9N9AWjfs0zS4DSCd7H2hy/PSAvxybIG70Gi7lMSo2FJFlVS07VoTH9RU2b8vaINEkz8OomGTiW7uW935QZetfxBGq6YRAod3GK/LQGDpjYGbsnaeUcBjPKae/nujU7k/yrEFB7u7vZCGEYPvQ3oHw==";
    public static final String LOGINDATA_4 = "/wEPDwUJODUyNzI1MDgzD2QWAmYPZBYEAgEPZBYCAgMPFgIeBGhyZWYFGS9Mb2dpbi9GYXZpY29ucy9qdW5hay5pY29kAgMPZBYIAgEPFgIeB1Zpc2libGVnFgICAQ8WAh4EVGV4dAUfTW9iaWxuw60gc2tsYWR5IGEgaW52ZW50YXJpemFjZWQCBA9kFgQCDQ8PFgIfAWhkZAIPDw9kFgIeDmZvcm1ub3ZhbGlkYXRlZWQCBQ8WAh8CBYwEPGR0PlRlc3RvdmFjw60gc2thdXRJUzwvZHQ+CjxkZCBjbGFzcz0iZGF0ZSI+NS40LjIwMTIgMTY6MjggfCBPbmTFmWVqIFBlxZlpbmEgLSBKZXJyeSwgdMO9bSBza2F1dElTPC9kZD4KPGRkPgogICAgViB0w6l0byB0ZXN0b3ZhY8OtIHZlcnppIHNrYXV0SVN1IHNpIG3Fr8W+ZXRlIHprb3XFoWV0IGNva29saSB2w6FzIG5hcGFkbmUuIFN5c3TDqW0gb2JzYWh1amUgdsWhZWNobnkgZnVua2NlIHN0ZWpuxJsgamFrbyBza3V0ZcSNbsO9IHNrYXV0SVMsIGFsZSB2ZcWha2Vyw6EgZGF0YSBqc291IHNtecWhbGVuw6EgYSBmaWt0aXZuw60uIAo8L2RkPgo8ZGQ+CiAgICBQb2Ryb2Juw6kgaW5mb3JtYWNlIG8gdGVzdG92YWPDrW0gc2thdXRJU3UgbmFsZXpuZXRlIHYgbsOhcG92xJtkxJsuCjwvZGQ+CjxkZCBjbGFzcz0iaHlwZXJsaW5rIj48YSBocmVmPSJodHRwOi8vaXMuc2thdXQuY3ovbmFwb3ZlZGEvVGVzdG92YWNpLXNrYXV0SVMuYXNoeCI+SmFrIG5hIHRlc3RvdmFjw60gc2thdXRJUzwvYT4gKG7DoXBvdsSbZGEpPC9kZD5kAgYPZBYCZg9kFgICAQ9kFgICAQ8WAh4Dc3JjBRxBcHBsaWNhdGlvblBhZ2VzL3NrYXV0SVMuaHRtZBgBBRpjdGwwMCRNdWx0aVZpZXdBcHBsaWNhdGlvbg8PZGZkO6l2PINwtFBILT39QG1ItmTDZ7ssGksWzNbjYBxuvMk=";

    public static final String USER_TOKEN = "UserToken";
    public static final String USER_ROLE_ID = "UserRoleID";
    public static final String USER_UNIT_ID = "UserUnitID";
    public static final String USER_ID = "UserID";
    public static final String USER_PERSON_ID = "UserPersonID";
    public static final String USER_PERSON_NAME = "UserPersonName";
    public static final String USER_ROLE = "UserRole";

    public static final String WAREHOUSES_LOADED = "WarehousesLoaded";
    public static final String ITEMS_LOADED = "ItemsLoaded";

    public static final String USER_NAME = "UserName";
    public static final String USER_PASSWORD = "UserPassword";
    public static final String USER_IS_LOGGED = "UserIsLogged";

    public static final String WAREHOUSE_INDEX = "warehouse";
    public static final String ITEM_INDEX = "item";
    public static final String ROLES_INDEX = "role";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATETIME_FORMAT_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String LOGIN_REFRESH_DATETIME_FORMAT = "d. M. yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd. MM. yyyy";

    public static final int CAMERA_REQUEST_CODE = 1;

    public static final int PHOTO_HEIGHT = 640;
    public static final String PHOTO_EXT = "jpg";
    public static final Bitmap.CompressFormat PHOTO_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final int PHOTO_COMPRESSION_QUALITY = 80;

    private static final Integer[] ALLOWED_ROLE_VALUES = new Integer[]{18, 22, 25, 44, 50, 51, 61, 103, 104};
    public static final Set<Integer> ALLOWED_ROLES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ALLOWED_ROLE_VALUES)));

    public static final String INVENTORIZE_PERIOD_DAYS = "inventorizePeriodDays";
}
