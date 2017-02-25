package cz.skaut.warehousemanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public abstract class RecyclerViewAdapter<T, VH extends RecyclerViewHolder> extends RecyclerView.Adapter<VH> {

	protected List<T> data;
	private int lastPosition = -1;
	private PublishSubject<Integer> clickSubject = PublishSubject.create();

	RecyclerViewAdapter(List<T> data) {
		setHasStableIds(true);
		this.data = data;
	}

	void subscribeClicks(View view, View parent, VH holder) {
		RxView.clicks(view)
				.takeUntil(RxView.detaches(parent))
				.doOnNext(v -> Timber.d("adapter click"))
				.doOnTerminate(() -> Timber.d("terminate"))
				.map(v -> holder.getAdapterPosition())
				.subscribe(clickSubject);
	}

	public void setData(List<T> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	public Observable<Integer> clicks() {
		return clickSubject.asObservable();
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
			Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
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