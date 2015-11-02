package cz.skaut.warehousemanager.helper


import android.graphics.Bitmap
import cz.skaut.warehousemanager.BuildConfig

@SuppressWarnings("SpellCheckingInspection")
object C {

    private val BASE_URL_DEV = "http://test-is.skaut.cz/"
    private val BASE_URL_PRODUCTION = "http://test-is.skaut.cz/" //TODO: change production URL
    val BASE_URL = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PRODUCTION

    private val SERVICE_NAME = "/JunakWebservice"
    val MATERIAL_SERVICE = SERVICE_NAME + "/Material.asmx"
    val USER_SERVICE = SERVICE_NAME + "/UserManagement.asmx"

    val REQUEST_XML_HEADER = "Content-Type: text/xml;"

    val APPLICATION_ID = "cd5f12c7-4fde-481a-9fbb-b167b1d33152"
    val LOGINDATA_1 = "Přihlásit"
    val LOGINDATA_2 = "659C1D5D"
    val LOGINDATA_3 = "/wEdAAal6dB3E/Azt4dIDEgFcVc6e9N9AWjfs0zS4DSCd7H2hy/PSAvxybIG70Gi7lMSo2FJFlVS07VoTH9RU2b8vaINEkz8OomGTiW7uW935QZetfxBGq6YRAod3GK/LQGDpjYGbsnaeUcBjPKae/nujU7k/yrEFB7u7vZCGEYPvQ3oHw=="
    val LOGINDATA_4 = "/wEPDwUJODUyNzI1MDgzD2QWAmYPZBYEAgEPZBYCAgMPFgIeBGhyZWYFGS9Mb2dpbi9GYXZpY29ucy9qdW5hay5pY29kAgMPZBYIAgEPFgIeB1Zpc2libGVnFgICAQ8WAh4EVGV4dAUfTW9iaWxuw60gc2tsYWR5IGEgaW52ZW50YXJpemFjZWQCBA9kFgQCDQ8PFgIfAWhkZAIPDw9kFgIeDmZvcm1ub3ZhbGlkYXRlZWQCBQ8WAh8CBYwEPGR0PlRlc3RvdmFjw60gc2thdXRJUzwvZHQ+CjxkZCBjbGFzcz0iZGF0ZSI+NS40LjIwMTIgMTY6MjggfCBPbmTFmWVqIFBlxZlpbmEgLSBKZXJyeSwgdMO9bSBza2F1dElTPC9kZD4KPGRkPgogICAgViB0w6l0byB0ZXN0b3ZhY8OtIHZlcnppIHNrYXV0SVN1IHNpIG3Fr8W+ZXRlIHprb3XFoWV0IGNva29saSB2w6FzIG5hcGFkbmUuIFN5c3TDqW0gb2JzYWh1amUgdsWhZWNobnkgZnVua2NlIHN0ZWpuxJsgamFrbyBza3V0ZcSNbsO9IHNrYXV0SVMsIGFsZSB2ZcWha2Vyw6EgZGF0YSBqc291IHNtecWhbGVuw6EgYSBmaWt0aXZuw60uIAo8L2RkPgo8ZGQ+CiAgICBQb2Ryb2Juw6kgaW5mb3JtYWNlIG8gdGVzdG92YWPDrW0gc2thdXRJU3UgbmFsZXpuZXRlIHYgbsOhcG92xJtkxJsuCjwvZGQ+CjxkZCBjbGFzcz0iaHlwZXJsaW5rIj48YSBocmVmPSJodHRwOi8vaXMuc2thdXQuY3ovbmFwb3ZlZGEvVGVzdG92YWNpLXNrYXV0SVMuYXNoeCI+SmFrIG5hIHRlc3RvdmFjw60gc2thdXRJUzwvYT4gKG7DoXBvdsSbZGEpPC9kZD5kAgYPZBYCZg9kFgICAQ9kFgICAQ8WAh4Dc3JjBRxBcHBsaWNhdGlvblBhZ2VzL3NrYXV0SVMuaHRtZBgBBRpjdGwwMCRNdWx0aVZpZXdBcHBsaWNhdGlvbg8PZGZkO6l2PINwtFBILT39QG1ItmTDZ7ssGksWzNbjYBxuvMk="

    val USER_TOKEN = "UserToken"
    val USER_ROLE_ID = "UserRoleID"
    val USER_UNIT_ID = "UserUnitID"
    val USER_ID = "UserID"
    val USER_PERSON_ID = "UserPersonID"
    val USER_PERSON_NAME = "UserPersonName"
    val USER_ROLE = "UserRole"

    val WAREHOUSES_LOADED = "WarehousesLoaded"
    val ITEMS_LOADED = "ItemsLoaded"
    val SYNC_NEEDED = "SyncNeeded"

    val USER_NAME = "UserName"
    val USER_PASSWORD = "UserPassword"
    val USER_IS_LOGGED = "UserIsLogged"

    val WAREHOUSE_INDEX = "warehouse"
    val ITEM_INDEX = "item"
    val ROLES_INDEX = "role"

    val DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    val DATETIME_FORMAT_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    val LOGIN_REFRESH_DATETIME_FORMAT = "d. M. yyyy HH:mm:ss"
    val DATE_FORMAT = "dd. MM. yyyy"

    val CAMERA_REQUEST_CODE = 1

    val PHOTO_HEIGHT = 640
    val PHOTO_EXT = "jpg"
    val PHOTO_FORMAT: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    val PHOTO_COMPRESSION_QUALITY = 80

    val ALLOWED_ROLES = setOf(18, 22, 25, 44, 50, 51, 61, 103, 104)

    val INVENTORIZE_PERIOD_DAYS = "inventorizePeriodDays"
}// no instances
