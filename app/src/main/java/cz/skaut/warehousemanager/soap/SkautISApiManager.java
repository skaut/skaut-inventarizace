package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.util.Map;

import cz.skaut.warehousemanager.helper.C;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public class SkautISApiManager {

	//@formatter:off, byl public
	 public interface SkautISWarehouseApi {

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

	public interface SkautISUserApi {

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

	 public interface SkautISLoginApi {

		@POST("/Login/")
		@FormUrlEncoded
		Observable<LoginResponse> login(
				@Query("appid") String appId,
				@FieldMap Map<String, String> params);
	}
	//@formatter:on

	private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
			.setEndpoint(C.BASE_URL)
			.setConverter(new SimpleXMLConverter(new Persister(new AnnotationStrategy())))
			.setLogLevel(RestAdapter.LogLevel.BASIC)
			.build();

	private static final SkautISLoginApi LOGIN_API = REST_ADAPTER.create(SkautISLoginApi.class);

	private static final SkautISWarehouseApi WAREHOUSE_API = REST_ADAPTER.create(SkautISWarehouseApi.class);

	private static final SkautISUserApi USER_API = REST_ADAPTER.create(SkautISUserApi.class);

	public static SkautISLoginApi getLoginApi() {
		return LOGIN_API;
	}

	public static SkautISWarehouseApi getWarehouseApi() {
		return WAREHOUSE_API;
	}

	public static SkautISUserApi getUserApi() {
		return USER_API;
	}

}
