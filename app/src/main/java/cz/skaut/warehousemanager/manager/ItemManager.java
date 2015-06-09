package cz.skaut.warehousemanager.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Set;

import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.entity.Inventory;
import cz.skaut.warehousemanager.entity.Item;
import cz.skaut.warehousemanager.entity.Warehouse;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.DateTimeUtils;
import cz.skaut.warehousemanager.rx.RealmObservable;
import cz.skaut.warehousemanager.soap.ItemAll;
import cz.skaut.warehousemanager.soap.ItemInventorize;
import cz.skaut.warehousemanager.soap.ItemInventory;
import cz.skaut.warehousemanager.soap.ItemPhoto;
import cz.skaut.warehousemanager.soap.SkautApiManager;
import cz.skaut.warehousemanager.soap.TempFileInsert;
import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ItemManager {

    private final Context context;
    private final Realm realmUI;
    private final SharedPreferences prefs;

    public ItemManager(Context ctx) {
        context = ctx.getApplicationContext();
        realmUI = Realm.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Item getItem(long id) {
        return realmUI.where(Item.class).equalTo("id", id).findFirst();
    }

    private Observable<List<Item>> getAllItems(long warehouseId) {
        List<Item> items = realmUI.where(Item.class).equalTo("warehouse.id", warehouseId).findAllSorted("id");
        return Observable.just(items);
    }

    public List<Item> getAllUninventorizedItems(long warehouseId) {
        // TODO: change this to reflect settings
        return realmUI.where(Item.class).equalTo("warehouse.id", warehouseId).equalTo("latestInventory.synced", true).findAllSorted("id");
    }

    public Observable<List<Item>> getItems(final long warehouseId) {
        // return items from database if they are already loaded
        if (prefs.getBoolean(C.ITEMS_LOADED, false)) {
            return getAllItems(warehouseId);
        } else {
            ItemAll request = new ItemAll();
            return SkautApiManager.getWarehouseApi().getAllItems(request)
                    .flatMap(itemAllResult -> RealmObservable.results(context, realm -> {
                        Timber.i("Item All: " + itemAllResult.toString());
                        realm.allObjects(Item.class).clear();
                        realm.allObjects(Inventory.class).clear();
                        for (Item i : itemAllResult.getItemList()) {
                            Warehouse w = realm.where(Warehouse.class).equalTo("id", i.getIdWarehouse()).findFirst();
                            i.setWarehouse(w);
                            i.setSynced(true);

                            realm.copyToRealm(i);
                        }
                        return realm.allObjects(Item.class);
                    }))
                    .flatMap(Observable::from)
                    .map(Item::getId)
                    .flatMap(this::getInventory)
                    .flatMap(this::getPhoto)
                    .toList()
                    .doOnCompleted(() -> prefs.edit().putBoolean(C.ITEMS_LOADED, true).apply())
                    .observeOn(AndroidSchedulers.mainThread()) // switch to main thread and fetch items
                    .flatMap(itemIds -> getAllItems(warehouseId));
        }
    }

    private Observable<Long> getInventory(final long itemId) {
        ItemInventory request = new ItemInventory(itemId);
        return SkautApiManager.getWarehouseApi().getItemInventory(request)
                .flatMap(itemInventoryResult -> RealmObservable.work(context, realm -> {
                    Inventory inventory = itemInventoryResult.getLatestInventory();
                    if (inventory != null) {
                        String inventoryDate = inventory.getDate();
                        long inventoryTimestamp = DateTimeUtils.getTimestampFromDate(inventoryDate, C.DATETIME_FORMAT);

                        // save new inventory to Realm and set its properties
                        inventory = realm.copyToRealm(inventory);
                        inventory.setDateTimestamp(inventoryTimestamp);
                        inventory.setSynced(true);

                        // assign new inventory to corresponding item
                        Item item = realm.where(Item.class).equalTo("id", itemId).findFirst();
                        item.setLatestInventory(inventory);
                    }
                    return itemId;
                }));
    }

    private Observable<Long> getPhoto(final long itemId) {
        ItemPhoto request = new ItemPhoto(itemId);
        return SkautApiManager.getWarehouseApi().getItemPhoto(request)
                .flatMap(itemPhotoResult -> RealmObservable.work(context, realm -> {
                    if (itemPhotoResult.getPhotoData() != null) {
                        Item item = realm.where(Item.class).equalTo("id", itemId).findFirst();
                        item.setPhoto(itemPhotoResult.getPhotoData());
                    }
                    return itemId;
                }));
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

    public Observable<Bitmap> saveItemPhoto(final File file, final long itemId) {
        return RealmObservable.work(context, realm -> {
            if (!file.exists()) {
                throw new RuntimeException("File does not exist");
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
            Item item = realm.where(Item.class).equalTo("id", itemId).findFirst();
            item.setPhoto(photoData);
            item.setSynced(false);

            prefs.edit().putBoolean(C.SYNC_NEEDED, true).apply();

            if (!file.delete()) {
                // Fail silently, user will find empty picture :)
                Timber.e("Failed to delete file: " + file.getAbsolutePath());
            }
            return scaledBitmap;
        })
                .subscribeOn(Schedulers.io());
    }

    public void updateInventorizeStatus(final Set<Item> items) {
        realmUI.beginTransaction();
        for (Item item : items) {
            // remove previous inventories of this item
            realmUI.where(Inventory.class).equalTo("itemId", item.getId()).findAll().clear();

            // save new inventory to database
            Inventory inventory = realmUI.createObject(Inventory.class);
            inventory.setItemId(item.getId());
            inventory.setPerson(prefs.getString(C.USER_PERSON_NAME, ""));
            inventory.setDateTimestamp(DateTimeUtils.getCurrentTimestamp());
            inventory.setSynced(false);

            item.setLatestInventory(inventory);
        }
        realmUI.commitTransaction();

        if (!items.isEmpty()) {
            prefs.edit().putBoolean(C.SYNC_NEEDED, true).apply();
        }
    }

    public Observable<List<Object>> synchronize() {
        return WarehouseApplication.getLoginManager().refreshLogin()
                .flatMap(o -> Observable.merge(
                        synchronizeInventories(),
                        synchronizeItems()
                ))
                .toList()
                .doOnCompleted(() -> prefs.edit().putBoolean(C.SYNC_NEEDED, false).apply());
    }

    private Observable<Object> synchronizeItems() {
        return RealmObservable.results(context, realm ->
                realm
                        .where(Item.class)
                        .equalTo("synced", false)
                        .findAll())
                .flatMap(Observable::from)
                .flatMap(this::synchronizeItem);
    }

    private Observable<Object> synchronizeItem(final Item item) {
        Timber.d("Syncing item" + item);
        final long itemId = item.getId();
        TempFileInsert request = new TempFileInsert("test.jpg", "jpg", item.getPhoto());

        return SkautApiManager.getWarehouseApi().uploadPhoto(request)
                .flatMap(tempFileInsertResult -> RealmObservable.work(context, realm -> {
                    Item tempItem = realm.where(Item.class).equalTo("id", itemId).findFirst();
                    tempItem.setSynced(true);
                    Timber.d("server responded with GUID: " + tempFileInsertResult.getGuid());
                    return Observable.empty();
                }));
    }

    private Observable<Object> synchronizeInventories() {
        return RealmObservable.results(context, realm ->
                realm
                        .where(Inventory.class)
                        .equalTo("synced", false)
                        .findAll())
                .flatMap(Observable::from)
                .flatMap(this::synchronizeInventory);
    }

    private Observable<Object> synchronizeInventory(final Inventory inventory) {
        Timber.d("Syncing " + inventory);

        ItemInventorize request = new ItemInventorize(inventory);
        final long itemId = inventory.getItemId();

        return SkautApiManager.getWarehouseApi().itemInventorize(request)
                .flatMap(itemInventorizeResult -> RealmObservable.work(context, realm -> {
                    Inventory tempInventory = realm.where(Inventory.class).equalTo("itemId", itemId).findFirst();
                    tempInventory.setSynced(true);
                    tempInventory.setId(itemInventorizeResult.getId());
                    Timber.d("got ID: " + itemInventorizeResult.getId());
                    return Observable.empty();
                }));
    }
}
