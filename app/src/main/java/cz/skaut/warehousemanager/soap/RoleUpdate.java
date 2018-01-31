package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class RoleUpdate extends BaseRequest {

    @Path("soap:Body/LoginUpdate/loginUpdateInput")
    @Element(name = "ID_Application")
    private String applicationID;

    @Path("soap:Body/LoginUpdate/loginUpdateInput")
    @Element(name = "ID")
    private String loginID;

    @Path("soap:Body/LoginUpdate/loginUpdateInput")
    @Element(name = "ID_UserRole")
    private long roleID;

    public RoleUpdate(long roleID) {
        applicationID = C.APPLICATION_ID;
        loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
        this.roleID = roleID;
    }
}
