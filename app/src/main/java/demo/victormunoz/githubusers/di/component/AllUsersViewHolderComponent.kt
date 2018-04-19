package demo.victormunoz.githubusers.di.component

import dagger.Component
import demo.victormunoz.githubusers.di.module.AllUsersViewHolderPresenterModule
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersViewHolder

@ActivityScope
@Component(modules = [(AllUsersViewHolderPresenterModule::class)])
interface AllUsersViewHolderComponent {

    fun inject(viewHolder: AllUsersViewHolder)

}
