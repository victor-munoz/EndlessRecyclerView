package demo.victormunoz.githubusers.features.allusers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.model.entity.User;
import demo.victormunoz.githubusers.utils.picasso.ImageLoader;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersViewHolder> {
    private final AllUsersContract.AdapterListener adapterListener;
    private final ImageLoader imageLoader;
    private final List<User> usersList = new ArrayList<>();

    public AllUsersAdapter(ImageLoader imageLoader, AllUsersContract.AdapterListener listener){
        this.imageLoader = imageLoader;
        this.adapterListener = listener;
    }

    @NonNull
    @Override
    public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_users, parent, false);
        return new AllUsersViewHolder(itemView, adapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final AllUsersViewHolder holder, final int position){
        final User user = usersList.get(position);
        holder.loginName.setText(user.getLoginName());
        imageLoader.load(user.getAvatarUrl(), Objects.requireNonNull(holder.avatar));
        entranceAnimation(holder.itemView);
    }

    @Override
    public int getItemCount(){
        return usersList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull final AllUsersViewHolder holder){
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == usersList.size() - 1) {
            adapterListener.onEndOfTheList();
        }
    }

    @Override
    public void onViewRecycled(@NonNull AllUsersViewHolder holder){
        holder.itemView.setVisibility(View.INVISIBLE);
        imageLoader.cancelRequest(holder.avatar);
        super.onViewRecycled(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView){
        imageLoader.cancelAll();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    User getItem(int position){
        return usersList.get(position);
    }

    void addUsers(@NonNull List<User> users){
        int startPosition = usersList.size();
        int endPosition = startPosition + users.size() - 1;
        usersList.addAll(users);
        notifyItemRangeInserted(startPosition, endPosition);
    }

    private void entranceAnimation(@NonNull final View view){
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(view.getContext().getApplicationContext(), R.anim.alpha_translation_in);
        view.startAnimation(hyperspaceJumpAnimation);
    }

}
