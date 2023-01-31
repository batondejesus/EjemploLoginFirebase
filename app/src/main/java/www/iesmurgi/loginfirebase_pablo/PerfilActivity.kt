package www.iesmurgi.loginfirebase_pablo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import www.iesmurgi.loginfirebase_pablo.databinding.ActivityPerfilBinding
import java.io.IOException
import java.util.*

class PerfilActivity : AppCompatActivity() {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var user : FirebaseUser? = auth.currentUser
    val database = FirebaseDatabase.getInstance()
    val reference = database.getReference("usuarios")

    private val PICK_IMAGE_REQUEST = 1


    private lateinit var bind : ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(bind.root)

        if (user != null){
            logOut(user?.email.toString())
        }
        val btnImagen: Button = bind.btnImagen
        val imagenPerfil: ImageView = bind.imagenPerfil
        btnImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        logOut(bind.tvCorreo.text.toString())

        val mibundle = intent.extras
        val nombre = mibundle?.getString("nombre")
        val email = mibundle?.getString("email")
        if (nombre.isNullOrBlank()){
            bind.tvCorreo.text = user?.email
            bind.tvNombre.text = user?.displayName
        }else{
            bind.tvNombre.text = nombre
            bind.tvCorreo.text = email
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun logOut(email: String) {

        var correo = bind.tvCorreo
        correo.text = email

        val btnCerrar = bind.btnCerrar
        btnCerrar.setOnClickListener{

            auth.signOut()
            val enviar = Intent(this, MainActivity::class.java)
            startActivity(enviar)

        }
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
                val imageRef = storageRef.child("imagenes/${UUID.randomUUID()}.jpg")
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
}