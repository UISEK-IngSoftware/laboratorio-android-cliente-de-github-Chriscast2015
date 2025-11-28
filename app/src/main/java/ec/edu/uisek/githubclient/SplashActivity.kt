package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.services.RetrofitClient
import ec.edu.uisek.githubclient.services.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Establecer la interfaz de la pantalla. Esto faltaba y causaba el error.
        setContentView(R.layout.activity_splash)

        val sessionManager = SessionManager(this)
        val credentials = sessionManager.getCredentials()

        // 2. Decidir a qué pantalla ir basado en si el usuario ya inició sesión.
        if (credentials != null) {
            // Si hay credenciales, configurar el cliente de red e ir a la pantalla principal.
            RetrofitClient.createAuthenticatedClient(credentials.username, credentials.password)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // Si no hay credenciales, ir a la pantalla de Login.
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 3. Cerrar la SplashActivity para que el usuario no pueda volver a ella.
        finish()
    }
}
