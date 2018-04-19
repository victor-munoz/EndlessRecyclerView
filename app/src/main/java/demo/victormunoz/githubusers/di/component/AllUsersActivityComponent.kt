package demo.victormunoz.githubusers.di.component

import dagger.Component
import demo.victormunoz.githubusers.di.module.AllUsersActivityPresenterModule
import demo.victormunoz.githubusers.di.module.AllUsersAdapterModule
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersActivity

@ActivityScope
@Component(modules = [(AllUsersActivityPresenterModule::class), (AllUsersAdapterModule::class)])
interface AllUsersActivityComponent {

    fun inject(activity: AllUsersActivity)

}
