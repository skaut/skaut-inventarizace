package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class UserDetail extends BaseRequest {

    @Path("soap:Body/UserDetail/userDetailInput")
    @Element(name = "ID_Login")
    private String loginID;

    @Path("soap:Body/UserDetail/userDetailInput")
    @Element(name = "ID_Application")
    private String applicationID;

    public UserDetail() {
        applicationID = C.APPLICATION_ID;
        loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
    }
}
