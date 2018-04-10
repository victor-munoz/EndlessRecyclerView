package demo.victormunoz.githubusers.features.allusers

import android.view.View

import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.model.network.GithubService

class AllUsersPresenter(private val mUsersView: PresenterListener, private val service: GithubService) : ViewListener {


    init {
        loadMore()
    }

    override fun onEndOfTheList() {
        loadMore()
    }

    override fun onRetry() {
        loadMore()
    }

    override fun onItemClick(view: View, user: User) {
        mUsersView.goToUserDetails(view, user)
    }

    private fun loadMore() {
        service.getUsers(object : GithubService.UsersCallback {
            override fun success(users: List<User>) {
                mUsersView.addUsers(users)
            }

            override fun fail() {
                mUsersView.showError()
            }
        })
    }
}
