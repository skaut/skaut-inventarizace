package cz.skaut.warehousemanager.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder {

	public RecyclerViewHolder(View view) {
		super(view);
		ButterKnife.bind(this, view);
	}

	public abstract View getAnimatedView();
}
