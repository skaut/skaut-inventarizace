package cz.skaut.warehousemanager.soap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "html", strict = false)
public class LoginResponse {

	@Path("body")
	@ElementList(name = "form", entry = "input")
	private List<Input> data;

	public String getToken() {
		return data.get(0).getValue();
	}

	public long getRole() {
		return Long.valueOf(data.get(1).getValue());
	}

	public long getUnit() {
		return Long.valueOf(data.get(2).getValue());
	}
}
