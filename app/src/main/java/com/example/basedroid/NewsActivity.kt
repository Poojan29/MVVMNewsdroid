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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val newsRepository = NewsRepository(ArticleDatabase.getArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        //Need to provide same id for menu resource file
        bottomNavigationView.setupWithNavController(navController)

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.options_menu, menu)
//
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//
//        (menu?.findItem(R.id.search)?.actionView as SearchView).apply {
//
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//
//        }
//
//        return true
//    }

}