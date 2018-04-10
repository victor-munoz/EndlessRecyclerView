package demo.victormunoz.githubusers.features.allusers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import java.util.ArrayList

import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.utils.picasso.ImageLoader
import kotlinx.android.synthetic.main.adapter_all_users.view.*

class AllUsersAdapter(private val imageLoader: ImageLoader, private val adapterListener: AllUsersContract.AdapterListener) : RecyclerView.Adapter<AllUsersViewHolder>() {

    private val usersList = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUsersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_all_users, parent, false)
        return AllUsersViewHolder(itemView, adapterListener, imageLoader)
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
        imageLoader.cancelRequest(holder.itemView.iv_avatar)
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        imageLoader.cancelAll()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    internal fun getItem(position: Int): User {
        return usersList[position]
    }

    internal fun addUsers(users: List<User>) {
        val startPosition = usersList.size
        val endPosition = startPosition + users.size - 1
        usersList.addAll(users)
        notifyItemRangeInserted(startPosition, endPosition)
    }


}
