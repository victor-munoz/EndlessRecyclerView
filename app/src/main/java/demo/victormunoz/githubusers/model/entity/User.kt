package demo.victormunoz.githubusers.model.entity

import android.os.Parcelable
import android.support.annotation.IntRange
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class User(
        val id: Int = 0,
        val companyName: String?,
        val location: String?,
        val email: String?,
        val isHireable: Boolean,
        @SerializedName("login")
        val loginName: String,
        @SerializedName("avatar_url")
        val avatarUrl: String,
        @SerializedName("name")
        val fullName: String?,
        @SerializedName("bio")
        val biography: String?,
        @SerializedName("created_at")
        private val _joinedIn: Date?,
        @SerializedName("public_repos")
        private val _totalRepos: Int,
        @SerializedName("public_gists")
        private val _totalGists: Int,
        @SerializedName("blog")
        private val _blog: String?,
        @SerializedName("followers")
        private val _followers: Int,
        @SerializedName("following")
        private val _following: Int

): Parcelable {
    val gitHubProfileUrl
        get() : String {
            return "https://github.com/$loginName"
        }

    val joinedIn
        get() :String {
            val df = SimpleDateFormat("dd MMM yyyy ", Locale.US)
            return df.format(_joinedIn)
        }
    val blog
        get(): String? {
            return if (_blog == null || _blog.startsWith("http://") || _blog.startsWith("https://")) {
                _blog
            } else {
                "http://$_blog"
            }
        }
    val totalRepos
        get(): String {
            return humanReadableFormat(_totalRepos)
        }
    val totalGists
        get(): String {
            return humanReadableFormat(_totalGists)
        }
    val followers
        get(): String {
            return humanReadableFormat(_followers)
        }
    val following
        get(): String {
            return humanReadableFormat(_following)
        }

    private fun humanReadableFormat(@IntRange(from = 0) number: Int): String {
        if (number < 1000) return "" + number
        val exp = (Math.log(number.toDouble()) / Math.log(1000.0)).toInt()
        return String.format(Locale.getDefault(), "%.1f%c", number / Math.pow(1000.0, exp.toDouble()), "kMGTPE"[exp - 1])
    }

}