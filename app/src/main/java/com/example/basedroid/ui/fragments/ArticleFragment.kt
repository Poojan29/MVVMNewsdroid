package com.example.basedroid.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.View.OnClickListener
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.basedroid.NewsActivity
import com.example.basedroid.R
import com.example.basedroid.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var newsViewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).viewModel

        val article = args.article
        web_view.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        save_fab.setOnClickListener {
            newsViewModel.saveArticle(article)
            Snackbar.make(it, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

}