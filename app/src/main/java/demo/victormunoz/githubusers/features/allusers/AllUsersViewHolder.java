package demo.victormunoz.githubusers.features.allusers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.R;

public class AllUsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final AllUsersContract.AdapterListener adapterListener;
    @BindView(R.id.login)
    TextView loginName;
    @BindView(R.id.avatar)
    ImageView avatar;

    AllUsersViewHolder(@NonNull View view, AllUsersContract.AdapterListener listener){
        super(view);
        adapterListener = listener;
        ButterKnife.bind(this, view);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        adapterListener.onItemClick(avatar, getAdapterPosition());
    }
}