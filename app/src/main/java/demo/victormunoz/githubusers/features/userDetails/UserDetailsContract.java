package demo.victormunoz.githubusers.features.userDetails;

import demo.victormunoz.githubusers.model.entity.User;

public interface UserDetailsContract {

    interface PresenterListener {

        void displayUserDetails(User user);

        void onLoadUserDetailsFail();
    }

    interface UserActionsListener {

        void getUserDetails(String login);

        void onBackPressed();
    }
}
