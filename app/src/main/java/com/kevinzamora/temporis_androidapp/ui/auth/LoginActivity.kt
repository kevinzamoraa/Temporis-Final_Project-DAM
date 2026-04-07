package com.kevinzamora.temporis_androidapp.ui.auth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.kevinzamora.temporis_androidapp.MainActivity
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kevinzamora.temporis_androidapp.ui.auth.Login.ForgottenPassword
import com.kevinzamora.temporis_androidapp.ui.auth.login.RegisterFragment
import java.util.*
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var progressBarLogin: ProgressBar

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Enlace de vistas
        val btnLogin = findViewById<Button>(R.id.btnRegistroRegistrar)
        val btnBiometric = findViewById<ImageButton>(R.id.btnBiometric)
        val btnIrARegistro = findViewById<Button>(R.id.btnLoginRegistro)
        val etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = findViewById<EditText>(R.id.etRegistroContra)
        val btnLoginGoogle = findViewById<Button>(R.id.btnLoginGoogle)
        val btnLoginNuevaContra = findViewById<Button>(R.id.btnLoginNuevaContra)
        progressBarLogin = findViewById(R.id.progressBarLogin)

        auth = FirebaseAuth.getInstance()
        executor = ContextCompat.getMainExecutor(this)

        setupBiometrics()

        // Lógica de inicio automático (Estilo Sabadell/ONCE)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("email", null)
        val savedPass = prefs.getString("password", null)

        // Si hay sesión activa en Firebase, entramos directo
        if (auth.currentUser != null) {
            goToMain()
        }
        // Si no hay sesión pero sí credenciales guardadas, lanzamos huella
        else if (savedEmail != null && savedPass != null) {
            biometricPrompt.authenticate(promptInfo)
        }

        // Botón manual de huella
        btnBiometric.setOnClickListener {
            if (savedEmail != null && savedPass != null) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Primero inicia sesión manualmente una vez", Toast.LENGTH_LONG).show()
            }
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val pass = etLoginPassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                progressBarLogin.visibility = View.VISIBLE
                performLogin(email, pass)
            } else {
                Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnLoginGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate que este ID existe en strings.xml o google-services
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut().addOnCompleteListener { // Forzamos elegir cuenta
                startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
            }
        }

        // Navegación a fragmentos
        btnIrARegistro.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.coordinatorLayout, RegisterFragment())
                .addToBackStack(null).commit()
        }

        btnLoginNuevaContra.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.coordinatorLayout, ForgottenPassword())
                .addToBackStack(null).commit()
        }

        pedirMultiplesPermisos()
    }

    private fun setupBiometrics() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
                    val email = prefs.getString("email", "") ?: ""
                    val pass = prefs.getString("password", "") ?: ""
                    if (email.isNotEmpty() && pass.isNotEmpty()) {
                        runOnUiThread { progressBarLogin.visibility = View.VISIBLE }
                        performLogin(email, pass)
                    }
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Si el usuario cancela, no hacemos nada, dejamos que use el teclado
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso Seguro Temporis")
            .setSubtitle("Identifícate para continuar")
            .setNegativeButtonText("Usar mi contraseña")
            .build()
    }

    private fun performLogin(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Guardamos credenciales para la próxima vez que use biometría
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.putString("email", email)
                    prefs.putString("password", pass)
                    prefs.apply()
                    goToMain()
                } else {
                    progressBarLogin.visibility = View.GONE
                    Toast.makeText(this, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToMain() {
        progressBarLogin.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                progressBarLogin.visibility = View.VISIBLE
                auth.signInWithCredential(credential).addOnCompleteListener { t ->
                    if (t.isSuccessful) {
                        goToMain()
                    } else {
                        progressBarLogin.visibility = View.GONE
                        Toast.makeText(this, "Error Auth Google: ${t.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                progressBarLogin.visibility = View.GONE
                // Si el error es 10, es un problema de SHA-1 en la consola de Firebase
                Toast.makeText(this, "Error Google (${e.statusCode}): Revisa SHA-1 en Firebase", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pedirMultiplesPermisos() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {}
                override fun onPermissionRationaleShouldBeShown(p: List<PermissionRequest?>?, t: PermissionToken) { t.continuePermissionRequest() }
            }).check()
    }
}
