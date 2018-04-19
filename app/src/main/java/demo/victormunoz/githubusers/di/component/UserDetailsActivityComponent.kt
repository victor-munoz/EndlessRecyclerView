package demo.victormunoz.githubusers.di.component

import dagger.Component
import demo.victormunoz.githubusers.di.module.UserDetailsActivityPresenterModule
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.userDetails.UserDetailsActivity

@ActivityScope
@Component(modules = [(UserDetailsActivityPresenterModule::class)])
interface UserDetailsActivityComponent {

    fun inject(activity: UserDetailsActivity)

}
