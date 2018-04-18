package demo.victormunoz.githubusers.features.allusers

import android.view.View
import demo.victormunoz.githubusers.model.User

interface AllUsersContract {

    interface PresenterListener {

        fun onEndOfTheList()

        fun onRetry()

        fun onItemClick(view: View, user: User)

    }

    interface ViewListener {

        fun showError()

        fun addUsers(users: List<User>)

        fun goToUserDetails(view: View, user: User)

    }

    interface AdapterListener {

        fun onEndOfTheList()

        fun onItemClick(view: View, position: Int)
    }

}
