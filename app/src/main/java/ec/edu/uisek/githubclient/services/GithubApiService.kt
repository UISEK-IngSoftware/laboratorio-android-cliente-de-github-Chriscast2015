package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE // nueva línea implementada
import retrofit2.http.GET
import retrofit2.http.PATCH // nueva línea implementada
import retrofit2.http.POST
import retrofit2.http.Path // nueva línea implementada
import retrofit2.http.Query

interface GithubApiService {

    @GET("user/repos")
    fun getRepos(
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc"
    ): Call<List<Repo>>

    @POST("user/repos")
    fun addRepo(   // NO se cambia tu nombre original
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    // -----------------------------
    // NUEVAS FUNCIONES IMPLEMENTADAS
    // -----------------------------

    @PATCH("repos/{owner}/{repo}") // nueva línea implementada
    fun updateRepo( // nueva línea implementada
        @Path("owner") owner: String, // nueva línea implementada
        @Path("repo") repoName: String, // nueva línea implementada
        @Body repoRequest: RepoRequest // nueva línea implementada
    ): Call<Repo> // nueva línea implementada

    @DELETE("repos/{owner}/{repo}") // nueva línea implementada
    fun deleteRepo( // nueva línea implementada
        @Path("owner") owner: String, // nueva línea implementada
        @Path("repo") repoName: String // nueva línea implementada
    ): Call<Void> // nueva línea implementada
}