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
import com.kevinzamora.temporis_androidapp.SessionLifecycleManager
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

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("email", null)
        val savedPass = prefs.getString("password", null)

        // IMPORTANTE: Para cumplir con el requisito de "cerrar sesión al salir",
        // aquí podríamos forzar un signOut si no viene de una navegación interna.
        if (auth.currentUser != null) {
            goToMain()
        }
        else if (!savedEmail.isNullOrEmpty() && !savedPass.isNullOrEmpty()) {
            etLoginEmail.setText(savedEmail)
            etLoginPassword.setText(savedPass)
            window.decorView.post { biometricPrompt.authenticate(promptInfo) }
        }

        btnBiometric.setOnClickListener {
            if (!prefs.getString("email", null).isNullOrEmpty()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Inicia sesión manualmente una vez", Toast.LENGTH_LONG).show()
            }
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val pass = etLoginPassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                progressBarLogin.visibility = View.VISIBLE
                performLogin(email, pass)
            }
        }

        btnLoginGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut().addOnCompleteListener {
                startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
            }
        }

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
                        performLogin(email, pass)
                    }
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Temporis 2.0")
            .setSubtitle("Autenticación biométrica")
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun performLogin(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit().apply {
                        putString("email", email)
                        putString("password", pass)
                        apply()
                    }
                    goToMain()
                } else {
                    progressBarLogin.visibility = View.GONE
                    val errorMsg = task.exception?.localizedMessage ?: "Credenciales incorrectas"
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToMain() {
        progressBarLogin.visibility = View.GONE

        // AVISAMOS al gestor de que esto es un cambio controlado
        SessionLifecycleManager.isChangingConfiguration = true

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
                        val firebaseUser = auth.currentUser
                        if (firebaseUser != null) {
                            val userObj = User(
                                firebaseUser.uid,
                                firebaseUser.displayName?.replace("\\s+".toRegex(), "")?.lowercase() ?: "user",
                                firebaseUser.email ?: "",
                                firebaseUser.displayName ?: "Usuario Temporis",
                                firebaseUser.photoUrl?.toString() ?: "android.resource://${packageName}/${R.drawable.ic_default_profile}"
                            )
                            userObj.rol = 1

                            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(firebaseUser.uid)
                                .set(userObj, com.google.firebase.firestore.SetOptions.merge())
                                .addOnSuccessListener { goToMain() }
                                .addOnFailureListener { goToMain() }
                        }
                    } else {
                        progressBarLogin.visibility = View.GONE
                        Toast.makeText(this, "Error Auth Google: ${t.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                progressBarLogin.visibility = View.GONE
                Toast.makeText(this, "Error Google (${e.statusCode})", Toast.LENGTH_LONG).show()
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