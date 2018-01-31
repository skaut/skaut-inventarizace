package cz.skaut.warehousemanager.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.entity.Inventory;
import cz.skaut.warehousemanager.entity.Item;
import cz.skaut.warehousemanager.entity.Warehouse;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.DateTimeUtils;
import cz.skaut.warehousemanager.soap.ItemAll;
import cz.skaut.warehousemanager.soap.ItemAllResult;
import cz.skaut.warehousemanager.soap.ItemInventorize;
import cz.skaut.warehousemanager.soap.ItemInventory;
import cz.skaut.warehousemanager.soap.ItemInventoryResult;
import cz.skaut.warehousemanager.soap.ItemPhoto;
import cz.skaut.warehousemanager.soap.ItemPhotoResult;
import cz.skaut.warehousemanager.soap.SkautApiManager;
import cz.skaut.warehousemanager.soap.TempFileInsert;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ItemManager {

    private final Context context;
    private final Realm realm;
    private final SharedPreferences prefs;

    public ItemManager(Context ctx) {
        context = ctx.getApplicationContext();
        realm = Realm.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Item getItem(long id) {
        return realm.where(Item.class).equalTo("id", id).findFirst();
    }

    private Observable<List<Item>> getAllItems(long warehouseId) {
        List<Item> items = realm.where(Item.class).equalTo("warehouse.id", warehouseId).findAllSorted("id");
        return Observable.just(items);
    }

    public List<Item> getAllUninventorizedItems(long warehouseId) {
        // TODO: change this to reflect settings
        return realm.where(Item.class).equalTo("warehouse.id", warehouseId).equalTo("latestInventory.synced", true).findAllSorted("id");
    }

    public Observable<List<Item>> getItems(final long warehouseId) {
        // return items from database if they are already loaded
        if (prefs.getBoolean(C.ITEMS_LOADED, false)) {
            return getAllItems(warehouseId);
        } else {
            ItemAll request = new ItemAll();
            return SkautApiManager.getWarehouseApi().getAllItems(request)
                    .flatMap((ItemAllResult itemAllResult) -> {
                        Timber.i("Item All: " + itemAllResult.toString());
                        Realm tempRealm = Realm.getInstance(context);
                        tempRealm.beginTransaction();
                        tempRealm.allObjects(Item.class).clear();
                        tempRealm.allObjects(Inventory.class).clear();
                        List<Item> realmItems = tempRealm.copyToRealm(itemAllResult.getItemList());
                        for (Item i : itemAllResult.getItemList()) {
                            Warehouse w = tempRealm.where(Warehouse.class).equalTo("id", i.getIdWarehouse()).findFirst();
                            i.setWarehouse(w);
                            i.setSynced(true);

                            tempRealm.copyToRealmOrUpdate(i);
                        }
                        tempRealm.commitTransaction();
                        Set<Long> itemIds = new HashSet<>();
                        for (Item i : realmItems) {
                            itemIds.add(i.getId());
                        }
                        tempRealm.close();
                        return Observable.from(itemIds);
                    })
                    .flatMap(this::getInventory)
                    .flatMap(this::getPhoto)
                    .toList()
                    .doOnCompleted(() -> prefs.edit().putBoolean(C.ITEMS_LOADED, true).commit())
                    .observeOn(AndroidSchedulers.mainThread()) // switch to main thread and fetch items
                    .flatMap(itemIds -> getAllItems(warehouseId));
        }
    }

    private Observable<Long> getInventory(final long itemId) {
        ItemInventory request = new ItemInventory(itemId);
        return SkautApiManager.getWarehouseApi().getItemInventory(request)
                .map((ItemInventoryResult itemInventoryResult) -> {
                    Inventory inventory = itemInventoryResult.getLatestInventory();
                    if (inventory != null) {
                        Realm tempRealm = Realm.getInstance(context);
                        tempRealm.beginTransaction();

                        String inventoryDate = inventory.getDate();
                        long inventoryTimestamp = DateTimeUtils.getTimestampFromDate(inventoryDate, C.DATETIME_FORMAT);

                        // save new inventory to Realm and set its properties
                        inventory = tempRealm.copyToRealm(inventory);
                        inventory.setDateTimestamp(inventoryTimestamp);
                        inventory.setSynced(true);

                        // assign new inventory to corresponding item
                        Item item = tempRealm.where(Item.class).equalTo("id", itemId).findFirst();
                        item.setLatestInventory(inventory);

                        tempRealm.commitTransaction();
                        tempRealm.close();
                    }
                    return itemId;
                });
    }

    private Observable<Long> getPhoto(final long itemId) {
        ItemPhoto request = new ItemPhoto(itemId);
        return SkautApiManager.getWarehouseApi().getItemPhoto(request)
                .map((ItemPhotoResult itemPhotoResult) -> {
                    if (itemPhotoResult.getPhotoData() == null) {
                        Timber.d("no photo data");
                    } else {
                        Realm tempRealm = Realm.getInstance(context);
                        tempRealm.beginTransaction();
                        Item item = tempRealm.where(Item.class).equalTo("id", itemId).findFirst();
                        item.setPhoto(itemPhotoResult.getPhotoData());
                        tempRealm.commitTransaction();
                        tempRealm.close();
                    }
                    return itemId;
                });
    }

    public Observable<Bitmap> getItemPhoto(final String itemPhotoData) {
        return Observable.create((Subscriber<? super Bitmap> subscriber) -> {
            if (!subscriber.isUnsubscribed()) {
                Timber.d("Photo data len: " + itemPhotoData.length());

                byte[] decodedString = Base64.decode(itemPhotoData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Bitmap> saveItemPhoto(final String path, final long itemId) {
        return Observable.create((Subscriber<? super Bitmap> subscriber) -> {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (!file.exists()) {
                    // throw only when subscriber is still subscribed
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new IOException("File does not exist"));
                    }
                    return;
                }

                // load photo from file
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                float aspectRatio = bitmap.getWidth() / (float) bitmap.getHeight();
                int width = Math.round(C.PHOTO_HEIGHT * aspectRatio);

                // resize photo
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, C.PHOTO_HEIGHT, true);

                // encode photo to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                scaledBitmap.compress(C.PHOTO_FORMAT, C.PHOTO_COMPRESSION_QUALITY, baos);
                byte[] bytes = baos.toByteArray();
                String photoData = Base64.encodeToString(bytes, Base64.DEFAULT);

                // save photo data to database
                Realm tempRealm = Realm.getInstance(context);
                tempRealm.beginTransaction();
                Item item = tempRealm.where(Item.class).equalTo("id", itemId).findFirst();
                item.setPhoto(photoData);
                item.setSynced(false);
                tempRealm.commitTransaction();
                tempRealm.close();

                if (file.delete()) {
                    // return only when subscriber is still subscribed
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(scaledBitmap);
                        subscriber.onCompleted();
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new IOException("Failed to delete the file"));
                    }
                }
            } else {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(new IllegalArgumentException("Path is empty"));
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public void updateInventorizeStatus(Set<Item> items) {
        realm.beginTransaction();
        for (Item item : items) {
            // remove previous inventories of this item
            realm.where(Inventory.class).equalTo("itemId", item.getId()).findAll().clear();

            // save new inventory to database
            Inventory inventory = realm.createObject(Inventory.class);
            inventory.setItemId(item.getId());
            inventory.setPerson(prefs.getString(C.USER_PERSON_NAME, ""));
            inventory.setDateTimestamp(DateTimeUtils.getCurrentTimestamp());
            inventory.setSynced(false);

            item.setLatestInventory(inventory);
        }
        realm.commitTransaction();
    }

    public Observable<List<Object>> synchronize() {
        return Observable.defer(() -> WarehouseApplication.getLoginManager().refreshLogin()
                        .flatMap(o -> Observable.merge(
                                synchronizeInventories(),
                                synchronizeItems()
                        ))
                        .toList()
        )
                .subscribeOn(Schedulers.io());
    }

    private Observable<Object> synchronizeItems() {
        final Realm tempRealm = Realm.getInstance(context);

        RealmResults<Item> items = tempRealm
                .where(Item.class)
                .equalTo("synced", false)
                .findAll();

        Timber.d("I would sync following items: " + items.size());
        return Observable.from(items)
                .flatMap(this::synchronizeItem)
                .finallyDo(tempRealm::close);
    }

    private Observable<Object> synchronizeItem(final Item item) {
        Timber.d("want to sync " + item.getName());
        final long itemId = item.getId();
        TempFileInsert request = new TempFileInsert("test.jpg", "jpg", item.getPhoto());

        return SkautApiManager.getWarehouseApi().uploadPhoto(request)
                .flatMap(tempFileInsertResult -> {
                    Realm tempRealm = Realm.getInstance(context);
                    tempRealm.beginTransaction();
                    Item tempItem = tempRealm.where(Item.class).equalTo("id", itemId).findFirst();
                    tempItem.setSynced(true);
                    Timber.d("server responded with GUID: " + tempFileInsertResult.getGuid());
                    tempRealm.commitTransaction();
                    tempRealm.close();
                    return Observable.empty();
                });
    }

    private Observable<Object> synchronizeInventories() {
        final Realm tempRealm = Realm.getInstance(context);

        RealmResults<Inventory> inventories = tempRealm
                .where(Inventory.class)
                .equalTo("synced", false)
                .findAll();

        Timber.d("I will sync following inventories: " + inventories);
        return Observable.from(inventories)
                .flatMap(this::synchronizeInventory)
                .finallyDo(tempRealm::close);
    }

    private Observable<Object> synchronizeInventory(final Inventory inventory) {
        ItemInventorize request = new ItemInventorize(inventory);
        Timber.d("syncing inventory with item id: " + inventory.getItemId() + " and date " + inventory.getDateTimestamp());
        final long itemId = inventory.getItemId();

        return SkautApiManager.getWarehouseApi().itemInventorize(request)
                .flatMap(itemInventorizeResult -> {
                    Realm tempRealm = Realm.getInstance(context);
                    tempRealm.beginTransaction();
                    Inventory tempInventory = tempRealm.where(Inventory.class).equalTo("itemId", itemId).findFirst();
                    tempInventory.setSynced(true);
                    tempInventory.setId(itemInventorizeResult.getId());
                    Timber.d("got ID: " + itemInventorizeResult.getId());
                    tempRealm.commitTransaction();
                    tempRealm.close();
                    return Observable.empty();
                });
    }
}
