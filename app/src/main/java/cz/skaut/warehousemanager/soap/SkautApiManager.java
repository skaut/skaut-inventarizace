package cz.skaut.warehousemanager.soap;


import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.util.Map;

import cz.skaut.warehousemanager.helper.C;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public class SkautApiManager {

    public interface SkautWarehouseApi {

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.MATERIAL_SERVICE)
        Observable<WarehouseAllResult> getAllWarehouses(@Body WarehouseAll body);

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.MATERIAL_SERVICE)
        Observable<ItemAllResult> getAllItems(@Body ItemAll body);

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.MATERIAL_SERVICE)
        Observable<ItemPhotoResult> getItemPhoto(@Body ItemPhoto body);

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.MATERIAL_SERVICE)
        Observable<ItemInventoryResult> getItemInventory(@Body ItemInventory body);

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.MATERIAL_SERVICE)
        Observable<ItemInventorizeResult> itemInventorize(@Body ItemInventorize body);

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.USER_SERVICE)
        Observable<TempFileInsertResult> uploadPhoto(@Body TempFileInsert body);
    }

    public interface SkautUserApi {

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.USER_SERVICE)
        Observable<UserDetailResult> getUserDetail(@Body UserDetail body);

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.USER_SERVICE)
        Observable<RoleAllResult> getRoles(@Body RoleAll body);

        @Headers(C.REQUEST_XML_HEADER)
        @POST(C.USER_SERVICE)
        Observable<RoleUpdateResult> changeRole(@Body RoleUpdate body);
    }

    public interface SkautLoginApi {

        @Headers(C.REQUEST_FORM_HEADER)
        @POST("/Login/")
        @FormUrlEncoded
        Observable<String> login(@Query("appid") String appId,
                                 @FieldMap Map<String, String> params);
    }

    private static final OkClient OK_CLIENT;

    static {
        //OkHttpClient client = new OkHttpClient();
        //client.networkInterceptors().add(new StethoInterceptor());
        OK_CLIENT = new OkClient();
    }

    private static final RestAdapter REST_LOGIN_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(C.BASE_URL)
            .setClient(OK_CLIENT)
            .setConverter(new StringConverter())
                    //.setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    private static final SkautLoginApi LOGIN_API = REST_LOGIN_ADAPTER.create(SkautLoginApi.class);

    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(C.BASE_URL)
            .setClient(OK_CLIENT)
            .setConverter(new SimpleXMLConverter(new Persister(new AnnotationStrategy())))
                    //.setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    private static final SkautWarehouseApi WAREHOUSE_API = REST_ADAPTER.create(SkautWarehouseApi.class);

    private static final SkautUserApi USER_API = REST_ADAPTER.create(SkautUserApi.class);

    public static SkautLoginApi getLoginApi() {
        return LOGIN_API;
    }

    public static SkautWarehouseApi getWarehouseApi() {
        return WAREHOUSE_API;
    }

    public static SkautUserApi getUserApi() {
        return USER_API;
    }

}
