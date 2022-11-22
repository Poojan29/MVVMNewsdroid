package com.example.basedroid.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basedroid.NewsActivity
import com.example.basedroid.R
import com.example.basedroid.adapters.NewsAdapter
import com.example.basedroid.ui.NewsViewModel
import com.example.basedroid.util.Constant.Companion.QUERY_PAGE_SIZE
import com.example.basedroid.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    private val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).viewModel

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        updateNewsRecyclerView()
    }

    private fun updateNewsRecyclerView() {
        newsViewModel.breakingNews.observe(
            viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressbar()
                        response.data?.let { newsResponse ->
                            newsAdapter.differ.submitList(newsResponse.articles.toList())
                            // we add + 2 == 1 > because integer division that is always rounded off
                            // and another 1 > last response always be empty so we don't need to consider that
                            val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                            isLastPage = newsViewModel.breakingNewsPage == totalPages
                        }
                    }
                    is Resource.Error -> {
                        hideProgressbar()
                        response.message?.let { message ->
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
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

    private var scrollListener = object : RecyclerView.OnScrollListener() {
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage &&
                    isAtLastItem &&
                    isNotAtBeginning &&
                    isTotalMoreThanVisible &&
                    isScrolling

            if (shouldPaginate) {
                newsViewModel.getBreakingNews("in")
                isScrolling = false
            } else {
                news_recycler_view.setPadding(0,0,0,0)
            }

        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        news_recycler_view.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }

    private fun hideProgressbar() {
        progress_bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressbar() {
        progress_bar.visibility = View.VISIBLE
        isLoading = true
    }

}