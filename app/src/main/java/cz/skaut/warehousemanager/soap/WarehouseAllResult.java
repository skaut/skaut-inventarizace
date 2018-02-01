package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

import cz.skaut.warehousemanager.entity.Warehouse;

@Root(name = "soap:Envelope", strict = false)
public class WarehouseAllResult {

    @Path("soap:Body/WarehouseAllResponse")
    @ElementList(name = "WarehouseAllResult", entry = "WarehouseAllOutput")
    private List<Warehouse> warehouseList;

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    @Override
    public String toString() {
        return "WarehouseAllOutput{" +
                "warehouseList=" + warehouseList +
                '}';
    }
}
