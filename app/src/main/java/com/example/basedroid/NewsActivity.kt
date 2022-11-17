package com.example.basedroid

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.basedroid.db.ArticleDatabase
import com.example.basedroid.repository.NewsRepository
import com.example.basedroid.ui.NewsViewModel
import com.example.basedroid.ui.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

//        val sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
//        sharedPref.edit().putBoolean(getString(R.string.is_saved_news_fragment_visited_first_time), true).apply()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val newsRepository = NewsRepository(ArticleDatabase.getArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        //Need to provide same id for menu resource file
        bottomNavigationView.setupWithNavController(navController)

    }

}