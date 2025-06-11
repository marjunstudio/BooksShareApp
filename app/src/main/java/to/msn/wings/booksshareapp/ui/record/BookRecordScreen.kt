package to.msn.wings.booksshareapp.ui.record

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
import java.time.LocalDate
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

            // 読書期間セクションを更新
            ReadPeriodSection(
                bookEntity = bookEntity,
                onEditClick = {
                    editTarget = EditTarget.ReadPeriod
                    showEditDialog = true
                },
                onDeleteClick = {
                    viewModel.updateBook(
                        bookEntity.copy(
                            readStartDate = null,
                            readEndDate = null
                        )
                    )
                }
            )

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

            HorizontalDivider()

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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReadPeriodSection(
    bookEntity: BookEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
            Text(
                text = when {
                    bookEntity.readStartDate != null && bookEntity.readEndDate != null -> {
                        "${bookEntity.readStartDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))} 〜 ${bookEntity.readEndDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))}"
                    }
                    bookEntity.readStartDate != null -> {
                        bookEntity.readStartDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                    }
                    else -> "未設定"
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Row {
                if (bookEntity.readStartDate != null) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "読書期間を削除"
                        )
                    }
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "読書期間を編集")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (startDate: LocalDate?, endDate: LocalDate?) -> Unit,
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartDate?.atStartOfDay()?.toEpochSecond(java.time.ZoneOffset.UTC)?.times(1000),
        initialSelectedEndDateMillis = initialEndDate?.atStartOfDay()?.toEpochSecond(java.time.ZoneOffset.UTC)?.times(1000)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ヘッダー
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "読書期間を選択",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Row {
                            TextButton(onClick = onDismiss) {
                                Text("キャンセル")
                            }
                            TextButton(
                                onClick = {
                                    val startDate = dateRangePickerState.selectedStartDateMillis?.let {
                                        LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                                    }
                                    val endDate = dateRangePickerState.selectedEndDateMillis?.let {
                                        LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                                    }
                                    onConfirm(startDate, endDate)
                                }
                            ) {
                                Text("保存")
                            }
                        }
                    }
                }

                // DateRangePicker
                DateRangePicker(
                    state = dateRangePickerState,
                    title = null,
                    headline = null,
                    showModeToggle = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // 選択された期間の表示
                dateRangePickerState.selectedStartDateMillis?.let { startMillis ->
                    val startDate = LocalDate.ofEpochDay(startMillis / (24 * 60 * 60 * 1000))
                    val endDate = dateRangePickerState.selectedEndDateMillis?.let { 
                        LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = when {
                                    endDate != null -> "選択期間: ${startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))} 〜 ${endDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))}"
                                    else -> "選択日: ${startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))}"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // クリアボタン
                            TextButton(
                                onClick = { dateRangePickerState.setSelection(null, null) },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("選択をクリア")
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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

        when (editTarget) {
            EditTarget.ReadPeriod -> {
                DateRangePickerDialog(
                    onDismiss = onDismiss,
                    onConfirm = { startDate, endDate ->
                        val updatedBook = currentBook.copy(
                            readStartDate = startDate,
                            readEndDate = endDate
                        )
                        onSave(updatedBook)
                    },
                    initialStartDate = currentBook.readStartDate,
                    initialEndDate = currentBook.readEndDate
                )
            }
            else -> {
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text(
                            when (editTarget) {
                                EditTarget.ImpressiveQuote -> "読書メモを編集"
                                EditTarget.Trigger -> "きっかけを編集"
                                else -> ""
                            }
                        )
                    },
                    text = {
                        when (editTarget) {
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
                            else -> {}
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val updatedBook = when (editTarget) {
                                    EditTarget.ImpressiveQuote -> currentBook.copy(
                                        impressiveQuote = impressiveQuote.takeIf { it.isNotBlank() }
                                    )
                                    EditTarget.Trigger -> currentBook.copy(
                                        trigger = trigger.takeIf { it.isNotBlank() }
                                    )
                                    else -> currentBook
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
    }
} 