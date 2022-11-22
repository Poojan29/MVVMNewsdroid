package com.example.basedroid

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuCompat
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
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
    private lateinit var networkReceiver: NetworkReceiver
    private val TAG = "NewsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val newsRepository = NewsRepository(ArticleDatabase.getArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        networkReceiver = NetworkReceiver(viewModel)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)

        //Need to provide same id for menu resource file
        bottomNavigationView.setupWithNavController(navController)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.options_menu, menu)
//        val searchItem : MenuItem = menu.findItem(R.id.search)
//        val searchView: SearchView = searchItem.actionView as SearchView
//
//        searchQuery(searchView)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    private fun searchQuery(searchView: SearchView) {
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newString: String?): Boolean {
//                Log.d(TAG, "News message $newString")
//                return true
//            }
//        })
//    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkReceiver)
    }

}