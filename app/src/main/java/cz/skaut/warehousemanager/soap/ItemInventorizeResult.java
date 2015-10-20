package cz.skaut.warehousemanager.soap;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name = "soap:Envelope", strict = false)
public class ItemInventorizeResult {

	@Path("soap:Body/WarehouseItemInventoryInsertResponse/WarehouseItemInventoryInsertResult")
	@Element(name = "ID")
	private long id;

	public long getId() {
		return id;
	}
}
