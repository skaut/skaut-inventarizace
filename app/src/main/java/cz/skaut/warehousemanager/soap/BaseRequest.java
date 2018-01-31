package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;

@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/"),
        @Namespace(reference = "https://is.skaut.cz/")
})
public abstract class BaseRequest {
}