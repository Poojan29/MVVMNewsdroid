package com.example.basedroid.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basedroid.NewsActivity
import com.example.basedroid.R
import com.example.basedroid.adapters.NewsAdapter
import com.example.basedroid.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_bookmark_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BookmarkNewsFragment : Fragment(R.layout.fragment_bookmark_news) {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private val TAG = "BookmarkNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).viewModel

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            it.url?.let { it1 -> Log.d(TAG, it1) }
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_bookmarkNewsFragment_to_articleFragment,
                bundle
            )
        }

        newsViewModel.getSavedArticles().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        saved_news_recycler_view.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}