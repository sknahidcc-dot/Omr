package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Exam
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun AnswerKeyEditorDialog(
    initialExam: Exam?,
    onSave: (id: Long, title: String, totalQuestions: Int, passPercentage: Double, negativeMark: Double, answerKeys: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialExam?.title ?: "New Exam") }
    var totalQuestionsText by remember { mutableStateOf((initialExam?.totalQuestions ?: 100).toString()) }
    var passPercentageText by remember { mutableStateOf((initialExam?.passPercentage ?: 80.0).toString()) }
    var negativeMarkText by remember { mutableStateOf((initialExam?.negativeMark ?: 0.25).toString()) }

    val totalQ = totalQuestionsText.toIntOrNull() ?: 100
    val keysList = remember(initialExam) {
        val existing = initialExam?.getAnswerKeyList() ?: List(100) { "A" }
        val list = mutableStateListOf<String>()
        list.addAll(existing)
        while (list.size < 100) list.add("A")
        list
    }

    val options = listOf("A", "B", "C", "D", "E")
    val bengaliLabels = listOf("ক", "খ", "গ", "ঘ", "ঙ")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.94f)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
                .testTag("answer_key_editor_dialog")
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
                    Text(
                        text = if (initialExam == null) "নতুন পরীক্ষা তৈরি ও উত্তর সেট" else "পরীক্ষার উত্তর ও সেটিংস সম্পাদনা",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_editor_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Exam Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("পরীক্ষার নাম (Exam Title)", color = TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exam_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3 Config Inputs: Total Questions, Pass %, Negative Mark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = totalQuestionsText,
                        onValueChange = { totalQuestionsText = it },
                        label = { Text("মোট প্রশ্ন (Default 100)", color = TextMuted, fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("total_questions_input")
                    )

                    OutlinedTextField(
                        value = passPercentageText,
                        onValueChange = { passPercentageText = it },
                        label = { Text("পাশ মার্ক % (80%)", color = TextMuted, fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pass_percentage_input")
                    )

                    OutlinedTextField(
                        value = negativeMarkText,
                        onValueChange = { negativeMarkText = it },
                        label = { Text("মাইনাস মার্ক (0.25)", color = TextMuted, fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("negative_mark_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Batch Fill Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            for (i in 0 until totalQ) {
                                val opt = when (i % 5) {
                                    0 -> "A"
                                    1 -> "B"
                                    2 -> "C"
                                    3 -> "D"
                                    else -> "E"
                                }
                                if (i < keysList.size) keysList[i] = opt else keysList.add(opt)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("প্যাটার্ন A-B-C-D", fontSize = 10.sp, color = TextWhite)
                    }

                    OutlinedButton(
                        onClick = {
                            for (i in 0 until totalQ) {
                                if (i < keysList.size) keysList[i] = "A" else keysList.add("A")
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("সবগুলো 'ক' (A)", fontSize = 10.sp, color = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "উত্তরমালা নির্বাচন করুন (প্রশ্ন ১ থেকে $totalQ):",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Question Grid
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(totalQ) { idx ->
                        val qNum = idx + 1
                        val selectedOpt = if (idx < keysList.size) keysList[idx] else "A"

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                                .border(0.8.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
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
                                            text = "$qNum",
                                            color = TextWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "প্রশ্ন $qNum:",
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    options.forEachIndexed { optIdx, opt ->
                                        val isSelected = selectedOpt.equals(opt, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    if (isSelected) PrimaryIndigo else Color(0xFF1E293B),
                                                    CircleShape
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) TextWhite else DarkCardBorder,
                                                    CircleShape
                                                )
                                                .clickable {
                                                    while (keysList.size <= idx) keysList.add("A")
                                                    keysList[idx] = opt
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${bengaliLabels[optIdx]}",
                                                color = if (isSelected) TextWhite else TextMuted,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Save Button
                Button(
                    onClick = {
                        val passP = passPercentageText.toDoubleOrNull() ?: 80.0
                        val negM = negativeMarkText.toDoubleOrNull() ?: 0.25
                        val keysString = (0 until totalQ).joinToString(",") { idx ->
                            if (idx < keysList.size) keysList[idx] else "A"
                        }
                        onSave(
                            initialExam?.id ?: 0L,
                            title.ifBlank { "RIP Exam" },
                            totalQ,
                            passP,
                            negM,
                            keysString
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_exam_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("সংরক্ষণ করুন (Save Exam)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
