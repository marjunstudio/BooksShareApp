package to.msn.wings.booksshareapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import to.msn.wings.booksshareapp.data.local.converter.DateConverter
import to.msn.wings.booksshareapp.data.local.dao.BookDao
import to.msn.wings.booksshareapp.data.local.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
} 