package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.util.Log // nueva línea implementada
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // nueva línea implementada
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    private val apiService: GithubApiService = RetrofitClient.gitHubApiService // nueva línea implementada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView() // nueva línea implementada

        binding.newRepoFab.setOnClickListener{
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(
            onEditClicked = { repo -> // nueva línea implementada
                displayEditRepoForm(repo) // nueva línea implementada
            },
            onDeleteClicked = { repo -> // nueva línea implementada
                showDeleteConfirmationDialog(repo) // nueva línea implementada
            }
        ) // nueva línea implementada

        binding.reposRecyclerView.adapter = reposAdapter
    }

    // -------------------------------
    // CONFIRMAR ELIMINACIÓN
    // -------------------------------
    private fun showDeleteConfirmationDialog(repo: Repo) { // nueva función implementada
        AlertDialog.Builder(this) // nueva línea implementada
            .setTitle("Confirmar eliminación") // nueva línea implementada
            .setMessage("¿Seguro deseas eliminar '${repo.name}'?") // nueva línea implementada
            .setPositiveButton("Eliminar") { _, _ -> // nueva línea implementada
                deleteRepository(repo) // nueva línea implementada
            }
            .setNegativeButton("Cancelar", null) // nueva línea implementada
            .show() // nueva línea implementada
    }

    // -------------------------------
    // ELIMINAR REPOSITORIO DESDE LA API
    // -------------------------------
    private fun deleteRepository(repo: Repo) { // nueva función implementada
        val call = apiService.deleteRepo(repo.owner.login, repo.name) // nueva línea implementada

        call.enqueue(object : Callback<Void> { // nueva línea implementada
            override fun onResponse(call: Call<Void>, response: Response<Void>) { // nueva línea implementada
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado correctamente") // nueva línea implementada
                    fetchRepositories() // nueva línea implementada
                } else {
                    showMessage("Error al eliminar: ${response.code()}") // nueva línea implementada
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) { // nueva línea implementada
                showMessage("Error de red al eliminar") // nueva línea implementada
                Log.e("MainActivity", "Error al eliminar", t) // nueva línea implementada
            }
        })
    }

    private fun fetchRepositories() {
        val call = apiService.getRepos()

        call.enqueue(object: Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
                if(response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    val errorMessage = when(response.code()){
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
                showMessage("No se pudieron cargar los repositorios")
            }
        })
    }

    private fun showMessage (message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // -------------------------------
    // ABRIR FORMULARIO PARA EDITAR
    // -------------------------------
    private fun displayEditRepoForm(repo: Repo) { // nueva función implementada
        val intent = Intent(this, RepoForm::class.java).apply { // nueva línea implementada
            putExtra("EXTRA_REPO", repo) // nueva línea implementada
        }
        startActivity(intent) // nueva línea implementada
    }

    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }
}