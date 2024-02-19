package campalans.m8.enregistrarso

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import campalans.m8.enregistrarso.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding   //Declarem una variable que inicialitzarem més tard

    lateinit var statusTV: TextView
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    var mFileName: File? = null

    //Els botons amb els quals volem treballar
    lateinit var btnPlayPause : Button
    lateinit var btnRecord : Button
    lateinit var btnSave : Button
    var pause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)   //Emplenem la variable binding amb tots els elements visuals del layout
        setContentView(binding.root)

        //L'animació amb la que volem treballar
        val animBtn = AnimationUtils.loadAnimation(this, R.anim.btn_click)

        btnRecord = binding.btnRecord
        btnSave = binding.btnSave
        btnPlayPause = binding.btnPlayPause

        statusTV = binding.estatGravacio

        //Listener al botó de pausar i fer play
        btnPlayPause.setOnClickListener()
        {
            //Afegim l'animació al botó
            btnPlayPause.startAnimation(animBtn)

            //Segons l'estat del pause
            if (!pause)
            {
                //Mostrem el botó de pausa quan escoltem la gravació
                pause = true
                btnPlayPause.setBackgroundResource(R.drawable.btn_pause)
            } else {
                //Mostrem el botó de play quan pausem la gravació
                pause = false
                btnPlayPause.setBackgroundResource(R.drawable.btn_play)
            }

            playAudio()
        }

        //Animem i executem les funcions corresponents de la resta de botons

        //Botó per gravar
        btnRecord.setOnClickListener()
        {
            btnRecord.startAnimation(animBtn)
            startRecording()
        }

        //Botó per desar
        btnSave.setOnClickListener()
        {
            btnSave.startAnimation(animBtn)
            pauseRecording()
        }
    }

    private fun startRecording() {

        // Check permissions
        if (CheckPermissions()) {

            // Save file
            mFileName = File(getExternalFilesDir("")?.absolutePath,"Record.3gp")

            // If file exists then increment counter
            var n = 0
            while (mFileName!!.exists()) {
                n++
                mFileName = File(getExternalFilesDir("")?.absolutePath,"Record$n.3gp")
            }

            // Initialize the class MediaRecorder
            mRecorder = MediaRecorder()

            // Set source to get audio
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)

            // Set the format of the file
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

            // Set the audio encoder
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            // Set the save path
            mRecorder!!.setOutputFile(mFileName)
            try {
                // Preparation of the audio file
                mRecorder!!.prepare()
            } catch (e: IOException) {
                Log.e("TAG", "prepare() failed")
            }
            // Start the audio recording
            mRecorder!!.start()
            statusTV.text = "Gravació en procés"
        } else {
            // Request permissions
            RequestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If permissions accepted ->
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.size > 0) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {

                    // Message
                    Toast.makeText(applicationContext, "Permisos garantits", Toast.LENGTH_LONG).show()

                } else {

                    // Message
                    Toast.makeText(applicationContext, "Permisos denegats", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun CheckPermissions(): Boolean {

        // Check permissions
        val result =
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermissions() {

        // Request permissions
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE)
    }

    fun playAudio() {
        // Use the MediaPlayer class to listen to recorded audio files
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
            try {
                //Assignem la font d'on treurem la gravació
                mPlayer!!.setDataSource(mFileName.toString())

                //Quan la reproducció s'hagi completat
                mPlayer!!.setOnCompletionListener {
                    //Mostrarem el botó play i assignarem el mPlayer null
                    //Per així fer que en cas de que hi hagi una nova gravació, s'assigni com a mPlayer
                    statusTV.text = "Gravació finalitzada"
                    btnPlayPause.setBackgroundResource(R.drawable.btn_play)
                    pause = false
                    mPlayer = null
                }

                // Fetch the source of the mPlayer
                mPlayer!!.prepare()

                // Start the mPlayer
                mPlayer!!.start()
                statusTV.text = "Escoltant gravació"
            } catch (e: IOException) {
                Log.e("TAG", "prepare() failed")
            }
        } else {
            // En cas de que el mPlayer no sigui null i no s'estigui reproduint, representa que  està en pausa
            if (!mPlayer!!.isPlaying) {
                //Continuem amb la reproducció
                mPlayer!!.start()
                statusTV.text = "Escoltant gravació"
            } else {
                //En cas contrari, pausem la reproducció
                mPlayer!!.pause()
                statusTV.text = "Gravació pausada"
            }
        }
    }

    fun pauseRecording() {

        // Stop recording
        if (mFileName == null) {

            // Message
            Toast.makeText(getApplicationContext(), "No s'ha començat cap gravació", Toast.LENGTH_LONG).show()

        } else {
            mRecorder!!.stop()

            // Message to confirm save file
            val savedUri = Uri.fromFile(mFileName)
            val msg = "File saved: " + savedUri!!.lastPathSegment
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show()

            // Release the class mRecorder
            mRecorder!!.release()
            mRecorder = null
            statusTV.text = "Gravació finalitzada"
        }
    }

    companion object {
        const val REQUEST_AUDIO_PERMISSION_CODE = 1
    }
}