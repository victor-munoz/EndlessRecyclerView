package demo.victormunoz.githubusers.features.allusers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import java.util.ArrayList

import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.network.image.ImageDownloadService

class AllUsersAdapter (
        private val imageService: ImageDownloadService,
        private val adapterListener: AllUsersContract.AdapterListener

) : RecyclerView.Adapter<AllUsersViewHolder>() {

    private val usersList = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUsersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_all_users, parent, false)
        return AllUsersViewHolder(itemView, adapterListener, imageService)
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
        //picassoService.cancelRequest(holder.itemView.iv_avatar)//todo
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        //picassoService.cancelAll()//todo
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
