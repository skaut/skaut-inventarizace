package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Input {

    @Attribute
    private String value;

    public String getValue() {
        return value;
    }
}