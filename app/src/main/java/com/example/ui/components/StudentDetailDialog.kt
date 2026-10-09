package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExamResult
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.StudentAnalytics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentDetailDialog(
    analytics: StudentAnalytics,
    onDismiss: () -> Unit
) {
    val student = analytics.student
    val results = analytics.resultsHistory.sortedBy { it.timestamp }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
                .testTag("student_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = student.name,
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "রোল নম্বর: ${student.roll} • পারফরম্যান্স এনালাইসিস",
                            color = CyanAccent,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_student_detail")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Cards (Total Exams, Avg Score, Highest Score, Pass Rate)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "মোট পরীক্ষা",
                        value = "${analytics.totalExamsTaken}",
                        color = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "গড় স্কোর",
                        value = "${analytics.averageScore}",
                        color = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "সর্বোচ্চ",
                        value = "${analytics.highestScore}",
                        color = EmeraldCorrect,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "পাসের হার",
                        value = "${analytics.passRatePercentage}%",
                        color = if (analytics.passRatePercentage >= 80) EmeraldCorrect else Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Student Performance Graph (অগ্রগতি গ্রাফ)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "অগ্রগতির গ্রাফ (Performance Trend Graph)",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Canvas Chart
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                ) {
                    if (results.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "এখনও কোনো পরীক্ষার ডাটা পাওয়া যায়নি",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        PerformanceChart(results)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "বিগত পরীক্ষার ফলাফল (Exam History)",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Results History List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (results.isEmpty()) {
                        item {
                            Text(
                                text = "এই রোল নম্বরের কোনো পরীক্ষার ইতিহাস নেই। OMR স্ক্যান করলে এখানে ডাটা যুক্ত হবে।",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        items(results.reversed()) { res ->
                            ResultHistoryItem(res)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceChart(results: List<ExamResult>) {
    val scores = results.map { it.score.toFloat() }
    val maxScore = maxOf(100f, (scores.maxOrNull() ?: 100f) + 10f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        val width = size.width
        val height = size.height

        // Draw horizontal grid lines
        val gridLines = 3
        for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        if (scores.size == 1) {
            // Single point
            val x = width / 2f
            val y = height - (scores[0] / maxScore) * height
            drawCircle(
                color = CyanAccent,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            return@Canvas
        }

        val stepX = width / (scores.size - 1).toFloat()
        val path = Path()

        scores.forEachIndexed { index, score ->
            val x = index * stepX
            val y = height - (score / maxScore) * height

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // Stroke line
        drawPath(
            path = path,
            color = CyanAccent,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw data points
        scores.forEachIndexed { index, score ->
            val x = index * stepX
            val y = height - (score / maxScore) * height

            drawCircle(
                color = Color(0xFF0F172A),
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = CyanAccent,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ResultHistoryItem(res: ExamResult) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(res.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
            .border(0.8.dp, DarkCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = res.examTitle,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$dateStr • সঠিক: ${res.correctCount} | ভুল: ${res.wrongCount}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${res.score} Marks",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .background(
                            if (res.isPassed) EmeraldCorrect.copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (res.isPassed) "PASS" else "FAIL",
                        color = if (res.isPassed) EmeraldCorrect else Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
