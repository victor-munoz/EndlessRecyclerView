package demo.victormunoz.githubusers.features.userDetails

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Callback
import demo.victormunoz.githubusers.App
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.utils.picasso.ImageLoader
import kotlinx.android.synthetic.main.activity_user_detail.*
import javax.inject.Inject

private const val INTENT_USER = "user"

fun Context.userDetailsActivity(user: User): Intent {
    return Intent(this, UserDetailsActivity::class.java).apply {
        putExtra(INTENT_USER, user)
    }

}

class UserDetailsActivity : AppCompatActivity(), UserDetailsContract.PresenterListener, Callback {

    companion object {
        private const val USER_DETAIL_FRAGMENT = "user's detail fragment"
    }

    private var detailFragment: UserDetailsFragment? = null

    private var isSharedElementOn = true

    @Inject
    lateinit var mActionsListener: UserDetailsContract.UserActionsListener

    @Inject
    lateinit var imageLoader: ImageLoader


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //postpone the transition start to wait for the avatar image be loaded
        postponeEnterTransition()
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(toolbar)
        setDependencyInjection()

        if (null == savedInstanceState) {
            mActionsListener.getUserDetails(getUser().loginName)
            initFragment(UserDetailsFragment.newInstance())
        }
        downloadProfileImage()
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        postponeEnterTransition()
    }

    override fun onDestroy() {
        imageLoader.cancelAll()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSuccess() {
        if (isSharedElementOn) {
            startPostponedEnterTransition()
            startRevealAnimation(gradient, 200)
        } else {
            startRevealAnimation(gradient, 0)
        }
    }

    override fun onError() {
        if (isSharedElementOn) {
            startPostponedEnterTransition()
        }
        onLoadUserDetailsFail()
    }

    override fun onBackPressed() {
        mActionsListener.onBackPressed()
        super.onBackPressed()
    }

    /**
     * use Android Palette Library to create ShapeDrawable with a gradient effect
     */
    private val gradient: ShapeDrawable
        get() {
            val bitmap = (user_avatar!!.drawable as BitmapDrawable).bitmap
            val palette = Palette.from(bitmap).generate()
            val muted = palette.getMutedColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
            val mutedDark = palette.getDarkMutedColor(ContextCompat.getColor(baseContext, R.color.colorPrimaryDark))
            val x = app_bar!!.width
            val mDrawable = ShapeDrawable(RectShape())
            val gradient = LinearGradient(0f, 0f, x.toFloat(), 0f, muted, mutedDark, Shader.TileMode.REPEAT)
            mDrawable.paint.shader = gradient
            return mDrawable

        }
    private fun getUser(): User {
        return intent.extras.getParcelable(INTENT_USER)
    }

    private fun downloadProfileImage() {
        imageLoader.load(getUser().avatarUrl, user_avatar, this)
    }

    private fun startRevealAnimation(gradient: ShapeDrawable, delay: Long) {
        val reveal = ViewAnimationUtils.createCircularReveal(app_bar, ((user_avatar!!.parent as LinearLayout).x + user_avatar!!.width / 2).toInt(), ((user_avatar!!.parent as LinearLayout).y + user_avatar!!.height / 2).toInt(), (user_avatar!!.width / 4).toFloat(), app_bar!!.width.toFloat())
        reveal.startDelay = delay
        reveal.duration = 1000
        reveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                app_bar.background = gradient
            }
        })
        reveal.start()
    }

    private fun initFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.contentFrame, fragment, USER_DETAIL_FRAGMENT)
        transaction.commit()
        fragmentManager.executePendingTransactions()
        detailFragment = fragmentManager.findFragmentByTag(USER_DETAIL_FRAGMENT) as UserDetailsFragment

    }

    private fun setDependencyInjection() {
        (application as App).getUserComponent(this).inject(this)
    }

    //presenter listener
    override fun displayUserDetails(user: User) {
        detailFragment!!.displayUserInfo(user)
        user_github!!.visibility = View.VISIBLE
        user_github!!.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(user.gitHubProfileUrl))
            startActivity(browserIntent)
        }
        if (user.blog != null && Patterns.WEB_URL.matcher(user.blog).matches()) {
            user_blog!!.visibility = View.VISIBLE
            user_blog!!.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(user.blog))
                startActivity(browserIntent)
            }
        }
        user_name!!.text = user.fullName
        user_email!!.text = user.email
        user_login!!.text = user.loginName
    }

    override fun onLoadUserDetailsFail() {
        val errorMessage = getString(R.string.error_downloading_users_profile)
        val snackbar = Snackbar.make(user_avatar!!, errorMessage, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) {
            if (user_avatar!!.drawable == null) {
                isSharedElementOn = false
                downloadProfileImage()
            }
            if (user_name!!.text.isEmpty()) {
                mActionsListener.getUserDetails(getUser().loginName)
            }
        }
        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        val cloudError = ResourcesCompat.getDrawable(resources, R.drawable.icon_cloud_error, null)
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudError, null, null, null)
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.margin_normal)
        snackbar.show()
    }


}


