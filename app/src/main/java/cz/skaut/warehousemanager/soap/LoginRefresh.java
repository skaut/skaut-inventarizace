package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class LoginRefresh extends BaseRequest {

	@Path("soap:Body/LoginUpdateRefresh/loginUpdateRefreshInput")
	@Element(name = "ID_Application")
	private String applicationID;

	@Path("soap:Body/LoginUpdateRefresh/loginUpdateRefreshInput")
	@Element(name = "ID")
	private String id;

	public LoginRefresh() {
		applicationID = C.APPLICATION_ID;
		id = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
	}
}
