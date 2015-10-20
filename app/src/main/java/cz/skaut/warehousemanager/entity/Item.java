package cz.skaut.warehousemanager.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

@Root(name = "Item", strict = false)
public class Item extends RealmObject {

	@PrimaryKey
	@Element(name = "ID")
	private long id;

	@Element(name = "DisplayName")
	private String name;

	private Warehouse warehouse;

	@Ignore
	@Element(name = "ID_Warehouse")
	private long idWarehouse;

	@Element(name = "InventoryNumber", required = false)
	private String inventoryNumber;

	@Element(name = "Description", required = false)
	private String description;

	@Element(name = "PurchasePrice", required = false)
	private String purchasePrice;

	@Element(name = "InWarehouse", required = false)
	private boolean isInWarehouse;

	@Element(name = "PurchaseDate", required = false)
	private String purchaseDate;

	private Inventory latestInventory;

	private boolean synced;

	private String photo;

	public Item() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public long getIdWarehouse() {
		return idWarehouse;
	}

	public void setIdWarehouse(long idWarehouse) {
		this.idWarehouse = idWarehouse;
	}

	public String getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public boolean isInWarehouse() {
		return isInWarehouse;
	}

	public void setIsInWarehouse(boolean isInWarehouse) {
		this.isInWarehouse = isInWarehouse;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Inventory getLatestInventory() {
		return latestInventory;
	}

	public void setLatestInventory(Inventory latestInventory) {
		this.latestInventory = latestInventory;
	}

	public boolean isSynced() {
		return synced;
	}

	public void setSynced(boolean synced) {
		this.synced = synced;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
}