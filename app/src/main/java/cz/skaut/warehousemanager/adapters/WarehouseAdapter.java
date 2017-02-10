package cz.skaut.warehousemanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;

import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.entity.Warehouse;

public class WarehouseAdapter extends RecyclerViewAdapter<Warehouse, WarehouseAdapter.WarehouseViewHolder> {

	public WarehouseAdapter(List<Warehouse> data) {
		super(data);
	}

	@Override
	public WarehouseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
		return new WarehouseViewHolder(view);
	}

	@Override
	public void onBindViewHolder(WarehouseViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);
		Warehouse warehouse = getItem(position);
		subscribeClicks(holder.itemView, holder.itemView, holder);
		holder.warehouseNameText.setText(warehouse.getName());
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getId();
	}

	class WarehouseViewHolder extends RecyclerViewHolder {

		@BindView(R.id.listItem) TextView warehouseNameText;

		WarehouseViewHolder(View view) {
			super(view);
		}

		@Override
		public View getAnimatedView() {
			return warehouseNameText;
		}
	}
}
