package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

import cz.skaut.warehousemanager.entity.Inventory;

@Root(name = "soap:Envelope", strict = false)
public class ItemInventoryResult {

	@Path("soap:Body/WarehouseItemInventoryAllResponse")
	@ElementList(name = "WarehouseItemInventoryAllResult", entry = "WarehouseItemInventoryAllOutput", required = false)
	private List<Inventory> inventoryList;

	public Inventory getLatestInventory() {
		if (inventoryList == null || inventoryList.isEmpty()) {
			return null;
		}
		Inventory latestInventory = inventoryList.get(0);
		for (Inventory i : inventoryList) {
			if (i.getId() > latestInventory.getId()) {
				latestInventory = i;
			}
		}
		return latestInventory;
	}

	@Override
	public String toString() {
		return "ItemInventoryResult{" +
				"inventoryList=" + inventoryList +
				'}';
	}
}
