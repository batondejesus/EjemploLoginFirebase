package www.iesmurgi.loginfirebase_pablo

import android.content.Intent
import android.os.Bundle
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
            logOut(user?.email.toString())
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

        }
    }
}