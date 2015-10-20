package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

import cz.skaut.warehousemanager.entity.Item;

@Root(name = "soap:Envelope", strict = false)
public class ItemAllResult {

	@Path("soap:Body/WarehouseItemAllResponse")
	@ElementList(name = "WarehouseItemAllResult", entry = "WarehouseItemAllOutput")
	private List<Item> itemList;

	public List<Item> getItemList() {
		return itemList;
	}

	@Override
	public String toString() {
		return "ItemAllResult{" +
				"itemList=" + itemList +
				'}';
	}
}
