package demo.victormunoz.githubusers.api.model;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class User implements Serializable {
    private int id;
    @SerializedName("created_at")
    private
    Date joinedOn;
    @SerializedName("login")
    private
    String loginName;
    @SerializedName("avatar_url")
    private
    String avatarURL;
    @SerializedName("name")
    private
    String fullName;
    @SerializedName("public_repos")
    private
    int repos;
    @SerializedName("public_gists")
    private
    int gists;
    @SerializedName("bio")
    private
    String biography;
    private String company;
    private String blog;
    private String location;
    private String email;
    private boolean hireable;
    private int followers;
    private int following;

    public String getJoinedOn() {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy ", Locale.US);
        return df.format(joinedOn);
    }

    public void setJoinedOn(@Nullable Date joinedOn) {
        this.joinedOn = joinedOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getGitHubProfileUrl() {
        return "https://github.com/"+loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getAvatarUrl() {
        return avatarURL;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarURL = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBlog() {
        String webSite= blog;
        if (webSite!=null &&!webSite.startsWith("http://") && !webSite.startsWith("https://")) {
            webSite = "http://" + webSite;
        }
        return webSite;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHireable() {
        return hireable;
    }

    public void setHireable(boolean hireable) {
        this.hireable = hireable;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getRepos() {
        return withSuffix(repos);
    }

    public void setRepos(int repos) {
        this.repos = repos;
    }

    public String getGists() {
        return withSuffix(gists);
    }

    public void setGists(int gists) {
        this.gists = gists;
    }

    public String getFollowers() {
        return withSuffix(followers);
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return withSuffix(following);
    }

    public void setFollowing(int following) {
        this.following = following;
    }
    /**
     * format a number adding k M G T P E format
     * <p>example: 1933 will be 1.9k</p>
     * @param number positive number to be formatted
     * @return formatted String number
     * @see #getFollowers()
     * @see #getFollowing()
     * @see #getGists()
     * @see #getRepos()
     */
    private static String withSuffix(@IntRange(from=0)int number) {
        if (number < 1000) return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f%c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp-1));
    }





}