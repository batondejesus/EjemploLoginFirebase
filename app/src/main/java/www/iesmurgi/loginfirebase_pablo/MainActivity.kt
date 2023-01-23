package www.iesmurgi.loginfirebase_pablo

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        writeNewUser("test@test")
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
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
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            email,clave
        ).addOnCompleteListener{

            if (it.isSuccessful){
                //abrirPerfil()
            }else{
                Toast.makeText(getApplicationContext(),it.exception.toString(), Toast.LENGTH_SHORT)
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
}