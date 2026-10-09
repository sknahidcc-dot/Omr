package com.example.ui.components

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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.omr.OmrEvaluationResult
import com.example.omr.QuestionEvaluation
import com.example.omr.QuestionStatus
import com.example.ui.theme.AmberMultiple
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ResultDialog(
    evaluation: OmrEvaluationResult,
    examTitle: String,
    studentName: String?,
    onDismiss: () -> Unit
) {
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
                .testTag("result_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "OMR Evaluation Result",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = examTitle,
                            color = PrimaryIndigo,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_result_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Student Roll & Name Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceVariant, RoundedCornerShape(14.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "শিক্ষার্থী (Student)",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = studentName ?: "New Student",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(PrimaryIndigo.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, PrimaryIndigo.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Roll: ${evaluation.studentRoll}",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score Ring & Pass/Fail Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (evaluation.isPassed) Color(0xFF064E3B).copy(alpha = 0.4f)
                        else Color(0xFF450A0A).copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (evaluation.isPassed) EmeraldCorrect.copy(alpha = 0.6f)
                            else CrimsonWrong.copy(alpha = 0.6f),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "মোট স্কোর (Net Score)",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${evaluation.totalScore} / ${evaluation.totalQuestions}",
                                color = TextWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "শতকরা: ${evaluation.percentage}%",
                                color = if (evaluation.isPassed) EmeraldCorrect else CrimsonWrong,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Pass/Fail Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    if (evaluation.isPassed) EmeraldCorrect else CrimsonWrong,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (evaluation.isPassed) "উত্তীর্ণ (PASS)" else "অনুত্তীর্ণ (FAIL)",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4 Breakdown Metric Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        title = "সঠিক",
                        count = evaluation.correctCount.toString(),
                        color = EmeraldCorrect,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        title = "ভুল",
                        count = evaluation.wrongCount.toString(),
                        color = CrimsonWrong,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        title = "দাগায়নি",
                        count = evaluation.blankCount.toString(),
                        color = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        title = "মাইনাস",
                        count = "-${evaluation.negativeMarksDeducted}",
                        color = CrimsonWrong,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "প্রশ্নোত্তর বিবরণী (Question-by-Question Analysis)",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Question Breakdown List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(evaluation.questions) { q ->
                        QuestionRowItem(q)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("done_result_button")
                ) {
                    Text("সম্পন্ন হয়েছে (Done)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 11.sp
            )
            Text(
                text = count,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuestionRowItem(q: QuestionEvaluation) {
    val (statusColor, statusIcon, statusLabel) = when (q.status) {
        QuestionStatus.CORRECT -> Triple(EmeraldCorrect, Icons.Default.CheckCircle, "সঠিক (+1)")
        QuestionStatus.WRONG -> Triple(CrimsonWrong, Icons.Default.Cancel, "ভুল (${q.scoreDelta})")
        QuestionStatus.MULTIPLE_MARKED -> Triple(AmberMultiple, Icons.Default.Warning, "একাধিক দাগানো (${q.scoreDelta})")
        QuestionStatus.UNATTEMPTED -> Triple(TextMuted, Icons.Default.HelpOutline, "ফাঁকা (0)")
    }

    val optionMapping = mapOf("A" to "ক", "B" to "খ", "C" to "গ", "D" to "ঘ", "E" to "ঙ")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF141B2D), RoundedCornerShape(10.dp))
            .border(0.8.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(Color(0xFF1E293B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${q.questionNumber}",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))

                val chosen = when (q.selectedOption) {
                    "BLANK" -> "ফাঁকা"
                    "MULTIPLE" -> "একাধিক"
                    else -> "${q.selectedOption} (${optionMapping[q.selectedOption] ?: ""})"
                }
                val correct = "${q.correctOption} (${optionMapping[q.correctOption] ?: ""})"

                Column {
                    Text(
                        text = "দাগিয়েছে: $chosen",
                        color = if (q.status == QuestionStatus.CORRECT) EmeraldCorrect else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "সঠিক উত্তর: $correct",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = statusLabel,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
