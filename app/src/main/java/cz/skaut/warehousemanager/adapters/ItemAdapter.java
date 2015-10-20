package cz.skaut.warehousemanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.entity.Item;

public class ItemAdapter extends RecyclerViewAdapter<Item, ItemAdapter.ItemViewHolder> {

	public ItemAdapter(List<Item> data) {
		super(data);
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
		return new ItemViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ItemViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);
		Item item = getItem(position);
		holder.itemNameText.setText(item.getName());
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getId();
	}

	class ItemViewHolder extends RecyclerViewHolder {

		@Bind(R.id.listItem) public TextView itemNameText;

		public ItemViewHolder(View base) {
			super(base);
		}

		@Override
		public View getAnimatedView() {
			return itemNameText;
		}
	}
}
