package demo.victormunoz.githubusers.ui.users;

import demo.victormunoz.githubusers.model.User;
import java.util.List;

public interface UsersContract {

    interface Views {

        void addUsersToAdapter(List<User> users);

        void onLoadMoreUsersFail();

    }

    interface UserActionsListener {

        void loadMoreUsers();

    }
}
