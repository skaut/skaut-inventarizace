package cz.skaut.warehousemanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

public abstract class RecyclerViewAdapter<T, VH extends RecyclerViewHolder> extends RecyclerView.Adapter<VH> {

	protected final Context context;
	protected final LayoutInflater inflater;
	protected List<T> data;
	private int lastPosition = -1;

	public RecyclerViewAdapter(Context context, List<T> data) {
		setHasStableIds(true);
		this.context = context.getApplicationContext();
		this.data = data;
		inflater = LayoutInflater.from(this.context);
	}

	public void setData(List<T> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public void onBindViewHolder(VH holder, int position) {
		setAnimation(holder.getAnimatedView(), position);
	}

	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	private void setAnimation(View viewToAnimate, int position) {
		if (viewToAnimate == null) {
			return;
		}
		if (position > lastPosition) {
			Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
			int offset = 0;
			if (position > 0) {
				offset = 50 * position;
			}
			animation.setStartOffset(offset);
			viewToAnimate.startAnimation(animation);
			lastPosition = position;
		}
	}
}