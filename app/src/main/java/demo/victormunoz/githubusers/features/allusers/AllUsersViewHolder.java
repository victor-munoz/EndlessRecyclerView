package demo.victormunoz.githubusers.features.allusers;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.R;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;


public class AllUsersViewHolder extends RecyclerView.ViewHolder {

    private final AllUsersContract.AdapterListener adapterListener;
    @BindView(R.id.login)
    TextView loginName;
    @BindView(R.id.avatar)
    ImageView avatar;


    @SuppressLint("CheckResult")
    AllUsersViewHolder(@NonNull View view, AllUsersContract.AdapterListener listener){
        super(view);
        adapterListener = listener;
        ButterKnife.bind(this, view);

        RxView
                .clicks(view)
                .debounce(300, TimeUnit.MILLISECONDS, mainThread())
                .compose(RxLifecycle.bindUntilEvent(adapterListener.getLifeCycle(), ActivityEvent.DESTROY))
                .subscribe((Object aVoid) -> adapterListener.onItemClick(avatar, getAdapterPosition()));

    }
}