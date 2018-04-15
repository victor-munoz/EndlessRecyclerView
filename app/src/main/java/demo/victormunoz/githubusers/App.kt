package demo.victormunoz.githubusers

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources

import com.squareup.leakcanary.LeakCanary

import demo.victormunoz.githubusers.di.component.AllUsersComponent
import demo.victormunoz.githubusers.di.component.DaggerAllUsersComponent
import demo.victormunoz.githubusers.di.component.DaggerUserDetailsComponent
import demo.victormunoz.githubusers.di.component.UserDetailsComponent
import demo.victormunoz.githubusers.di.module.AllUsersPresenterModule
import demo.victormunoz.githubusers.di.module.ContextModule
import demo.victormunoz.githubusers.di.module.UserDetailsPresenterModule
import demo.victormunoz.githubusers.di.module.UsersAdapterModule

class App : Application() {

    companion object {
        lateinit var mResources: Resources
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        setLeakCanary()
        context = this.applicationContext
        setResources()
    }

    fun getUsersComponent(activity: Activity): AllUsersComponent {
        return DaggerAllUsersComponent.builder()
                .contextModule(ContextModule(activity))
                .usersAdapterModule(UsersAdapterModule(activity))
                .allUsersPresenterModule(AllUsersPresenterModule(activity))
                .build()
    }

    fun getUserComponent(activity: Activity): UserDetailsComponent {
        return DaggerUserDetailsComponent.builder()
                .contextModule(ContextModule(activity))
                .userDetailsPresenterModule(UserDetailsPresenterModule(activity))
                .build()
    }

    private fun setResources(){
        mResources = resources
    }

    private fun setLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }


}