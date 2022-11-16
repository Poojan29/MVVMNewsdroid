package com.example.basedroid.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basedroid.R
import com.example.basedroid.model.Article
import kotlinx.android.synthetic.main.single_news_component.view.*

/**
 * DiffUtil => calculate differences between two lists which runs in background thread
 * and update those items which are different
**/

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.single_news_component,
                parent,
                false
            )
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).placeholder(R.drawable.placeholder)
                .into(news_image_view)
            news_title.text = article.title
            news_description.text = article.description
            news_time.text = article.publishedAt?.split("T")?.get(0)

        }
        holder.itemView.card_view.setOnClickListener{
            onItemClickListener?.let {
                it(article)
            }
        }
//        holder.itemView.save_button.setOnClickListener{
//            onItemClickListener?.let {
//                it(article)
//            }
//        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}