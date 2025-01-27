package com.example.oneminute.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.oneminute.BuildConfig
import com.example.oneminute.R
import com.example.oneminute.db.ArticleDatabase
import com.example.oneminute.repository.NewsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

//import com.example.oneminute.databinding.ActivityMainBinding

class MainActivity_news : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel

    private lateinit var fragmentManager : FragmentManager
    //private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityMainBinding.inflate(layoutInflaterZ)

        Thread.sleep(2000)
        installSplashScreen()

        enableEdgeToEdge()

        setContentView(R.layout.activity_main_news)
        val fab = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        ViewCompat.setOnApplyWindowInsetsListener(fab) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. This solution sets
            // only the bottom, left, and right dimensions, but you can apply whichever
            // insets are appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }


        // Inizializzazione del ViewModel
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        /*intent esplicito
        val explicitButton: Button = findViewById<Button>(R.id.chatButton)

        //rendo il bottono cliccabile
        explicitButton.setOnClickListener{
            val explicitIntent = Intent(this, Chat::class.java)
            startActivity(explicitIntent)
        }

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

         */
    }
}