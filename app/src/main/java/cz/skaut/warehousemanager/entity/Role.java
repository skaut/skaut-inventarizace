package cz.skaut.warehousemanager.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Role", strict = false)
public class Role implements Parcelable {

	@Element(name = "ID")
	private long id;

	@Element(name = "ID_Role")
	private int roleID;

	@Element(name = "Unit")
	private String unitName;

	@Element(name = "Role")
	private String roleName;

	public Role() {
	}

	public long getId() {
		return id;
	}

	public int getRoleID() {
		return roleID;
	}

	public String getUnitName() {
		return unitName;
	}

	public String getRoleName() {
		return roleName;
	}

	@Override
	public String toString() {
		return roleName + " " + unitName;
	}

	private Role(Parcel in) {
		id = in.readLong();
		roleID = in.readInt();
		unitName = in.readString();
		roleName = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(roleID);
		dest.writeString(unitName);
		dest.writeString(roleName);
	}

	//@SuppressWarnings("unused")
	public static final Parcelable.Creator<Role> CREATOR = new Parcelable.Creator<Role>() {
		@Override
		public Role createFromParcel(Parcel in) {
			return new Role(in);
		}

		@Override
		public Role[] newArray(int size) {
			return new Role[size];
		}
	};
}
