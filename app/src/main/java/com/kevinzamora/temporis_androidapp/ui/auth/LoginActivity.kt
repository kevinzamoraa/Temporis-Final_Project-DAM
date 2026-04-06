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

    private val GOOGLE_SIGN_IN = 100
    private lateinit var progressBarLogin: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de Vistas
        val btnLogin = findViewById<Button>(R.id.btnRegistroRegistrar)
        val btnBiometric = findViewById<ImageButton>(R.id.btnBiometric)
        val btnRegistro = findViewById<Button>(R.id.btnLoginRegistro)
        val etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = findViewById<EditText>(R.id.etRegistroContra)
        val btnLoginGoogle = findViewById<Button>(R.id.btnLoginGoogle)
        val btnLoginNuevaContra = findViewById<Button>(R.id.btnLoginNuevaContra)
        progressBarLogin = findViewById(R.id.progressBarLogin)

        auth = FirebaseAuth.getInstance()
        executor = ContextCompat.getMainExecutor(this)

        // 1. Configurar Biometría
        setupBiometrics()

        // 2. Verificar si existe sesión previa para ofrecer Biometría
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("email", null)
        val savedPass = prefs.getString("password", null)

        if (savedEmail != null && savedPass != null) {
            // Desplegar automáticamente la ventana de huella
            biometricPrompt.authenticate(promptInfo)
        }

        // 3. Listeners
        btnBiometric.setOnClickListener {
            if (savedEmail != null && savedPass != null) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Primero inicia sesión manualmente una vez", Toast.LENGTH_LONG).show()
            }
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString()
            val pass = etLoginPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                progressBarLogin.visibility = View.VISIBLE
                performLogin(email, pass)
            } else {
                Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnLoginGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        btnRegistro.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.coordinatorLayout, RegisterFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnLoginNuevaContra.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.coordinatorLayout, ForgottenPassword())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        pedirMultiplesPermisos()
    }

    private fun setupBiometrics() {
        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
                    val email = prefs.getString("email", "")!!
                    val pass = prefs.getString("password", "")!!

                    progressBarLogin.visibility = View.VISIBLE
                    performLogin(email, pass)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Error o cancelación del usuario, no cerramos la app
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso Biométrico")
            .setSubtitle("Usa tu huella para entrar")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun performLogin(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // GUARDADO DE SESIÓN Y TIMESTAMP PARA CADUCIDAD
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.putString("email", email)
                    prefs.putString("password", pass)
                    prefs.putLong("last_login_time", System.currentTimeMillis()) // Timestamp actual
                    prefs.apply()

                    goToMain()
                } else {
                    progressBarLogin.visibility = View.GONE
                    Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                progressBarLogin.visibility = View.GONE
                Toast.makeText(this, "Fallo de red o servidor", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToMain() {
        progressBarLogin.visibility = View.GONE
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // En Google también guardamos el timestamp
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putLong("last_login_time", System.currentTimeMillis())
                        prefs.apply()
                        goToMain()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Google Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pedirMultiplesPermisos() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {}
                override fun onPermissionRationaleShouldBeShown(p: List<PermissionRequest?>?, t: PermissionToken) {
                    t.continuePermissionRequest()
                }
            }).check()
    }
}
