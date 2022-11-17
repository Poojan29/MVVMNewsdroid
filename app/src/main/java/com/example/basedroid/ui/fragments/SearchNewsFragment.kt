package com.example.basedroid.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basedroid.NewsActivity
import com.example.basedroid.R
import com.example.basedroid.adapters.NewsAdapter
import com.example.basedroid.ui.NewsViewModel
import com.example.basedroid.util.Constant
import com.example.basedroid.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        var job: Job? = null

        search_bar.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.isNotEmpty()) {
                        newsViewModel.getSearchNews(editable.toString())
                    } else {
                        updateNews()
                    }
                }
            }
        }

        updateNews()
    }

    private fun updateNews() {
        newsViewModel.searchNews.observe(
            viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressbar()
                        response.data?.let { newsResponse ->
                            newsAdapter.differ.submitList(newsResponse.articles.toList())
                            // we add + 2 == 1 > because integer division that is always rounded off
                            // and another 1 > last response always be empty so we don't need to consider that
                            val totalPages = newsResponse.totalResults / Constant.QUERY_PAGE_SIZE + 2
                            isLastPage = newsViewModel.searchNewsPage == totalPages
                        }
                    }
                    is Resource.Error -> {
                        hideProgressbar()
                        response.message?.let { message ->
                            Toast.makeText(activity, "an error occurred: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Loading -> {
                        showProgressbar()
                    }
                }

            }
        )
    }

    var isScrolling = false
    var isLastPage = false
    var isLoading = false

    var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
                Log.d(TAG, "Currently scrolling...")
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            Log.d(
                TAG, "\nFirstVisibleItemPosition = $firstVisibleItemPosition " +
                        "\nVisibleItemCount = $visibleItemCount " +
                        "\nTotalItemCount = $totalItemCount"
            )

//            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount)) {
//                Log.d(TAG, "On end you have yo fetch new data")
//            }

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            //QUERY_PAGE_SIZE = 20 in our case because each page contain 20 news request
            val isTotalMoreThanVisible = totalItemCount >= Constant.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage &&
                    isAtLastItem &&
                    isNotAtBeginning &&
                    isTotalMoreThanVisible &&
                    isScrolling

            if (shouldPaginate) {
                newsViewModel.getSearchNews(search_bar.text.toString())
                isScrolling = false
            } else {
                news_search_recycler_view.setPadding(0,0,0,0)
            }

        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        news_search_recycler_view.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }

    private fun hideProgressbar() {
        search_news_progress_bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressbar() {
        search_news_progress_bar.visibility = View.VISIBLE
        isLoading = true
    }

}