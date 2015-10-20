package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name = "soap:Envelope", strict = false)
public class ItemPhotoResult {

	@Path("soap:Body/WarehouseItemDetailPhotoResponse/WarehouseItemDetailPhotoResult")
	@Element(name = "PhotoContent", required = false)
	private String photoData;

	@Path("soap:Body/WarehouseItemDetailPhotoResponse/WarehouseItemDetailPhotoResult")
	@Element(name = "PhotoExtension", required = false)
	private String photoExtension;

	public String getPhotoData() {
		return photoData;
	}

	public String getPhotoExtension() {
		return photoExtension;
	}
}
