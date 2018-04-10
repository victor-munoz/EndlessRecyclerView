package demo.victormunoz.githubusers.features.userDetails

import demo.victormunoz.githubusers.model.entity.User

interface UserDetailsContract {

    interface PresenterListener {

        fun displayUserDetails(user: User)

        fun onLoadUserDetailsFail()
    }

    interface UserActionsListener {

        fun getUserDetails(login: String)

        fun onBackPressed()
    }
}
