package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name = "soap:Envelope", strict = false)
public class UserDetailResult extends BaseRequest {

	@Path("soap:Body/UserDetailResponse/UserDetailResult")
	@Element(name = "ID")
	private String userID;

	@Path("soap:Body/UserDetailResponse/UserDetailResult")
	@Element(name = "ID_Person")
	private String personID;

	@Path("soap:Body/UserDetailResponse/UserDetailResult")
	@Element(name = "Person")
	private String personName;

	public String getUserID() {
		return userID;
	}

	public String getPersonID() {
		return personID;
	}

	public String getPersonName() {
		return personName;
	}

	@Override
	public String toString() {
		return "UserDetailResult{" +
				"userID='" + userID + '\'' +
				", personID='" + personID + '\'' +
				", personName='" + personName + '\'' +
				'}';
	}
}
