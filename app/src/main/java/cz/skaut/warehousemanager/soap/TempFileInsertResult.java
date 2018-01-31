package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name = "soap:Envelope", strict = false)
public class TempFileInsertResult {

    @Path("soap:Body/TempFileInsertResponse")
    @Element(name = "TempFileInsertResult")
    private long guid;

    public long getGuid() {
        return guid;
    }
}
