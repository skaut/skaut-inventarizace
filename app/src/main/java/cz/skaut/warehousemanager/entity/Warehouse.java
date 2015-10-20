package cz.skaut.warehousemanager.entity;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Root(name = "Warehouse", strict = false)
public class Warehouse extends RealmObject {

	@PrimaryKey
	@Element(name = "ID")
	private long id;

	@Element(name = "DisplayName")
	private String name;

	@Element(name = "ID_WarehouseMain", required = false)
	private long idParentWarehouse;

	private Warehouse parentWarehouse;

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

	public long getIdParentWarehouse() {
		return idParentWarehouse;
	}

	public void setIdParentWarehouse(long idParentWarehouse) {
		this.idParentWarehouse = idParentWarehouse;
	}

	public Warehouse getParentWarehouse() {
		return parentWarehouse;
	}

	public void setParentWarehouse(Warehouse parentWarehouse) {
		this.parentWarehouse = parentWarehouse;
	}
}
