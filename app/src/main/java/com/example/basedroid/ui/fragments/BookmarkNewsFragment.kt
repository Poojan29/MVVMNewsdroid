package com.example.basedroid.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basedroid.NewsActivity
import com.example.basedroid.R
import com.example.basedroid.adapters.NewsAdapter
import com.example.basedroid.ui.NewsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_bookmark_news.*

class BookmarkNewsFragment : Fragment(R.layout.fragment_bookmark_news) {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private val TAG = "BookmarkNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isFirsTimeVisit = activity?.getSharedPreferences(
            getString(R.string.preference_file),
            Context.MODE_PRIVATE
        )
            ?.getBoolean(getString(R.string.is_saved_news_fragment_visited_first_time), true)

        if (isFirsTimeVisit == true) {
            context?.let {
                MaterialAlertDialogBuilder(it)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message)
                    .show()
            }
            activity?.getSharedPreferences(
                getString(R.string.preference_file),
                Context.MODE_PRIVATE
            )
                ?.edit()
                ?.putBoolean(getString(R.string.is_saved_news_fragment_visited_first_time), false)
                ?.apply()
        }

        Log.d("TAG", isFirsTimeVisit.toString())

        newsViewModel = (activity as NewsActivity).viewModel

        if (newsViewModel.getSavedArticles().value == null) {
            no_news.visibility = View.VISIBLE
        } else {
            no_news.visibility = View.INVISIBLE
        }

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

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP  or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(saved_news_recycler_view)
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