package to.msn.wings.booksshareapp.ui.reading

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReadingRecordScreen(
    viewModel: ReadingRecordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 月の選択
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.updateMonth(-1) }) {
                Text("<")
            }

            Text(
                text = state.currentDate.format(DateTimeFormatter.ofPattern("yyyy年MM月")),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = { viewModel.updateMonth(1) }) {
                Text(">")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // カレンダー
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount > 50) {
                                viewModel.updateMonth(-1)
                            } else if (dragAmount < -50) {
                                viewModel.updateMonth(1)
                            }
                        }
                    )
                }
        ) {
            Calendar(state.calendarDays)
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun Calendar(calendarDays: List<CalendarDay>) {
    Column {
        // 曜日のヘッダー
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("日", "月", "火", "水", "木", "金", "土").forEachIndexed { index, day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (index) {
                        0 -> Color.Red
                        6 -> Color.Blue
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // カレンダーの日付
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(calendarDays.size) { index ->
                val day = calendarDays[index]
                val isWeekend = index % 7 == 0 || index % 7 == 6
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .background(
                            when {
                                day.hasBook -> MaterialTheme.colorScheme.primaryContainer
                                !day.isCurrentMonth -> Color.LightGray.copy(alpha = 0.3f)
                                else -> Color.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.day.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            isWeekend && index % 7 == 0 -> Color.Red
                            isWeekend && index % 7 == 6 -> Color.Blue
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
} 