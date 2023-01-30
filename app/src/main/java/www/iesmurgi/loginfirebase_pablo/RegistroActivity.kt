package www.iesmurgi.loginfirebase_pablo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.UploadTask
import com.google.firebase.database.FirebaseDatabase
import www.iesmurgi.loginfirebase_pablo.databinding.ActivityRegistroBinding
import java.io.IOException
import java.util.*

class RegistroActivity : AppCompatActivity() {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var bind : ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(bind.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnImagen: Button = bind.btnImagen
        val imagenPerfil: ImageView = bind.imagenPerfil

        btnImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        val btnRegister : Button = bind.btnRegister
        btnRegister.setOnClickListener{
            crearNuevoUsuario(bind.etUsuario.text.toString(), bind.etPassword.text.toString())
            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(bind.etUser.text.toString())
                .build()
            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Se ha añadido el username", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "NO Se ha añadido el username", Toast.LENGTH_SHORT).show()
                    }
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath: Uri = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                bind.imagenPerfil.setImageBitmap(bitmap)
                bind.imagenPerfil.visibility = View.VISIBLE


                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
                var uploadTask = imageRef.putFile(filePath)

                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener {
                    // Obtener la URL de la imagen subida
                    val uploadTask = storageRef.putFile(filePath)
                    val imageUrl = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            // Guarda la URL de la imagen en la base de datos de Firebase
                            val user = FirebaseAuth.getInstance().currentUser
                            val userRef = FirebaseDatabase.getInstance().getReference("users/${user?.uid}")
                            userRef.child("profile_image_url").setValue(downloadUri.toString())
                        } else {
                            // Handle unsuccessful download
                        }
                    }
            }
            }catch (e: IOException){
                print(e.printStackTrace())
            }
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
}