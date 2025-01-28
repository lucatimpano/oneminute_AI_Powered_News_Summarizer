package com.example.oneminute.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String = "Unknown Author",
    val content: String = "No Content",
    val description: String = "No Description",
    val publishedAt: String = "Unknown Date",
    var source: Source? = null,
    val title: String = "Untitled",
    val url: String = "No URL",
    val urlToImage: String = "No Image URL"
): Serializable{

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (publishedAt?.hashCode() ?: 0)
        result = 31 * result + (source?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (urlToImage?.hashCode() ?: 0)
        return result
    }
}