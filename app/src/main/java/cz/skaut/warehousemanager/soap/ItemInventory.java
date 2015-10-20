package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class ItemInventory extends BaseRequest {

	@Path("soap:Body/WarehouseItemInventoryAll/warehouseItemInventoryAllInput")
	@Element(name = "ID_Application")
	private String applicationID;

	@Path("soap:Body/WarehouseItemInventoryAll/warehouseItemInventoryAllInput")
	@Element(name = "ID_WarehouseItem")
	private long id;

	@Path("soap:Body/WarehouseItemInventoryAll/warehouseItemInventoryAllInput")
	@Element(name = "ID_Login")
	private String loginID;

	public ItemInventory(long itemId) {
		applicationID = C.APPLICATION_ID;
		id = itemId;
		loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
	}
}
