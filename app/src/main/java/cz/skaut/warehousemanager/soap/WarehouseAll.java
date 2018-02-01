package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class WarehouseAll extends BaseRequest {

    @Path("soap:Body/WarehouseAll/warehouseAllInput")
    @Element(name = "ID_Unit")
    private long unitID;

    @Path("soap:Body/WarehouseAll/warehouseAllInput")
    @Element(name = "ID_Application")
    private String applicationID;

    @Path("soap:Body/WarehouseAll/warehouseAllInput")
    @Element(name = "ID_Login")
    private String loginID;

    public WarehouseAll() {
        applicationID = C.APPLICATION_ID;
        loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
        unitID = WarehouseApplication.getPrefs().getLong(C.USER_UNIT_ID, 0);
    }
}
