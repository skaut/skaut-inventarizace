package cz.skaut.warehousemanager.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

@Root(name = "Inventory", strict = false)
public class Inventory extends RealmObject {

    @Element(name = "ID")
    private long id;

    @Element(name = "ID_WarehouseItem")
    private long itemId;

    @Element(name = "Person")
    private String person;

    @Element(name = "Note", required = false)
    private String note;

    @Ignore
    @Element(name = "Date")
    private String date;

    private long dateTimestamp;

    private boolean synced;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public long getDateTimestamp() {
        return dateTimestamp;
    }

    public void setDateTimestamp(long dateTimestamp) {
        this.dateTimestamp = dateTimestamp;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
