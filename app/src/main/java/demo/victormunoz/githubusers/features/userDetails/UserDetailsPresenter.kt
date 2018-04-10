package demo.victormunoz.githubusers.features.userDetails

import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.PresenterListener
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.model.network.GithubService

class UserDetailsPresenter(private var mUsersPresenterListener: PresenterListener?, private val githubService: GithubService) : UserDetailsContract.UserActionsListener {

    override fun onBackPressed() {
        mUsersPresenterListener = null
    }

    override fun getUserDetails(login: String) {
        githubService.getUser(login, object : GithubService.UserCallback {
            override fun success(user: User) {
                if (mUsersPresenterListener != null) {
                    mUsersPresenterListener!!.displayUserDetails(user)
                }
            }

            override fun fail() {
                if (mUsersPresenterListener != null) {
                    mUsersPresenterListener!!.onLoadUserDetailsFail()
                }
            }
        })

    }
}
