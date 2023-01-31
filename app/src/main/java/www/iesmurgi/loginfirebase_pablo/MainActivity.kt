package www.iesmurgi.loginfirebase_pablo


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import www.iesmurgi.loginfirebase_pablo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        auth = FirebaseAuth.getInstance()


        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val btnLogueo : Button = bind.btnLogueo
        btnLogueo.setOnClickListener{
            iniciarSesion(bind.etUsuario.text.toString(), bind.etPassword.text.toString())
        }
        val btnRegister : Button = bind.btnRegister
        btnRegister.setOnClickListener{
            val enviar1 = Intent(this, RegistroActivity::class.java)
            startActivity(enviar1)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)


        bind.btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }
    }


    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(www.iesmurgi.loginfirebase_pablo.R.menu.acerca_de, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            www.iesmurgi.loginfirebase_pablo.R.id.action_acerca_de -> {
                //Aqui puedes abrir un dialogo o una activity con la informacion de quien desarrollo la aplicacion
                Toast.makeText(this, "Aplicación desarrollada por Pablo Alvarez Relat", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }



    fun iniciarSesion(email:String, clave: String)  {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, clave)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this, PerfilActivity::class.java))
                    } else {
                        Toast.makeText(this, "Email y/o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val name = account.displayName
                val email = account.email
                val idToken = account.idToken

                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("name", name)
                editor.putString("email", email)
                editor.putString("idToken", idToken)
                editor.apply()

                val enviar = Intent(this, PerfilActivity::class.java)
                enviar.putExtra("nombre",name)
                enviar.putExtra("email",email)
                startActivity(enviar)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesion con google", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun writeNewUser(email: String){
         val db = Firebase.firestore

        val data = hashMapOf(
            "email" to email,
            "usuario" to "nouser",
            "nacionalidad" to "default-nacionality",
            "edad" to "0"
        )

        db.collection("user").document(email)
            .set(data)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot correctamente escrito") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error al escribir el documento", e) }
    }

    fun goToIniciarSesion(){
        val enviar1 = Intent(this, PerfilActivity::class.java)
        enviar1.putExtra("nombre","a")
        startActivity(enviar1)
    }
}