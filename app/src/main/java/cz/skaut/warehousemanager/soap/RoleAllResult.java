package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

import cz.skaut.warehousemanager.entity.Role;


@Root(name = "soap:Envelope", strict = false)
public class RoleAllResult {

	@Path("soap:Body/UserRoleAllResponse")
	@ElementList(name = "UserRoleAllResult", entry = "UserRoleAllOutput")
	private List<Role> roleList;

	public List<Role> getRoleList() {
		return roleList;
	}

	@Override
	public String toString() {
		return "RoleAllResult{" +
				"roleList=" + roleList +
				'}';
	}
}
