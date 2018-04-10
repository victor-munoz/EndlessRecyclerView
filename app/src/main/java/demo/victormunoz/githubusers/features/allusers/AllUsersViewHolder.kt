package demo.victormunoz.githubusers.features.allusers

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.jakewharton.rxbinding2.view.RxView
import com.squareup.picasso.Callback
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.utils.picasso.ImageLoader
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_all_users.*
import java.util.*
import java.util.concurrent.TimeUnit


class AllUsersViewHolder
internal constructor(override val containerView: View, private val adapterListener: AllUsersContract.AdapterListener, private val imageLoader: ImageLoader) : RecyclerView.ViewHolder(containerView), Callback, LayoutContainer {

    init {
        containerView.visibility = View.GONE
        val lifeCycle = (containerView.context as RxAppCompatActivity).lifecycle()
        RxView
                .clicks(containerView)
                .debounce(300, TimeUnit.MILLISECONDS, mainThread())
                .compose(RxLifecycle.bindUntilEvent(lifeCycle, ActivityEvent.DESTROY))
                .subscribe { adapterListener.onItemClick(iv_avatar, adapterPosition) }

    }

    fun bind(user: User) {
        containerView.visibility = View.INVISIBLE
        iv_login.text = user.loginName
        imageLoader.load(user.avatarUrl, Objects.requireNonNull<ImageView>(iv_avatar),this)

    }

    override fun onSuccess() {
        containerView.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(containerView.context.applicationContext, R.anim.alpha_translation_in)
        containerView.startAnimation(anim)
    }

    override fun onError() {
        containerView.visibility = View.VISIBLE
    }


}