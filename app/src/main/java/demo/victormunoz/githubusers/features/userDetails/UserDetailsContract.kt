package demo.victormunoz.githubusers.features.userDetails

import android.graphics.Bitmap
import demo.victormunoz.githubusers.model.User

interface UserDetailsContract {

    interface ViewListener {

        fun displayUserDetails(user: User, bitmap: Bitmap)

        fun showErrorMessage()
    }

    interface PresenterListener {

        fun getUserDetails(login: String, url: String)

        fun onBackPressed()
    }
}
