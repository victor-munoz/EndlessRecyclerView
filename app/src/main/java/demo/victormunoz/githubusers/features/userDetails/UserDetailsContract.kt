package demo.victormunoz.githubusers.features.userDetails

import android.graphics.Bitmap
import demo.victormunoz.githubusers.model.entity.User

interface UserDetailsContract {

    interface PresenterListener {

        fun displayUserDetails(user: User, bitmap: Bitmap)

        fun showErrorMessage()
    }

    interface UserActionsListener {

        fun getUserDetails(login: String, url: String)

        fun onBackPressed()
    }
}
