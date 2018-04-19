package demo.victormunoz.githubusers

import android.app.Activity
import android.app.Application
import android.content.res.Resources
import android.support.v7.widget.RecyclerView

import com.squareup.leakcanary.LeakCanary
import demo.victormunoz.githubusers.di.component.*
import demo.victormunoz.githubusers.di.module.*

class App : Application() {

    companion object {
        lateinit var mResources: Resources
    }

    override fun onCreate() {
        super.onCreate()
        setLeakCanary()
        setResources()
    }

    fun getUsersComponent(activity: Activity): AllUsersActivityComponent {
        return DaggerAllUsersActivityComponent.builder()
                .contextModule(ContextModule(activity))
                .allUsersAdapterModule(AllUsersAdapterModule())
                .allUsersActivityPresenterModule(AllUsersActivityPresenterModule())
                .build()
    }

    fun getUserComponent(activity: Activity): UserDetailsActivityComponent {
        return DaggerUserDetailsActivityComponent.builder()
                .contextModule(ContextModule(activity))
                .userDetailsActivityPresenterModule(UserDetailsActivityPresenterModule())
                .build()
    }

    fun getUsersViewHolderComponent(viewHolder: RecyclerView.ViewHolder): AllUsersViewHolderComponent {
        return DaggerAllUsersViewHolderComponent.builder()
                .contextModule(ContextModule(viewHolder.itemView.context))
                .allUsersViewHolderPresenterModule(AllUsersViewHolderPresenterModule(viewHolder))
                .build()
    }

    private fun setResources() {
        mResources = resources
    }

    private fun setLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }


}