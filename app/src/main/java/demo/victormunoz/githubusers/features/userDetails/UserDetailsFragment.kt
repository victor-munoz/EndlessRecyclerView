package demo.victormunoz.githubusers.features.userDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.User
import kotlinx.android.synthetic.main.fragment_user_detail.*


class UserDetailsFragment : Fragment() {

    companion object {

        fun newInstance(): UserDetailsFragment {
            return UserDetailsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View? {
        return inflater.inflate(R.layout.fragment_user_detail, container, false)

    }

    fun displayUserInfo(user: User) {
        nsv_root.visibility = View.VISIBLE
        tv_followers.text = user.followers
        tv_following.text = user.following
        tv_gists.text = user.totalGists
        tv_repos.text = user.totalRepos
        tv_created_at.text = resources.getString(R.string.joined_on, user.joinedIn)
        user.biography?.run {
            tv_bio.visibility = View.VISIBLE
            tv_bio.text = this
        }
        user.companyName?.run {
            tv_company.visibility = View.VISIBLE
            tv_company.text = this
        }
        user.location?.run {
            tv_location.visibility = View.VISIBLE
            tv_location.text = this
        }
        if (user.isHireable) {
            tv_hireable.visibility = View.VISIBLE
            tv_hireable.setText(R.string.available_for_hire)
        }

    }

}
