package www.iesmurgi.loginfirebase_pablo

import android.R
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import www.iesmurgi.loginfirebase_pablo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = bind.toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.title = "Nombre de la Aplicación"
        val btnLogueo : Button = bind.btnLogueo
        btnLogueo.setOnClickListener{
            iniciarSesion(bind.etUsuario.text.toString(), bind.etPassword.text.toString())
        }
    }


    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
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

    fun crearNuevoUsuario(email: String, clave: String) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            email, clave
        ).addOnCompleteListener{

            if (it.isSuccessful){
                //abrirPerfil()
            }else{
                Toast.makeText(getApplicationContext(),"Email o contraseña incorrectos", Toast.LENGTH_SHORT)
            }
        }
    }

    fun iniciarSesion(email:String, clave: String)  {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, clave)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "HAS INICIADO SESION", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "ERROR: NO HAS INICIADO SESION", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {

            Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
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
}