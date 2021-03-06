package demo.victormunoz.githubusers.features.allusers;

import android.view.View;
import com.trello.rxlifecycle2.android.ActivityEvent;
import java.util.List;
import demo.victormunoz.githubusers.model.entity.User;
import io.reactivex.Observable;

public interface AllUsersContract {

    interface ViewListener {
        void onEndOfTheList();

        void onRetry();

        void onItemClick(View view, User user);

    }

    interface PresenterListener {
        void showError();

        void addUsers(List<User> users);

        void goToUserDetails(View view, String loginName, String avatarURL);

    }

    interface AdapterListener {
        void onEndOfTheList();

        void onItemClick(View view, int position);

        Observable<ActivityEvent> getLifeCycle();
    }

}
