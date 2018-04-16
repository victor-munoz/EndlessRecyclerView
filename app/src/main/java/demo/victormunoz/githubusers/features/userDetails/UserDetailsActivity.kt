package demo.victormunoz.githubusers.features.userDetails

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import demo.victormunoz.githubusers.App
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.User
import kotlinx.android.synthetic.main.activity_user_detail.*
import javax.inject.Inject

private const val INTENT_USER = "user"

fun Context.userDetailsActivity(user: User): Intent {
    return Intent(this, UserDetailsActivity::class.java).apply {
        putExtra(INTENT_USER, user)
    }

}

class UserDetailsActivity : RxAppCompatActivity(), UserDetailsContract.PresenterListener {

    companion object {
        private const val USER_DETAIL_FRAGMENT = "user's detail fragment"
    }

    @Inject
    lateinit var mActionsListener: UserDetailsContract.UserActionsListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(toolbar)
        injectDependencies()
        initFragment(UserDetailsFragment.newInstance())
        mActionsListener.getUserDetails(getUser().loginName, getUser().avatarUrl)
        supportPostponeEnterTransition()
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        supportPostponeEnterTransition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mActionsListener.onBackPressed()
        supportFinishAfterTransition()
        super.onBackPressed()
    }

    //presenter listener
    override fun displayUserDetails(user: User, bitmap: Bitmap) {
        setViews(user, bitmap)
        supportStartPostponedEnterTransition()
        startRevealAnimation(gradient, 200)
    }

    override fun showErrorMessage() {
        supportStartPostponedEnterTransition()
        val errorMessage = getString(R.string.error_downloading_users_profile)
        val snackbar = Snackbar.make(user_avatar, errorMessage, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) {
            mActionsListener.getUserDetails(getUser().loginName, getUser().avatarUrl)
        }
        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        val cloudError = ResourcesCompat.getDrawable(resources, R.drawable.icon_cloud_error, null)
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudError, null, null, null)
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.margin_normal)
        snackbar.show()
    }


    /**
     * use Android Palette Library to create ShapeDrawable with a gradient effect
     */
    private val gradient: ShapeDrawable
        get() {
            val bitmap = (user_avatar.drawable as BitmapDrawable).bitmap
            val palette = Palette.from(bitmap).generate()
            val muted = palette.getMutedColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
            val mutedDark = palette.getDarkMutedColor(ContextCompat.getColor(baseContext, R.color.colorPrimaryDark))
            val x = app_bar.width
            val mDrawable = ShapeDrawable(RectShape())
            val gradient = LinearGradient(0f, 0f, x.toFloat(), 0f, muted, mutedDark, Shader.TileMode.REPEAT)
            mDrawable.paint.shader = gradient
            return mDrawable

        }

    private fun getUser(): User {
        return intent.extras.getParcelable(INTENT_USER)
    }


    private fun startRevealAnimation(gradient: ShapeDrawable, delay: Long) {
        val centerX = ((user_avatar.parent as LinearLayout).x + user_avatar.width / 2).toInt()
        val centerY = ((user_avatar.parent as LinearLayout).y + user_avatar.height / 2).toInt()
        val startRadio = (user_avatar.width / 4).toFloat()
        val endRadio = app_bar.width.toFloat()
        val reveal = ViewAnimationUtils.createCircularReveal(app_bar, centerX, centerY, startRadio, endRadio)
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

    }

    private fun injectDependencies() {
        (application as App).getUserComponent(this).inject(this)
    }

    private fun setViews(user: User, bitmap: Bitmap) {
        user_avatar.setImageBitmap(bitmap)
        user_name.text = user.fullName
        user_email.text = user.email
        user_login.text = user.loginName
        user_github.visibility = View.VISIBLE
        user_github.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(user.gitHubProfileUrl))
            startActivity(browserIntent)
        }
        if (user.blog != null && Patterns.WEB_URL.matcher(user.blog).matches()) {
            user_blog.visibility = View.VISIBLE
            user_blog.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(user.blog))
                startActivity(browserIntent)
            }
        }

        val detailFragment = supportFragmentManager.findFragmentByTag(USER_DETAIL_FRAGMENT) as? UserDetailsFragment
        detailFragment?.displayUserInfo(user)
    }

}


