package to.msn.wings.booksshareapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val bookId: String,  // Google Books APIのID
    val userId: String,
    val title: String,
    val authors: String?, // カンマ区切りの著者名
    val description: String?,
    val thumbnailUrl: String?,
    val impressiveQuote: String?, // 印象に残った一文
    val trigger: String?, // 読もうと思ったきっかけ
    val thoughts: String?,
    val regDate: LocalDate,  // 登録日
    val readStartDate: LocalDate?,  // 読書開始日
    val readEndDate: LocalDate?,    // 読書終了日
) 