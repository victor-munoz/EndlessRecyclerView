package demo.victormunoz.githubusers.ui.userdetail;
import demo.victormunoz.githubusers.model.User;

public interface UserDetailContract {

    interface View {

        void displayUserDetails(User user);

        void onLoadUserDetailsFail();
    }

    interface UserActionsListener {

        void loadUserDetails(String login);
    }
}
