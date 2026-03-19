package com.kevinzamora.temporis_androidapp.ui.auth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val GOOGLE_SIGN_IN = 100
    lateinit var progressBarLogin : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // setSupportActionBar(findViewById(R.id.toolbar))

        //declaraciones
        val btnLogin = this.findViewById<Button>(R.id.btnRegistroRegistrar)
        val btnRegistro = this.findViewById<Button>(R.id.btnLoginRegistro)
        val etLoginEmail = this.findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = this.findViewById<EditText>(R.id.etRegistroContra)
        val btnLoginGoogle = this.findViewById<Button>(R.id.btnLoginGoogle)
        val btnLoginNuevaContra = this.findViewById<Button>(R.id.btnLoginNuevaContra)
        progressBarLogin= this.findViewById<ProgressBar>(R.id.progressBarLogin)
        auth = FirebaseAuth.getInstance()

        //para cargar la sesion si el usuario la ha iniciado previamente
        cargarSesion()
        //se piden los permisos
        pedirMultiplesPermisos()

        btnLogin.setOnClickListener(View.OnClickListener {
            //se comprueba que el correo y contraseña esten bien rellenos
            if (!etLoginEmail.text.toString().isEmpty() && !etLoginPassword.text.toString()
                    .isEmpty()
            ) {

                //se accede al acceso de Firebase
                progressBarLogin.setVisibility(View.VISIBLE)

                auth.signInWithEmailAndPassword(
                    etLoginEmail.text.toString(),
                    etLoginPassword.text.toString()
                ).addOnCompleteListener {
                    //si se ha completado se guardan las preferencias y nos vamos a la pantalla principal de la app
                    if (it.isSuccessful) {
                        //Guardado de datos de sesion
                        val prefs: SharedPreferences.Editor = getSharedPreferences(
                            getString(R.string.prefs_file),
                            Context.MODE_PRIVATE
                        ).edit()
                        prefs.putString("email", etLoginEmail.text.toString())
                        prefs.putString("password", etLoginPassword.text.toString())
                        prefs.apply()

                        progressBarLogin.setVisibility(View.GONE)
                        //pasamos al home de la aplicacion
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Datos incorrectos!", Toast.LENGTH_SHORT).show()
                        progressBarLogin.setVisibility(View.GONE)


                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Revisa tu conexion!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Rellene todos los campos!", Toast.LENGTH_SHORT).show()
            }


        })


        btnLoginGoogle.setOnClickListener(View.OnClickListener {

            val googleConf =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
                    getString(
                        R.string.default_web_client_id
                    )
                ).requestEmail().build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)

        })

        btnRegistro.setOnClickListener(View.OnClickListener {

            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            val Registro = RegisterFragment()
            transaction.replace(R.id.coordinatorLayout, Registro)
            transaction.addToBackStack(null)
            transaction.commit()


        })

        btnLoginNuevaContra.setOnClickListener(View.OnClickListener {
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            val ContraOlvidada = ForgottenPassword()
            transaction.replace(R.id.coordinatorLayout, ContraOlvidada)
            transaction.addToBackStack(null)
            transaction.commit()


        })


    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java) ?: throw Exception("Cuenta nula")
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid.orEmpty()
                            val displayName = user?.displayName ?: "Sin nombre a mostrar"
                            val email = user?.email ?: "prueba@prueba.es"
                            val photo = user?.photoUrl?.toString() ?: "/SinFoto/"
                            val userDB = User(uid, displayName, email, displayName/*, "Tu descripción"*/, photo)

                            FirebaseDatabase.getInstance("https://tenisclubdroid-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("usuarios").child(uid).setValue(userDB)

                            goToMain()
                        } else {
                            showToast("Error al iniciar sesión con Google")
                        }
                    }
            } catch (e: Exception) {
                showToast("Error de autenticación: ${e.message}")
            } finally {
                progressBarLogin.visibility = View.GONE
            }
        }
    }

    fun Random.nextInt(range: IntRange): Int {
        return range.start + nextInt(range.last - range.start)
    }

    private fun cargarSesion() {
        val prefs: SharedPreferences =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val password = prefs.getString("password", null)

        if (email != null && password != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun pedirMultiplesPermisos() {
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // ccomprbamos si tenemos los permisos de todos ellos
                    if (report.areAllPermissionsGranted()) {
                        //Toast.makeText(getContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                    }

                    // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        // abrimos un diálogo a los permisos
                        //openSettingsDialog();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError?) {

                }
            })
            .onSameThread()
            .check()
    }
}
