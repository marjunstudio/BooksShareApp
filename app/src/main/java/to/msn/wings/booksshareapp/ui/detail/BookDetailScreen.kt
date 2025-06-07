package to.msn.wings.booksshareapp.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import to.msn.wings.booksshareapp.data.remote.VolumeInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: VolumeInfo,
    userId: String,
    viewModel: BookDetailViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val saveStatus by viewModel.saveBookStatus.collectAsState()

    // 保存状態の監視
    LaunchedEffect(saveStatus) {
        when (saveStatus) {
            is SaveBookStatus.Success -> {
                android.widget.Toast.makeText(
                    context,
                    "書籍を保存しました",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                onNavigateToHome()
            }
            is SaveBookStatus.Error -> {
                android.widget.Toast.makeText(
                    context,
                    (saveStatus as SaveBookStatus.Error).message,
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("書籍詳細") },
                actions = {
                    IconButton(onClick = { viewModel.saveBook(book, userId) }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "保存"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 書籍画像
            AsyncImage(
                model = book.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                contentDescription = book.volumeInfo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            // 書籍情報セクション
            DetailSection(title = "書籍情報") {
                DetailItem("タイトル", book.volumeInfo.title)
                book.volumeInfo.authors?.let { authors ->
                    DetailItem("著者", authors.joinToString(", "))
                }
                book.volumeInfo.pageCount?.let { pageCount ->
                    DetailItem("総ページ数", "$pageCount ページ")
                }
                book.volumeInfo.printType?.let { printType ->
                    DetailItem("出版タイプ", when(printType) {
                        "BOOK" -> "書籍"
                        "MAGAZINE" -> "雑誌"
                        else -> printType
                    })
                }
                book.volumeInfo.categories?.let { categories ->
                    DetailItem("カテゴリ", categories.joinToString(", "))
                }
                if (book.volumeInfo.averageRating != null && book.volumeInfo.ratingsCount != null) {
                    DetailItem("評価", "★${book.volumeInfo.averageRating} (${book.volumeInfo.ratingsCount}件の評価)")
                }
            }

            // 販売情報セクション
            book.saleInfo?.let { saleInfo ->
                DetailSection(title = "販売情報") {
                    DetailItem("形式", if (saleInfo.isEbook) "電子書籍" else "紙の書籍")
                    DetailItem("販売状況", when(saleInfo.saleability) {
                        "FOR_SALE" -> "販売中"
                        "FREE" -> "無料"
                        "NOT_FOR_SALE" -> "現在販売していません"
                        "FOR_PREORDER" -> "予約受付中"
                        else -> saleInfo.saleability
                    })
                    saleInfo.listPrice?.let { listPrice ->
                        DetailItem("定価", "¥${listPrice.amount}")
                    }
                    saleInfo.buyLink?.let { buyLink ->
                        OutlinedButton(
                            onClick = { /* TODO: 購入リンクを開く */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("購入ページを開く")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 