package cz.skaut.warehousemanager.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.entity.Warehouse;

public class WarehouseAdapter extends RecyclerViewAdapter<Warehouse, WarehouseAdapter.WarehouseViewHolder> {

    public WarehouseAdapter(Context context, List<Warehouse> data) {
        super(context, data);
    }

    @Override
    public WarehouseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new WarehouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WarehouseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Warehouse warehouse = getItem(position);
        holder.warehouseNameText.setText(warehouse.getName());
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    public class WarehouseViewHolder extends RecyclerViewHolder {

        @InjectView(R.id.listItem)
        TextView warehouseNameText;

        public WarehouseViewHolder(View view) {
            super(view);
        }

        @Override
        public View getAnimatedView() {
            return warehouseNameText;
        }
    }
}
