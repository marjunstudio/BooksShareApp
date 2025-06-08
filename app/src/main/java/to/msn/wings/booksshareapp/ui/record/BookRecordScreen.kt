package to.msn.wings.booksshareapp.ui.record

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import to.msn.wings.booksshareapp.data.local.entity.BookEntity
import to.msn.wings.booksshareapp.ui.auth.AuthViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookRecordScreen(
    bookId: String,
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: BookRecordViewModel = hiltViewModel()
) {
    val auth by authViewModel.authState.collectAsState()
    val book by viewModel.book.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<EditTarget?>(null) }
    
    LaunchedEffect(bookId) {
        auth.userId?.let { viewModel.loadBook(bookId, it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        book?.let { bookEntity ->
            // 書籍情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 表紙画像
                AsyncImage(
                    model = bookEntity.thumbnailUrl,
                    contentDescription = bookEntity.title,
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(0.7f),
                    contentScale = ContentScale.Crop
                )

                // 書籍情報
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = bookEntity.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    bookEntity.authors?.let { authors ->
                        Text(
                            text = authors,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "登録日: ${bookEntity.regDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Divider()

            // 読書期間
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "読書期間",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "開始日: ${bookEntity.readStartDate?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) ?: "未設定"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "終了日: ${bookEntity.readEndDate?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) ?: "未設定"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(onClick = {
                        editTarget = EditTarget.ReadPeriod
                        showEditDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "読書期間を編集")
                    }
                }
            }

            Divider()

            // 読書メモ
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "読書メモ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        editTarget = EditTarget.ImpressiveQuote
                        showEditDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "読書メモを編集")
                    }
                }
                Text(
                    text = bookEntity.impressiveQuote ?: "メモはありません",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider()

            // 読もうと思ったきっかけ
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "読もうと思ったきっかけ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        editTarget = EditTarget.Trigger
                        showEditDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "きっかけを編集")
                    }
                }
                Text(
                    text = bookEntity.trigger ?: "未記入",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showEditDialog && editTarget != null) {
        EditDialog(
            book = book,
            editTarget = editTarget!!,
            onDismiss = {
                showEditDialog = false
                editTarget = null
            },
            onSave = { updatedBook ->
                viewModel.updateBook(updatedBook)
                showEditDialog = false
                editTarget = null
            }
        )
    }
}

enum class EditTarget {
    ReadPeriod,
    ImpressiveQuote,
    Trigger
}

@Composable
fun EditDialog(
    book: BookEntity?,
    editTarget: EditTarget,
    onDismiss: () -> Unit,
    onSave: (BookEntity) -> Unit
) {
    book?.let { currentBook ->
        var impressiveQuote by remember { mutableStateOf(currentBook.impressiveQuote ?: "") }
        var trigger by remember { mutableStateOf(currentBook.trigger ?: "") }
        var readStartDate by remember { mutableStateOf(currentBook.readStartDate) }
        var readEndDate by remember { mutableStateOf(currentBook.readEndDate) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    when (editTarget) {
                        EditTarget.ReadPeriod -> "読書期間を編集"
                        EditTarget.ImpressiveQuote -> "読書メモを編集"
                        EditTarget.Trigger -> "きっかけを編集"
                    }
                )
            },
            text = {
                when (editTarget) {
                    EditTarget.ReadPeriod -> {
                        Column {
                            // 読書期間の編集UI（DatePickerなど）を実装
                            Text("読書期間の編集UIをここに実装")
                        }
                    }
                    EditTarget.ImpressiveQuote -> {
                        OutlinedTextField(
                            value = impressiveQuote,
                            onValueChange = { impressiveQuote = it },
                            label = { Text("読書メモ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    EditTarget.Trigger -> {
                        OutlinedTextField(
                            value = trigger,
                            onValueChange = { trigger = it },
                            label = { Text("きっかけ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedBook = when (editTarget) {
                            EditTarget.ReadPeriod -> currentBook.copy(
                                readStartDate = readStartDate,
                                readEndDate = readEndDate
                            )
                            EditTarget.ImpressiveQuote -> currentBook.copy(
                                impressiveQuote = impressiveQuote.takeIf { it.isNotBlank() }
                            )
                            EditTarget.Trigger -> currentBook.copy(
                                trigger = trigger.takeIf { it.isNotBlank() }
                            )
                        }
                        onSave(updatedBook)
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("キャンセル")
                }
            }
        )
    }
} 