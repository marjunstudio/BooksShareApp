package to.msn.wings.booksshareapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import to.msn.wings.booksshareapp.data.local.entity.BookEntity

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSearch
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "書籍を追加"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val viewModel = hiltViewModel<HomeViewModel>()
            val books by viewModel.books.collectAsState()

            if (books.isEmpty()) {
                // 書籍が登録されていない場合
                Text(
                    text = "登録されている書籍がありません",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            } else {
                // 書籍一覧をグリッド表示
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(books) { book ->
                        BookItem(book = book)
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(book: BookEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f), // 本の表紙の一般的なアスペクト比
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = { /* TODO: 詳細画面への遷移 */ }
    ) {
        // 書籍の表紙画像
        AsyncImage(
            model = book.thumbnailUrl,
            contentDescription = book.title,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
} 