package www.iesmurgi.loginfirebase_pablo

import android.R
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnLogueo : Button = bind.btnLogueo
        btnLogueo.setOnClickListener{
            iniciarSesion(bind.etUsuario.text.toString(), bind.etPassword.text.toString())
        }

        val btnRegister : Button = bind.btnRegister
        btnRegister.setOnClickListener{
            crearNuevoUsuario(bind.etUsuario.text.toString(), bind.etPassword.text.toString())
        }

        val etPassword : EditText = bind.etPassword
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Verifica si el contenido tiene 6 o más caracteres
                if (s?.length!! >= 6) {
                    // Si tiene 6 o más caracteres, cambia el color del fondo a verde
                    etPassword.setBackgroundColor(Color.WHITE)
                } else {
                    // Si tiene menos de 6 caracteres, cambia el color del fondo a rojo
                    etPassword.setBackgroundColor(Color.RED)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
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
                Toast.makeText(this, "Se ha creado un nuevo usuario", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Usuario ya existente", Toast.LENGTH_SHORT).show()
                print(it.result)
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