package to.msn.wings.booksshareapp.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.gson.Gson
import to.msn.wings.booksshareapp.data.remote.VolumeInfo

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 検索バー
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.padding(16.dp),
                singleLine = true,
                placeholder = { Text("書籍を検索") }
            )
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { viewModel.searchBooks(searchQuery) }
            ) {
                Text(text = "検索")
            }

        }

        // 検索結果
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val bookJson = Gson().toJson(book)
                                onNavigateToDetail(bookJson)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 書籍画像
                            AsyncImage(
                                model = book.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                                contentDescription = book.volumeInfo.title,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(120.dp),
                                contentScale = ContentScale.Fit
                            )

                            // 書籍情報
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = book.volumeInfo.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                book.volumeInfo.authors?.let { authors ->
                                    Text(
                                        text = authors.joinToString(", "),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    book: VolumeInfo,
    onBookSelected: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onBookSelected
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 書籍画像
            AsyncImage(
                model = book.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                contentDescription = book.volumeInfo.title,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )

            // 書籍情報
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.volumeInfo.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                book.volumeInfo.authors?.let { authors ->
                    Text(
                        text = authors.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
