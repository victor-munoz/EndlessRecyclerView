package demo.victormunoz.githubusers.features.userDetails

import android.graphics.Bitmap
import demo.victormunoz.githubusers.model.User

interface UserDetailsContract {

    interface ActivityListener {

        fun displayUserDetails(user: User, bitmap: Bitmap)

        fun showErrorMessage()
    }

    interface ActivityPresenterListener {

        fun getUserDetails(login: String, url: String, imageWidth: Int)

        fun onBackPressed()
    }
}
