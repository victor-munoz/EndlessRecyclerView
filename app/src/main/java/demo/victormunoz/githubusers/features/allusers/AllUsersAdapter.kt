package demo.victormunoz.githubusers.features.allusers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.User
import java.util.*

class AllUsersAdapter(
        private val adapterListener: AllUsersContract.AdapterListener
) : RecyclerView.Adapter<AllUsersViewHolder>() {

    private val usersList = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUsersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_all_users, parent, false)
        return AllUsersViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AllUsersViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onViewAttachedToWindow(holder: AllUsersViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.adapterPosition == usersList.size - 1) {
            adapterListener.onEndOfTheList()
        }
    }

    override fun onViewDetachedFromWindow(holder: AllUsersViewHolder) {
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    internal fun addUsers(users: List<User>) {
        val startPosition = usersList.size
        val numberOfUsers = startPosition + users.size
        usersList.addAll(users)
        notifyItemRangeInserted(startPosition, numberOfUsers)
    }


}
