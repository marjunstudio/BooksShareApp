package to.msn.wings.booksshareapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


/**
 * 書籍情報を保存するエンティティ
 * @property bookId 書籍ID
 * @property userId ユーザーID
 * @property title 書籍タイトル
 * @property authors 著者名
 * @property thumbnailUrl 書籍の表紙画像URL
 * @property impressiveQuote 印象に残った一文
 * @property trigger 読もうと思ったきっかけ
 * @property thoughts 本を読んだ感想
 * @property regDate マイリスト登録日
 * @property readStartDate 読書開始日
 * @property readEndDate 読書終了日
*/

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val bookId: String,
    val userId: String,
    val title: String,
    val authors: String?,
    val thumbnailUrl: String?,
    val impressiveQuote: String?,
    val trigger: String?,
    val thoughts: String?,
    val regDate: LocalDate,
    val readStartDate: LocalDate?,
    val readEndDate: LocalDate?,
) 