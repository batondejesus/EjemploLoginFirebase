package www.iesmurgi.loginfirebase_pablo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import www.iesmurgi.loginfirebase_pablo.databinding.ActivityRegistroBinding
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegistroActivity : AppCompatActivity() {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()


    private lateinit var bind : ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(bind.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val btnRegister : Button = bind.btnRegister
        btnRegister.setOnClickListener{
            crearNuevoUsuario(bind.etUsuario.text.toString(), bind.etPassword.text.toString())
        }

        val etPassword : EditText = bind.etPassword
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Verifica si el contenido tiene 6 o más caracteres
                if (s?.length!! >= 6) {
                    // Si tiene 6 o más caracteres, cambia el color del borde a verde
                    etPassword.setBackgroundResource(R.drawable.rounded_edittext)
                } else {
                    // Si tiene menos de 6 caracteres, cambia el color del borde a rojo
                    etPassword.setBackgroundResource(R.drawable.rounded_edittext_error)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }


    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val pattern: Pattern = Pattern.compile(emailPattern)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }


    fun crearNuevoUsuario(email: String, clave: String) {

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Por favor, introduzca un email válido", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, clave)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(bind.etNombre.text.toString())
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Se ha creado un nuevo usuario", Toast.LENGTH_SHORT).show()
                                val enviar = Intent(this, PerfilActivity::class.java)
                                startActivity(enviar)
                            } else {
                                Toast.makeText(this, "Error al asignar displayname", Toast.LENGTH_SHORT).show()
                                print(updateTask.exception)
                            }
                        }
                } else {
                    Toast.makeText(this, "Usuario ya existente", Toast.LENGTH_SHORT).show()
                    print(task.exception)
                }
            }
    }
}