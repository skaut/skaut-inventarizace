package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.helper.C;

@Root(name = "soap:Envelope")
public class TempFileInsert extends BaseRequest {

    @Path("soap:Body/TempFileInsert/tempFileInput")
    @Element(name = "Extension", required = false)
    private String extension;

    @Path("soap:Body/TempFileInsert/tempFileInput")
    @Element(name = "Content")
    private String content;

    @Path("soap:Body/TempFileInsert/tempFileInput")
    @Element(name = "ID_Application")
    private String applicationID;

    @Path("soap:Body/TempFileInsert/tempFileInput")
    @Element(name = "ID_Login")
    private String loginID;

    @Path("soap:Body/TempFileInsert/tempFileInput")
    @Element(name = "Filename", required = false)
    private String filename;

    public TempFileInsert(String filename, String extension, String content) {
        applicationID = C.APPLICATION_ID;
        loginID = WarehouseApplication.getPrefs().getString(C.USER_TOKEN, "");
        //this.filename = filename;
        //this.extension = extension;
        this.content = content;
    }
}
