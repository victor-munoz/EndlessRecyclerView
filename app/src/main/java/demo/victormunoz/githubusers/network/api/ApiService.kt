package demo.victormunoz.githubusers.network.api

import demo.victormunoz.githubusers.model.User
import io.reactivex.Observable
import io.reactivex.Single

interface ApiService {

    fun getUsers(sinceId: Int): Observable<List<User>>

    fun getUser(id: String): Single<User>
}