package demo.victormunoz.githubusers.di.component

import dagger.Component
import demo.victormunoz.githubusers.di.module.UserDetailsPresenterModule
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.userDetails.UserDetailsActivity

@ActivityScope
@Component(modules = [(UserDetailsPresenterModule::class)])
interface UserDetailsComponent {

    fun inject(activity: UserDetailsActivity)

}
