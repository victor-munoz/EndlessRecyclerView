package demo.victormunoz.githubusers.features.userDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.entity.User
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
        nestedScroll.visibility = View.VISIBLE
        user_followers.text = user.followers
        user_following.text = user.following
        user_gists.text = user.totalGists
        user_repos.text = user.totalRepos
        user_created_at.text = resources.getString(R.string.joined_on, user.joinedIn)
        if (user.biography != null) {
            user_bio.visibility = View.VISIBLE
            user_bio.text = user.biography
        }
        if (user.companyName != null) {
            user_company.visibility = View.VISIBLE
            user_company.text = user.companyName
        }
        if (user.isHireable) {
            user_hireable.visibility = View.VISIBLE
            user_hireable.setText(R.string.available_for_hire)
        }
        if (user.location != null) {
            user_location.visibility = View.VISIBLE
            user_location.text = user.location
        }
    }

}
