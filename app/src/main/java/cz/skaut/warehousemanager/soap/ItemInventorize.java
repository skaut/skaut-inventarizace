package cz.skaut.warehousemanager.soap;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.entity.Inventory;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.DateTimeUtils;

@Root(name = "soap:Envelope")
public class ItemInventorize extends BaseRequest {

    @Path("soap:Body/WarehouseItemInventoryInsert/warehouseItemInventory")
    @Element(name = "ID_Application")
    private String applicationID;

    @Path("soap:Body/WarehouseItemInventoryInsert/warehouseItemInventory")
    @Element(name = "ID_Login")
    private String loginID;

    @Path("soap:Body/WarehouseItemInventoryInsert/warehouseItemInventory")
    @Element(name = "ID")
    private long id;

    @Path("soap:Body/WarehouseItemInventoryInsert/warehouseItemInventory")
    @Element(name = "ID_WarehouseItem")
    private long id2;

    @Path("soap:Body/WarehouseItemInventoryInsert/warehouseItemInventory")
    @Element(name = "Date")
    private String date;

    @Path("soap:Body/WarehouseItemInventoryInsert/warehouseItemInventory")
    @Element(name = "Note", required = false)
    private String note;

    public ItemInventorize(Inventory inventory) {
        applicationID = C.APPLICATION_ID;
        loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
        id = inventory.getItemId();
        id2 = inventory.getItemId();
        date = DateTimeUtils.getFormattedTimestamp(inventory.getDateTimestamp(), C.DATETIME_FORMAT);
        note = inventory.getNote();
    }
}
