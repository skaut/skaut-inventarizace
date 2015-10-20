package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class RoleAll extends BaseRequest {

	@Path("soap:Body/UserRoleAll/userRoleAllInput")
	@Element(name = "ID_Login")
	private String loginID;

	@Path("soap:Body/UserRoleAll/userRoleAllInput")
	@Element(name = "ID_Application")
	private String applicationID;

	@Path("soap:Body/UserRoleAll/userRoleAllInput")
	@Element(name = "ID_User")
	private String userID;

	@Path("soap:Body/UserRoleAll/userRoleAllInput")
	@Element(name = "IsActive")
	private boolean isActive;

	public RoleAll() {
		applicationID = C.APPLICATION_ID;
		loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
		userID = WarehouseApplication.getPrefs().getString(C.USER_ID, "");
		isActive = true;
	}
}
