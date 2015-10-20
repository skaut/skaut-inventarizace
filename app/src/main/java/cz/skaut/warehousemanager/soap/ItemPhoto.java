package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class ItemPhoto extends BaseRequest {

	@Path("soap:Body/WarehouseItemDetailPhoto/warehouseItemDetailPhotoInput")
	@Element(name = "ID_Application")
	private String applicationID;

	@Path("soap:Body/WarehouseItemDetailPhoto/warehouseItemDetailPhotoInput")
	@Element(name = "ID_Login")
	private String loginID;

	@Path("soap:Body/WarehouseItemDetailPhoto/warehouseItemDetailPhotoInput")
	@Element(name = "ID")
	private long id;

	@Path("soap:Body/WarehouseItemDetailPhoto/warehouseItemDetailPhotoInput")
	@Element(name = "Size")
	private String size;

	public ItemPhoto(long id) {
		applicationID = C.APPLICATION_ID;
		loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
		this.id = id;
		this.size = "big";
	}
}
