package ec.edu.uisek.githubclient

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {
    private lateinit var binding: ActivityRepoFormBinding
    private val apiService: GithubApiService = RetrofitClient.getApiService()
    private var repoToEdit: Repo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RECIBIR OBJETO REPO DESDE MAIN
        repoToEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_REPO", Repo::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_REPO") as? Repo
        }

        // CONFIGURAR SI ES EDICIÓN O CREACIÓN
        if (repoToEdit != null) {
            prepareEditMode(repoToEdit!!)
        } else {
            prepareCreateMode()
        }

        binding.cancelButton.setOnClickListener { finish() }
    }

    // -------------------------
    //     MODO EDICIÓN
    // -------------------------
    private fun prepareEditMode(repo: Repo) {
        supportActionBar?.title = "Editar Repositorio"
        binding.saveButton.text = "Actualizar"

        // Cargar datos existentes
        binding.repoNameInput.setText(repo.name)
        binding.repoDescriptionInput.setText(repo.description)

        // ---------------------------
        // AGREGADO DEL CÓDIGO GUÍA
        // Desactivar el nombre (GitHub NO permite cambiarlo)
        // ---------------------------
        binding.repoNameInput.isEnabled = false
        binding.repoNameInput.alpha = 0.5f

        binding.saveButton.setOnClickListener { handleUpdate(repo) }
    }

    // -------------------------
    //     MODO CREACIÓN
    // -------------------------
    private fun prepareCreateMode() {
        supportActionBar?.title = "Nuevo Repositorio"
        binding.saveButton.text = "Crear"

        binding.repoNameInput.isEnabled = true
        binding.repoNameInput.alpha = 1.0f

        binding.saveButton.setOnClickListener { createRepo() }
    }

    // -------------------------
    //  CREAR REPOSITORIO
    // -------------------------
    private fun createRepo() {
        if (!validateForm()) return

        val repoName = binding.repoNameInput.text.toString().trim()
        val repoDescription = binding.repoDescriptionInput.text.toString().trim()

        val repoRequest = RepoRequest(repoName, repoDescription)
        val call = apiService.addRepo(repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No Autorizado"
                        403 -> "Prohibido"
                        404 -> "No Encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage("Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                val errorMsg = "Error al crear el repositorio: ${t.message}"
                Log.e("RepoForm", errorMsg, t)
                showMessage(errorMsg)
            }
        })
    }

    // -------------------------
    //  ACTUALIZAR REPOSITORIO
    // -------------------------
    private fun handleUpdate(originalRepo: Repo) {
        if (!validateForm()) return

        val newDescription = binding.repoDescriptionInput.text.toString().trim()

        // ---------------------------
        // AGREGADO DEL CÓDIGO GUÍA
        // Siempre enviar el nombre original
        // ---------------------------
        val repoRequest = RepoRequest(
            name = originalRepo.name,
            description = newDescription
        )

        apiService.updateRepo(
            originalRepo.owner.login,
            originalRepo.name,
            repoRequest
        ).enqueue(object : Callback<Repo> {

            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado exitosamente")
                    finish()
                } else {
                    showMessage("Error al actualizar: ${response.code()}")
                    Log.e("RepoForm", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo de conexión al actualizar: ${t.message}")
            }
        })
    }

    private fun validateForm(): Boolean {
        val repoName = binding.repoNameInput.text.toString()

        if (repoName.isBlank()) {
            binding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }
        if (repoName.contains(" ")) {
            binding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }

        binding.repoNameInput.error = null
        return true
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}