package cz.skaut.warehousemanager.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import butterknife.InjectView;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.WarehouseApplication;
import cz.skaut.warehousemanager.entity.Inventory;
import cz.skaut.warehousemanager.entity.Item;
import cz.skaut.warehousemanager.helper.C;
import cz.skaut.warehousemanager.helper.DateTimeUtils;
import cz.skaut.warehousemanager.manager.ItemManager;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class ItemDetailFragment extends BaseFragment {

    private Item item;

    @InjectView(R.id.itemPhoto)
    ImageView itemPhoto;

    @InjectView(R.id.itemDescription)
    TextView itemDescription;

    @InjectView(R.id.itemInventoryNumber)
    TextView itemInventoryNumber;

    @InjectView(R.id.itemPurchasePrice)
    TextView itemPurchasePrice;

    @InjectView(R.id.itemPurchaseDate)
    TextView itemPurchaseDate;

    @InjectView(R.id.itemLatestInventory)
    TextView itemLatestInventory;

    @InjectView(R.id.progressWheel)
    ProgressWheel progressWheel;

    private String photoPath;

    private ItemManager itemManager;

    public static ItemDetailFragment newInstance(long itemId) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putLong(C.ITEM_INDEX, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    public ItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemManager = WarehouseApplication.getItemManager();

        Bundle bundle = this.getArguments();
        item = itemManager.getItem(bundle.getLong(C.ITEM_INDEX));

        setHasOptionsMenu(true);

        setTitle(item.getName());

        itemDescription.setText(item.getDescription());
        itemInventoryNumber.setText(item.getInventoryNumber());
        itemPurchasePrice.setText(formatPrice(item.getPurchasePrice()));
        itemPurchaseDate.setText(DateTimeUtils.getFormattedDate(item.getPurchaseDate()));
        Inventory i = item.getLatestInventory();

        // item has no inventory yet
        if (i == null) {
            itemLatestInventory.setText(R.string.never);
        } else {
            long timestamp = i.getDateTimestamp();
            itemLatestInventory.setText(DateTimeUtils.getFormattedTimestamp(timestamp, C.DATE_FORMAT));
        }

        String photoData = item.getPhoto();
        if (!TextUtils.isEmpty(photoData)) {
            itemPhoto.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
            itemManager.getItemPhoto(item.getPhoto())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bitmap -> {
                        itemPhoto.setImageBitmap(bitmap);
                        itemPhoto.setVisibility(View.VISIBLE);
                        progressWheel.setVisibility(View.GONE);
                    }, throwable -> {
                        Timber.e("Unexpected error while fetching photo: " + throwable.getMessage());
                        itemPhoto.setVisibility(View.VISIBLE);
                        progressWheel.setVisibility(View.GONE);
                        showToast(R.string.get_photo_error);
                    });
        }
    }

    private String formatPrice(String price) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(0);

        if (TextUtils.isEmpty(price)) {
            return "";
        } else {
            float priceFloat = Float.valueOf(price);
            return nf.format(priceFloat);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_photo) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // check if the phone can handle camera intent
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    Timber.e(e.getMessage());
                    showToast(R.string.camera_file_error);
                }

                if (photoFile != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent, C.CAMERA_REQUEST_CODE);
                }
            } else {
                Timber.e("error dispatching camera intent");
                showToast(R.string.camera_error);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == C.CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                itemPhoto.setVisibility(View.GONE);
                progressWheel.setVisibility(View.VISIBLE);

                // initiate saving photo
                itemManager.saveItemPhoto(photoPath, item.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bitmap -> {
                            itemPhoto.setImageBitmap(bitmap);
                            itemPhoto.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                        }, throwable -> {
                            Timber.e(throwable.getMessage());
                            throwable.printStackTrace();
                            showToast(R.string.photo_save_error);
                            itemPhoto.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                        });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("warehousemanager_", "." + C.PHOTO_EXT, storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_item_detail;
    }


}
