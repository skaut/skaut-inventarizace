package cz.skaut.warehousemanager.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.OnClick;
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

	@BindView(R.id.itemPhoto) ImageView itemPhoto;
	@BindView(R.id.itemDescription) TextView itemDescription;
	@BindView(R.id.itemInventoryNumber) TextView itemInventoryNumber;
	@BindView(R.id.itemPurchasePrice) TextView itemPurchasePrice;
	@BindView(R.id.itemPurchaseDate) TextView itemPurchaseDate;
	@BindView(R.id.itemLatestInventory) TextView itemLatestInventory;
	@BindView(R.id.progressWheel) ProgressWheel progressWheel;

	private Item item;

	private File file = null;

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
		if (item == null) {
			throw new AssertionError("ItemDetailFragment created without valid item");
		}

		setHasOptionsMenu(true);

		setTitle(item.getName());

		itemDescription.setText(item.getDescription());
		itemInventoryNumber.setText(item.getInventoryNumber());
		itemPurchasePrice.setText(formatPrice(item.getPurchasePrice()));
		itemPurchaseDate.setText(DateTimeUtils.getFormattedDate(item.getPurchaseDate()));
		Inventory latestInventory = item.getLatestInventory();

		// item has no inventory yet
		if (latestInventory == null) {
			itemLatestInventory.setText(R.string.never);
		} else {
			long timestamp = latestInventory.getDateTimestamp();
			itemLatestInventory.setText(DateTimeUtils.getFormattedTimestamp(timestamp, C.DATE_FORMAT));
		}

		String photoData = item.getPhoto();
		if (!TextUtils.isEmpty(photoData)) {
			// decode photo to Bitmap in background thread
			itemPhoto.setVisibility(View.GONE);
			progressWheel.setVisibility(View.VISIBLE);
			itemManager.decodePhoto(item.getPhoto())
					.observeOn(AndroidSchedulers.mainThread()) // TODO: check if it works without this
					.subscribe(bitmap -> {
						itemPhoto.setImageBitmap(bitmap);
						itemPhoto.setVisibility(View.VISIBLE);
						progressWheel.setVisibility(View.GONE);
					}, e -> {
						Timber.e(e, "Failed to load photo");
						itemPhoto.setVisibility(View.VISIBLE);
						progressWheel.setVisibility(View.GONE);
						Snackbar.make(progressWheel, R.string.get_photo_error, Snackbar.LENGTH_LONG).show();
					});
		}
	}

	/**
	 * Formats purchase price
	 *
	 * @param price price to be formatted
	 * @return formatted price
	 */
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

	@OnClick(R.id.photoFab) void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// check if the phone can handle camera intent
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			try {
				File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				file = File.createTempFile("warehousemanager_", "." + C.PHOTO_EXT, storageDir);
			} catch (IOException e) {
				Timber.e(e, "Failed to create file");
				Snackbar.make(progressWheel, R.string.camera_file_error, Snackbar.LENGTH_LONG).show();
			}

			if (file != null) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, C.CAMERA_REQUEST_CODE);
			}
		} else {
			Timber.e("Error dispatching camera intent");
			Snackbar.make(progressWheel, R.string.camera_error, Snackbar.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == C.CAMERA_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				itemPhoto.setVisibility(View.GONE);
				progressWheel.setVisibility(View.VISIBLE);

				// initiate saving photo
				itemManager.saveItemPhoto(file, item.getId())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(bitmap -> {
							itemPhoto.setImageBitmap(bitmap);
							itemPhoto.setVisibility(View.VISIBLE);
							progressWheel.setVisibility(View.GONE);
						}, e -> {
							Timber.e(e, "Failed to save photo");
							itemPhoto.setVisibility(View.VISIBLE);
							progressWheel.setVisibility(View.GONE);
							Snackbar.make(progressWheel, R.string.photo_save_error, Snackbar.LENGTH_LONG).show();

							// saving photo failed, delete temporary file
							if (!file.delete()) {
								Timber.e("Failed to delete file");
							}
						});
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// no photo taken, delete temporary file
				if (!file.delete()) {
					Timber.e("Failed to delete file");
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected int getFragmentLayout() {
		return R.layout.fragment_item_detail;
	}


}
