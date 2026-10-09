package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exam
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ExamsScreen(
    exams: List<Exam>,
    selectedExam: Exam?,
    onSelectExam: (Exam) -> Unit,
    onEditExam: (Exam) -> Unit,
    onDeleteExam: (Exam) -> Unit,
    onShareQr: (Exam) -> Unit,
    onCreateNewExam: () -> Unit,
    onOpenQrImport: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(16.dp)
            .testTag("exams_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "পরীক্ষা ও উত্তরমালা",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Exam Configuration & Answer Keys",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Quick Import QR Button
                OutlinedButton(
                    onClick = onOpenQrImport,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("open_qr_import_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("QR ইম্পোর্ট", color = CyanAccent, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exams List
            if (exams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "কোনো পরীক্ষা তৈরি করা হয়নি",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onCreateNewExam,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Text("নতুন পরীক্ষা তৈরি করুন")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(exams, key = { it.id }) { exam ->
                        val isSelected = selectedExam?.id == exam.id
                        ExamCardItem(
                            exam = exam,
                            isSelected = isSelected,
                            onSelect = { onSelectExam(exam) },
                            onEdit = { onEditExam(exam) },
                            onDelete = { onDeleteExam(exam) },
                            onShareQr = { onShareQr(exam) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Exam
        FloatingActionButton(
            onClick = onCreateNewExam,
            containerColor = PrimaryIndigo,
            contentColor = TextWhite,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp)
                .testTag("create_exam_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exam")
        }
    }
}

@Composable
private fun ExamCardItem(
    exam: Exam,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShareQr: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF131D36) else DarkSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) PrimaryIndigo else DarkCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("exam_card_${exam.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = EmeraldCorrect,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = exam.title,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .background(EmeraldCorrect.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "বর্তমান সক্রিয় (Active)",
                            color = EmeraldCorrect,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details pills (Total questions, pass %, negative mark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoBadge(label = "প্রশ্ন", value = "${exam.totalQuestions} টি")
                InfoBadge(label = "পাশ মার্ক", value = "${exam.passPercentage}%")
                InfoBadge(label = "ভুলে কর্তন", value = "-${exam.negativeMark}")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onShareQr,
                    modifier = Modifier.size(36.dp).testTag("share_qr_btn_${exam.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "Share QR",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp).testTag("edit_exam_btn_${exam.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp).testTag("delete_exam_btn_${exam.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBadge(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
            .border(0.8.dp, DarkCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$label: ", color = TextMuted, fontSize = 11.sp)
            Text(text = value, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
