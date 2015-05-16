package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name = "soap:Envelope", strict = false)
public class RoleUpdateResult extends BaseRequest {

    @Path("soap:Body/LoginUpdateResponse/LoginUpdateResult")
    @Element(name = "ID_Unit")
    private long unitID;

    public long getUnitID() {
        return unitID;
    }
}