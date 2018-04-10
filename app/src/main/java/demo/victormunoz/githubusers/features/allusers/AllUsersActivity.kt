package demo.victormunoz.githubusers.features.allusers

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.annotation.VisibleForTesting
import android.support.design.widget.Snackbar
import android.support.test.espresso.IdlingResource
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import demo.victormunoz.githubusers.App
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.AdapterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener
import demo.victormunoz.githubusers.features.userDetails.userDetailsActivity
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.services.FloatingIconService
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import demo.victormunoz.githubusers.utils.recyclerview.MarginDecoration
import kotlinx.android.synthetic.main.activity_all_users.*
import javax.inject.Inject


class AllUsersActivity : RxAppCompatActivity(), PresenterListener, AdapterListener {

    companion object {
        private const val REQUEST_CODE = 5463
    }

    @Inject
    lateinit var mAllUsersAdapter: AllUsersAdapter

    @Inject
    lateinit var mViewListener: AllUsersContract.ViewListener

    val countingIdlingResource: IdlingResource
        @VisibleForTesting
        get() = EspressoIdlingResource.idlingResource


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)
        setDependencyInjection()
        setSupportActionBar(tool_bar)
        setRecyclerView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestPermission()
        }
    }

    override fun onDestroy() {
        recycler_view.adapter = mAllUsersAdapter
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            startService(Intent(this, FloatingIconService::class.java))
        }
        super.onDestroy()
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowTitleEnabled(true)
    }


    private fun setDependencyInjection() {
        (application as App).getUsersComponent(this).inject(this)
    }

    private fun setRecyclerView() {
        recycler_view!!.adapter = mAllUsersAdapter
        val numColumns = resources.getInteger(R.integer.columns)
        val itemMargin = resources.getDimensionPixelSize(R.dimen.margin_small)
        recycler_view!!.setHasFixedSize(true)
        recycler_view!!.layoutManager = GridLayoutManager(this, numColumns)
        recycler_view!!.addItemDecoration(MarginDecoration(itemMargin, numColumns))
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(resources.getString(R.string.package_uri, packageName)))
        startActivityForResult(intent, REQUEST_CODE)
    }

    //presenter listener
    override fun addUsers(users: List<User>) {
        mAllUsersAdapter.addUsers(users)
    }

    override fun goToUserDetails(view: View, user: User) {
        val options = ActivityOptions.makeSceneTransitionAnimation(this, view, view.transitionName)
        startActivity(userDetailsActivity(user), options.toBundle())
    }

    override fun showError() {
        var errorMessage = getString(R.string.error_downloading_more_users)
        if (mAllUsersAdapter.itemCount == 0) {
            errorMessage = getString(R.string.error_downloading_users)
        }
        val snackbar = Snackbar
                .make(github_logo, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { mViewListener.onRetry() }
        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        val cloudError = ResourcesCompat.getDrawable(resources, R.drawable.icon_cloud_error, null)
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudError, null, null, null)
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.margin_normal)
        snackbar.show()
    }

    //adapter Listener
    override fun onEndOfTheList() {
        mViewListener.onEndOfTheList()
    }

    override fun onItemClick(view: View, position: Int) {
        val user = mAllUsersAdapter.getItem(position)
        mViewListener.onItemClick(view, user)
    }


}
