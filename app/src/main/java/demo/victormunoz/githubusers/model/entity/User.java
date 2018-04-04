package demo.victormunoz.githubusers.model.entity;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unused")
public class User implements Serializable {
    private int id;
    private String companyName;
    private String blog;
    private String location;
    private String email;
    private boolean hireable;
    private int followers;
    private int following;
    @SerializedName("created_at")
    private Date joinedIn;
    @SerializedName("login")
    private String loginName;
    @SerializedName("avatar_url")
    private String avatarURL;
    @SerializedName("name")
    private String fullName;
    @SerializedName("public_repos")
    private int totalRepos;
    @SerializedName("public_gists")
    private int totalGists;
    @SerializedName("bio")
    private String biography;


    /**
     * format a number to a human readable format (k M G T P E)
     * <p>example: 1933 will be 1.9k</p>
     *
     * @param number positive number to be formatted
     * @return formatted String number
     * @see #getFollowers()
     * @see #getFollowing()
     * @see #getTotalGists()
     * @see #getTotalRepos()
     */
    private static String humanReadableFormat(@IntRange(from = 0) int number){
        if (number < 1000) return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format(Locale.getDefault(), "%.1f%c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public String getJoinedIn(){
        DateFormat df = new SimpleDateFormat("dd MMM yyyy ", Locale.US);
        return df.format(joinedIn);
    }

    public int getId(){
        return id;
    }

    public String getLoginName(){
        return loginName;
    }

    @NonNull
    public String getGitHubProfileUrl(){
        return "https://github.com/" + loginName;
    }

    public String getAvatarUrl(){
        return avatarURL;
    }

    public String getFullName(){
        return fullName;
    }

    public String getCompanyName(){
        return companyName;
    }

    public String getBlog(){
        String webSite = blog;
        if (webSite != null && !webSite.startsWith("http://") && !webSite.startsWith("https://")) {
            webSite = "http://" + webSite;
        }
        return webSite;
    }

    public String getLocation(){
        return location;
    }

    public String getEmail(){
        return email;
    }

    public boolean isHireable(){
        return hireable;
    }

    public String getBiography(){
        return biography;
    }

    public String getTotalRepos(){
        return humanReadableFormat(totalRepos);
    }

    public String getTotalGists(){
        return humanReadableFormat(totalGists);
    }

    public String getFollowers(){
        return humanReadableFormat(followers);
    }

    public String getFollowing(){
        return humanReadableFormat(following);
    }


}