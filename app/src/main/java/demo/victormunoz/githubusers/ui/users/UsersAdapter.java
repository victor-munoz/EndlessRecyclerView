package demo.victormunoz.githubusers.ui.users;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.model.User;
import demo.victormunoz.githubusers.utils.picasso.ImageToCircleTransformation;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    private final static String ALPHA = "alpha";
    private final static String TRANSLATION_Y = "translationY";
    private final static int ANIMATION_DURATION = 1000;
    private final UsersListener gitHubUsersListener;
    private List<User> usersList;
    private Context context;

    public UsersAdapter(Context context, List<User> moviesList, UsersListener listener) {
        this.usersList = moviesList;
        this.context = context;
        this.gitHubUsersListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_users, parent, false);
        return new MyViewHolder(itemView, gitHubUsersListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = usersList.get(position);
        //set user's profile image
        Picasso.with(context)
                .load(user.getAvatarUrl()).fit().centerCrop()
                .transform(new ImageToCircleTransformation())
                .into(holder.avatar);
        //set username
        holder.loginName.setText(user.getLoginName());
        //start entrance animation

    }

    /**
     * Change the visibility (alpha) and the position of the view when make his entrance to the
     * screen.
     * @param view the view to be animated
     * @return ObjectAnimator
     */
    private ObjectAnimator entranceAnimation(final View view) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(ALPHA, 0f, 1f);
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 100f, 0f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, alpha, translationY);

        //we need to set this delay to avoid an undesirable blink effect
        anim.setStartDelay(1);

        anim.setDuration(ANIMATION_DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
        return anim;
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    /**
     * Override this method to:
     * 1.- Animate when a item enter to the screen
     * 2.- inform the activity that we reach the end of the list
     * @param holder the holder of the item
     */
    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //start entrance animation
        entranceAnimation(holder.itemView).start();
        //it is the last element of the list displayed?
        if(holder.getAdapterPosition()==usersList.size()-1){
            gitHubUsersListener.onEndOfTheList();
        }
    }


    public User getItem(int position) {
        return usersList.get(position);
    }

    public void addUsers(List<User> users) {
        int startPosition=usersList.size();
        int endPosition=startPosition+users.size()-1;
        usersList.addAll(users);
        notifyItemRangeInserted(startPosition,endPosition);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final UsersListener usersListener;
        @BindView(R.id.login)
        TextView loginName;
        @BindView(R.id.avatar)
        ImageView avatar;

        public MyViewHolder(View view, UsersListener listener) {
            super(view);
            usersListener = listener;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            User user = getItem(position);
            usersListener.onUserClick(avatar,user.getLoginName(),user.getAvatarUrl());
        }
    }

    public interface UsersListener {
        void onUserClick(View view,String loginName,String avatarURL);
        void onEndOfTheList();
    }
}
