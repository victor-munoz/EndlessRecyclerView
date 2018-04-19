package demo.victormunoz.githubusers.features.allusers

import android.graphics.Bitmap
import android.view.View
import demo.victormunoz.githubusers.model.User

interface AllUsersContract {

    interface ActivityPresenterListener {

        fun onEndOfTheList()

        fun onRetry()

    }

    interface ViewHolderPresenterListener {

        fun onItemClick(view: View, user: User)

        fun onImageRequest(url:String, size: Int)

    }

    interface ActivityListener {

        fun showError()

        fun showUsers(users: List<User>)

    }
    interface ActivityNavigationListener {

        fun goToUserDetails(view: View, user: User)

    }

    interface ViewHolderListener {

        fun showImage(bitmap:Bitmap)

        fun showError()

    }

    interface AdapterListener {

        fun onEndOfTheList()

    }

}
