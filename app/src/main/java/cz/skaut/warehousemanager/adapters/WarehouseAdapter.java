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
        View v = inflater.inflate(R.layout.list_item_warehouse, parent, false);
        return new WarehouseViewHolder(v);
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

        @InjectView(R.id.warehouseListItem)
        public TextView warehouseNameText;

        public WarehouseViewHolder(View view) {
            super(view);
        }

        @Override
        public View getAnimatedView() {
            return warehouseNameText;
        }
    }

    /*private void reorderList() {
        Map<Long, Warehouse> result = new HashMap<>();
        List<Warehouse> workingData = new ArrayList<>(data);
        Timber.d("Start list: " + workingData);
        for(Iterator<Warehouse> iterator = workingData.iterator(); iterator.hasNext();) {
            Warehouse w = iterator.next();
            if(w.getIdParentWarehouse() == 0) {
                Timber.d("Putting main warehouse to map: " + w.getName());
                result.put(w.getId(), w);
                iterator.remove();
            } else {
                result.put(w.getIdParentWarehouse(), w);
                Timber.d("Putting child warehouse to map: " + w.getName());
                Timber.d("Remaining: " + workingData);
            }
            Timber.d("Remaining: " + workingData);
        }

        Timber.d("Result map: " + result);
    }*/
}
