package www.iesmurgi.loginfirebase_pablo

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import www.iesmurgi.loginfirebase_pablo.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var user : FirebaseUser? = auth.currentUser

    private lateinit var bind : ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(bind.root)

        if (user != null){
            setup(user?.email.toString())
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //Aqui va lo que quiero que haga el boton al pulsar atras
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun setup(email: String) {

        var correo = bind.tvCorreo
        correo.text = email

        val btnCerrar = bind.btnCerrar
        btnCerrar.setOnClickListener{

            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}