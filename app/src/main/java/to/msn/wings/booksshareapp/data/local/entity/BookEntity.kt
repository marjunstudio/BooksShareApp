package to.msn.wings.booksshareapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val bookId: String,  // Google Books APIのID
    val title: String,
    val authors: String?, // カンマ区切りの著者名
    val thumbnailUrl: String?,
    val readDate: Date,
    val trigger: String?, // 読もうと思ったきっかけ
    val impressiveQuote: String?, // 印象に残った一文
    val thoughts: String?, // 感想
    val userId: String // Googleアカウントのユーザーid
) 