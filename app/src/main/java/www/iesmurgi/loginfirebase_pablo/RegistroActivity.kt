package www.iesmurgi.loginfirebase_pablo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import www.iesmurgi.loginfirebase_pablo.databinding.ActivityRegistroBinding
import java.io.File

class RegistroActivity : AppCompatActivity() {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var PICK_IMAGE = 1

    private lateinit var bind : ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val btnImagen : Button = bind.btnImagen
        btnImagen.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
            val file = Uri.fromFile(File(imageUri.toString()))
            val storageRef = auth.getInstance().getReference(file.lastPathSegment)
            val uploadTask = storageRef.putFile(file)

            uploadTask.addOnFailureListener {
                // Ha ocurrido un error al subir la imagen
            }.addOnSuccessListener {
                // La imagen se ha subido correctamente
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val imagen : ImageView = bind.imageView
            imagen.setImageURI(imageUri)
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