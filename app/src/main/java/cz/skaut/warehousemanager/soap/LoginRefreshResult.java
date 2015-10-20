package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name = "soap:Envelope", strict = false)
public class LoginRefreshResult {

	@Path("soap:Body/LoginUpdateRefreshResponse/LoginUpdateRefreshResult")
	@Element(name = "DateLogout")
	private String dateLogout;

	public String getDateLogout() {
		return dateLogout;
	}
}
