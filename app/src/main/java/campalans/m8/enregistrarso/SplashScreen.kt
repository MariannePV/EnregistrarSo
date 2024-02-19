package campalans.m8.enregistrarso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import campalans.m8.enregistrarso.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding   //Declarem una variable que inicialitzarem més tard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)   //Emplenem la variable binding amb tots els elements visuals del layout
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        //Definim els view amb el qual volem interactuar
        val titol : TextView = binding.titol
        val nom : TextView = binding.nom
        val logo : ImageView = binding.logo

        //Definim l'animació amb la que volem treballar
        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        //Apliquem l'animació als elements indicats
        titol.startAnimation(animFadeIn)
        nom.startAnimation(animFadeIn)
        logo.startAnimation(animFadeIn)

        //Amb aquest codi, el que aconseguim és mostrar el SplashScreen durant el temps indicat i llavors, automàticament canviar al MainWindow
        // Splashscreen delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            // Time in milliseconds
        }, 2100)
    }
}