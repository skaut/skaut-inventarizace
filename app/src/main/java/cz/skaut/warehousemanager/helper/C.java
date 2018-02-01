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

    private static final boolean TEST_SKAUTS = !BuildConfig.DEBUG;

    private static final String BASE_URL_DEV = "https://test-is.skaut.cz/";
    private static final String BASE_URL_PRODUCTION = "https://is.skaut.cz/";
    public static final String BASE_URL = TEST_SKAUTS
            ? BASE_URL_DEV
            : BASE_URL_PRODUCTION;

    private static final String SERVICE_NAME = "/JunakWebservice";
    public static final String MATERIAL_SERVICE = SERVICE_NAME + "/Material.asmx";
    public static final String USER_SERVICE = SERVICE_NAME + "/UserManagement.asmx";

    public static final String REQUEST_XML_HEADER = "Content-Type: text/xml; ";
    public static final String REQUEST_FORM_HEADER = "Content-Type: application/x-www-form-urlencoded; charset=utf-8";

    private static final String APPLICATION_ID_DEV = "cd5f12c7-4fde-481a-9fbb-b167b1d33152";
    private static final String APPLICATION_ID_PRODUCTION = "38c9057a-a97d-4ad0-96ae-d5a7ac4956e9";
    public static final String APPLICATION_ID = TEST_SKAUTS
            ? APPLICATION_ID_DEV
            : APPLICATION_ID_PRODUCTION;

    public static final String LOGINDATA_1 = "Přihlásit";
    public static final String LOGINDATA_2 = "659C1D5D";
    public static final String LOGINDATA_3 = TEST_SKAUTS
            ? "/wEdAAbUPcsB8lZt0eubI3qrQasyDWTlVFFJ9YJcCdgyvbvSZJjc8X/OeYsNy9+KgXIoSOZMlO+KuBJDgBNV3/T8Jw9hbOt01hIqJPnLi/Gc3NzATMPVWCNOPgGxUCfc4D95At/dLp2PV0JbIeul+0/lTDizSHayNAuvr7ygzBWG/V354g=="
            : "/wEdAAaR3Gp1aUCoyHcRtOm+Pcz+DWTlVFFJ9YJcCdgyvbvSZJjc8X/OeYsNy9+KgXIoSOZMlO+KuBJDgBNV3/T8Jw9hbOt01hIqJPnLi/Gc3NzATMPVWCNOPgGxUCfc4D95At80f83QsKvAR6dhrxigw8tgphRp+EvIdf5SuXsu5/EtKQ==";

    public static final String LOGINDATA_4 = TEST_SKAUTS
            ? "/wEPDwUKMTQ5OTgxNjA5OQ9kFgJmD2QWBAIBD2QWAgIIDxYCHgRocmVmBRkvTG9naW4vRmF2aWNvbnMvanVuYWsuaWNvZAIDD2QWBAICDxYCHglpbm5lcmh0bWwFGHNrYXV0SVMgKFRFU1RPVkFDJiMyMDU7KWQCAw9kFgQCBQ8PFgIeBE1vZGULKWhKdW5hay5VdGlscy5Db250cm9scy5Gb3JtTW9kZSwgSnVuYWsuVXRpbHMsIFZlcnNpb249MS4wLjY1OTEuMjkyNjQsIEN1bHR1cmU9bmV1dHJhbCwgUHVibGljS2V5VG9rZW49bnVsbABkFggCAQ8PFgQeCENzc0NsYXNzBRlmb3JtLWNvbnRyb2wgZm9ybS1jb250cm9sHgRfIVNCAgJkZAIDDw8WBB8DBRlmb3JtLWNvbnRyb2wgZm9ybS1jb250cm9sHwQCAmRkAgUPFgIeB1Zpc2libGVoZAIHD2QWAgIBDw8WBh8DBQ9idG4gYnRuLXByaW1hcnkeBEljb24FDWZhIGZhLXNpZ24taW4fBAICZGQCCA9kFgICAQ8WAh8FaGRkDq+ghP4oBZUjuPr16eO3aF+1RA6sRMp5wpagYadzuXE="
            : "/wEPDwUKMTQ5OTgxNjA5OQ9kFgJmD2QWBAIBD2QWAgIIDxYCHgRocmVmBRkvTG9naW4vRmF2aWNvbnMvanVuYWsuaWNvZAIDD2QWBAICDxYCHglpbm5lcmh0bWwFC3NrYXV0SVMgd2ViZAIDD2QWBAIFDw8WAh4ETW9kZQspaEp1bmFrLlV0aWxzLkNvbnRyb2xzLkZvcm1Nb2RlLCBKdW5hay5VdGlscywgVmVyc2lvbj0xLjAuNjU0OC4yMjk3MywgQ3VsdHVyZT1uZXV0cmFsLCBQdWJsaWNLZXlUb2tlbj1udWxsAGQWCAIBDw8WBB4IQ3NzQ2xhc3MFGWZvcm0tY29udHJvbCBmb3JtLWNvbnRyb2weBF8hU0ICAmRkAgMPDxYEHwMFGWZvcm0tY29udHJvbCBmb3JtLWNvbnRyb2wfBAICZGQCBQ8WAh4HVmlzaWJsZWhkAgcPZBYCAgEPDxYGHwMFD2J0biBidG4tcHJpbWFyeR4ESWNvbgUNZmEgZmEtc2lnbi1pbh8EAgJkZAIID2QWAgIBDxYCHwVoZGQXxbHHPGyIASuY5nLf6kRnBC5h7ZmFwOCuqxzqSG6q4A==";
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
