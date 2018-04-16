package demo.victormunoz.githubusers.features.allusers

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnimationUtils
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.User
import demo.victormunoz.githubusers.network.image.ImageService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_all_users.*


class AllUsersViewHolder(
        override val containerView: View,
        private val adapterListener: AllUsersContract.AdapterListener,
        private val imageService: ImageService

) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val lifeCycle = (containerView.context as RxAppCompatActivity).lifecycle()

    init {
        RxView.clicks(containerView)
                .compose(RxLifecycle.bindUntilEvent(lifeCycle, ActivityEvent.DESTROY))
                .subscribe {
                    adapterListener.onItemClick(iv_avatar, adapterPosition)
                }

    }

    fun bind(user: User) {
        containerView.visibility = View.INVISIBLE
        imageService.getImage(user.avatarUrl, ImageService.ImageSize.SMALL)
                .compose(RxLifecycle.bindUntilEvent(lifeCycle, ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { bitmap ->
                            containerView.visibility = View.VISIBLE
                            updateData(bitmap, user.loginName)
                            animEntrance()
                        },
                        { _ ->
                            containerView.visibility = View.VISIBLE
                        })
    }

    private fun updateData(v: Bitmap, loginName: String) {
        iv_login.text = loginName
        iv_avatar.setImageBitmap(v)
    }

    private fun animEntrance() {
        val anim = AnimationUtils.loadAnimation(containerView.context.applicationContext, R.anim.alpha_translation_in)
        containerView.startAnimation(anim)
    }


}