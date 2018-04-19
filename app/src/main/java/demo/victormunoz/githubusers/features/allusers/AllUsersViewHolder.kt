package demo.victormunoz.githubusers.features.allusers

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnimationUtils
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import demo.victormunoz.githubusers.App
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.User
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_all_users.*
import javax.inject.Inject


class AllUsersViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer, AllUsersContract.ViewHolderListener {

    private val lifeCycle = (containerView.context as RxAppCompatActivity).lifecycle()

    @Inject
    lateinit var presenterListener: AllUsersContract.ViewHolderPresenterListener

    init {
        injectDependencies()
    }

    private fun injectDependencies() {
        (containerView.context.applicationContext as App).getUsersViewHolderComponent(this).inject(this)
    }

    fun bind(user: User) {
        containerView.visibility = View.INVISIBLE
        iv_login.text = user.loginName
        RxView.clicks(containerView)
                .compose(RxLifecycle.bindUntilEvent(lifeCycle, ActivityEvent.DESTROY))
                .subscribe {
                    presenterListener.onItemClick(iv_avatar, user)
                }
        presenterListener.onImageRequest(user.avatarUrl, App.mResources.getDimensionPixelSize(R.dimen.image_circle_normal))
    }

    override fun showImage(bitmap: Bitmap) {
        containerView.visibility = View.VISIBLE
        iv_avatar.setImageBitmap(bitmap)
        val anim = AnimationUtils.loadAnimation(containerView.context.applicationContext, R.anim.alpha_translation_in)
        containerView.startAnimation(anim)
    }
    override fun showError() {
        containerView.visibility = View.VISIBLE
    }

}