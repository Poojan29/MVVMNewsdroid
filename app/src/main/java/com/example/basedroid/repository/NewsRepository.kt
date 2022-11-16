package com.example.basedroid.repository

import com.example.basedroid.api.RetrofitInstance
import com.example.basedroid.db.ArticleDatabase
import com.example.basedroid.model.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageCount: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageCount)

    suspend fun getSearchNews(searchQuery: String, pageCount: Int) = RetrofitInstance.api.searchForNews(searchQuery, pageCount)

    suspend fun saveArticle(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedArticle() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}