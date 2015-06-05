package cz.skaut.warehousemanager.rx;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public final class RealmObservable {
    private RealmObservable() {
        // no instances
    }

    public static <T> Observable<T> workReturn(Context context, final Func1<Realm, T> function) {
        return Observable.create(new OnSubscribeRealmWorkReturn<T>(context) {
            @Override
            public T work(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<T> work(Context context, final Action1<Realm> function) {
        return Observable.create(new OnSubscribeRealmWork<T>(context) {
            @Override
            public void work(Realm realm) {
                function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<T> object(Context context, final Func1<Realm, T> function) {
        return Observable.create(new OnSubscribeRealm<T>(context) {
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<RealmList<T>> list(Context context, final Func1<Realm, RealmList<T>> function) {
        return Observable.create(new OnSubscribeRealmList<T>(context) {
            @Override
            public RealmList<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends RealmObject> Observable<RealmResults<T>> results(Context context, final Func1<Realm, RealmResults<T>> function) {
        return Observable.create(new OnSubscribeRealmResults<T>(context) {
            @Override
            public RealmResults<T> get(Realm realm) {
                return function.call(realm);
            }
        });
    }
}