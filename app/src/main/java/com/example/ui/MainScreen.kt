package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Exam
import com.example.data.model.Student
import com.example.ui.components.AnswerKeyEditorDialog
import com.example.ui.components.DeveloperDialog
import com.example.ui.components.QrCodeDialog
import com.example.ui.components.ResultDialog
import com.example.ui.components.StudentDetailDialog
import com.example.ui.screens.ExamsScreen
import com.example.ui.screens.PdfSheetScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.OmrViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: OmrViewModel) {
    val context = LocalContext.current

    val students by viewModel.students.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val allResults by viewModel.allResults.collectAsStateWithLifecycle()
    val selectedExam by viewModel.selectedExam.collectAsStateWithLifecycle()
    val currentEvaluation by viewModel.currentEvaluation.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val newRollPrompt by viewModel.newRollPrompt.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) } // 0: Scanner, 1: Exams, 2: Students, 3: PDF Sheet

    // Dialog state variables
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var editingExam: Exam? by remember { mutableStateOf(null) }
    var showAnswerEditor by remember { mutableStateOf(false) }
    var qrExam: Exam? by remember { mutableStateOf(null) }
    var showQrDialog by remember { mutableStateOf(false) }
    var viewingStudent: Student? by remember { mutableStateOf(null) }
    var showAddStudentDialog by remember { mutableStateOf(false) }

    // Message Toast effect
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(PrimaryIndigo, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RIP",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RIP OMR",
                                color = TextWhite,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Barishal University • Nahid Hasan",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Developer Info Action Button (Requirement #10)
                    IconButton(
                        onClick = { showDeveloperDialog = true },
                        modifier = Modifier.testTag("developer_info_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(DarkSurfaceVariant, CircleShape)
                                .border(1.dp, DarkCardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Developer Info",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AmoledBlack,
                    titleContentColor = TextWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier.border(1.dp, DarkCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 0) Icons.Default.DocumentScanner else Icons.Outlined.DocumentScanner,
                            contentDescription = "Scanner"
                        )
                    },
                    label = { Text("স্ক্যান (Scan)", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = CyanAccent,
                        indicatorColor = PrimaryIndigo,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_scanner")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 1) Icons.Default.Assignment else Icons.Outlined.Assignment,
                            contentDescription = "Exams"
                        )
                    },
                    label = { Text("পরীক্ষা (Exams)", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = CyanAccent,
                        indicatorColor = PrimaryIndigo,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_exams")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 2) Icons.Default.People else Icons.Outlined.People,
                            contentDescription = "Students"
                        )
                    },
                    label = { Text("স্টুডেন্ট (Students)", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = CyanAccent,
                        indicatorColor = PrimaryIndigo,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_students")
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 3) Icons.Default.PictureAsPdf else Icons.Outlined.PictureAsPdf,
                            contentDescription = "PDF Sheet"
                        )
                    },
                    label = { Text("OMR শীট (PDF)", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextWhite,
                        selectedTextColor = CyanAccent,
                        indicatorColor = PrimaryIndigo,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_pdf")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> ScannerScreen(
                    activeExam = selectedExam,
                    isProcessing = isProcessing,
                    onEvaluateBitmap = { bitmap ->
                        viewModel.evaluateBitmap(bitmap)
                    },
                    onSwitchExamClicked = { currentTab = 1 }
                )
                1 -> ExamsScreen(
                    exams = exams,
                    selectedExam = selectedExam,
                    onSelectExam = { viewModel.selectExam(it) },
                    onEditExam = {
                        editingExam = it
                        showAnswerEditor = true
                    },
                    onDeleteExam = { viewModel.deleteExam(it) },
                    onShareQr = {
                        qrExam = it
                        showQrDialog = true
                    },
                    onCreateNewExam = {
                        editingExam = null
                        showAnswerEditor = true
                    },
                    onOpenQrImport = {
                        qrExam = selectedExam
                        showQrDialog = true
                    }
                )
                2 -> StudentsScreen(
                    students = students,
                    allResults = allResults,
                    onSelectStudent = { viewingStudent = it },
                    onAddStudentClicked = { showAddStudentDialog = true },
                    onDeleteStudent = { viewModel.deleteStudent(it) }
                )
                3 -> PdfSheetScreen()
            }
        }
    }

    // 1. Developer Dialog (Requirement #10)
    if (showDeveloperDialog) {
        DeveloperDialog(onDismiss = { showDeveloperDialog = false })
    }

    // 2. Result Evaluation Dialog (Requirement #3, #4, #9)
    currentEvaluation?.let { evaluation ->
        val mappedStudent = students.find { it.roll == evaluation.studentRoll }
        ResultDialog(
            evaluation = evaluation,
            examTitle = selectedExam?.title ?: "RIP Exam",
            studentName = mappedStudent?.name,
            onDismiss = { viewModel.clearEvaluation() }
        )
    }

    // 3. Exam & Answer Key Editor Dialog (Requirement #2, #5)
    if (showAnswerEditor) {
        AnswerKeyEditorDialog(
            initialExam = editingExam,
            onSave = { id, title, totalQuestions, passPercentage, negativeMark, answerKeys ->
                viewModel.createOrUpdateExam(
                    id = id,
                    title = title,
                    totalQuestions = totalQuestions,
                    passPercentage = passPercentage,
                    negativeMark = negativeMark,
                    answerKeys = answerKeys
                )
            },
            onDismiss = { showAnswerEditor = false }
        )
    }

    // 4. QR Code Share / Import Dialog (Requirement #5)
    if (showQrDialog) {
        QrCodeDialog(
            exam = qrExam ?: selectedExam,
            onImportQr = { qrPayload ->
                viewModel.importExamFromQr(qrPayload)
            },
            onDismiss = { showQrDialog = false }
        )
    }

    // 5. Student Detail Dialog with Progress Line Graph (Requirement #6, #7)
    viewingStudent?.let { student ->
        val analytics = viewModel.getStudentAnalytics(student, allResults)
        StudentDetailDialog(
            analytics = analytics,
            onDismiss = { viewingStudent = null }
        )
    }

    // 6. Manual Add Student Dialog
    if (showAddStudentDialog) {
        var newStudentName by remember { mutableStateOf("") }
        var newStudentRoll by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("নতুন স্টুডেন্ট যুক্ত করুন", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newStudentName,
                        onValueChange = { newStudentName = it },
                        label = { Text("স্টুডেন্টের নাম (Name)", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = DarkCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_student_name_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newStudentRoll,
                        onValueChange = { newStudentRoll = it },
                        label = { Text("রোল নম্বর (4-digit Roll)", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = DarkCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_student_roll_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newStudentName.isNotBlank() && newStudentRoll.isNotBlank()) {
                            viewModel.addStudent(newStudentName.trim(), newStudentRoll.trim())
                            showAddStudentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    modifier = Modifier.testTag("confirm_add_student_button")
                ) {
                    Text("যুক্ত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) {
                    Text("বাতিল", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }

    // 7. Auto-Prompt for Unregistered Roll (Requirement #6)
    newRollPrompt?.let { unknownRoll ->
        var promptStudentName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.dismissNewRollPrompt() },
            title = {
                Text(
                    "নতুন রোল ($unknownRoll) পাওয়া গেছে!",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "স্ক্যান করা ওএমআর শীটে একটি নতুন রোল নম্বর পাওয়া গেছে। স্টুডেন্টের নাম লিখলে স্বয়ংক্রিয়ভাবে রেজাল্ট যুক্ত হয়ে যাবে:",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = promptStudentName,
                        onValueChange = { promptStudentName = it },
                        label = { Text("স্টুডেন্টের নাম", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = DarkCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_roll_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.registerNewStudentAndSaveResult(unknownRoll, promptStudentName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    modifier = Modifier.testTag("save_new_roll_student_button")
                ) {
                    Text("সেভ ও রেজাল্ট যুক্ত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissNewRollPrompt() }) {
                    Text("পরে দেব", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }
}
