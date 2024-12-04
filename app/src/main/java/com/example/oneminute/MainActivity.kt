package com.example.oneminute

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.example.oneminute.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager : FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityMainBinding.inflate(layoutInflaterZ)

        Thread.sleep(2000)
        installSplashScreen()

        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*intent esplicito
        val explicitButton: Button = findViewById<Button>(R.id.chatButton)

        //rendo il bottono cliccabile
        explicitButton.setOnClickListener{
            val explicitIntent = Intent(this, Chat::class.java)
            startActivity(explicitIntent)
        }
        */
        val listaNotizie: ListView = findViewById<ListView>(R.id.lista)
        val listItem = arrayOf(
            "Notizia 1", "Notizia 2", "Notizia 3",
            "Notizia 4", "Notizia 5", "Notizia 6",
            "Notizia 4", "Notizia 5", "Notizia 6",
            "Notizia 4", "Notizia 5", "Notizia 6",
            "Notizia 4", "Notizia 5", "Notizia 6",
            "Notizia 4", "Notizia 5", "Notizia 6",
            "Notizia 4", "Notizia 5", "Notizia 6"
        )

        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,listItem)
        listaNotizie.adapter = listAdapter
        
        listaNotizie.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            Toast.makeText(this, "Hai cliccato su $selectedItem", Toast.LENGTH_SHORT).show()
        }

        val logo: ImageView = findViewById<ImageView>(R.id.logo)

        val testo: TextView = findViewById<TextView>(R.id.Scritta_Benvenuto)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}