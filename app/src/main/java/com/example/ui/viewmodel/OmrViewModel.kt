package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.Exam
import com.example.data.model.ExamResult
import com.example.data.model.Student
import com.example.data.repository.OmrRepository
import com.example.omr.OmrEvaluationResult
import com.example.omr.OmrPdfGenerator
import com.example.omr.OmrProcessor
import com.example.util.QrCodeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class StudentAnalytics(
    val student: Student,
    val totalExamsTaken: Int,
    val passedExamsCount: Int,
    val averageScore: Double,
    val highestScore: Double,
    val passRatePercentage: Double,
    val resultsHistory: List<ExamResult>
)

class OmrViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OmrRepository

    val students: StateFlow<List<Student>>
    val exams: StateFlow<List<Exam>>
    val allResults: StateFlow<List<ExamResult>>

    private val _selectedExam = MutableStateFlow<Exam?>(null)
    val selectedExam: StateFlow<Exam?> = _selectedExam.asStateFlow()

    private val _currentEvaluation = MutableStateFlow<OmrEvaluationResult?>(null)
    val currentEvaluation: StateFlow<OmrEvaluationResult?> = _currentEvaluation.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _newRollPrompt = MutableStateFlow<String?>(null)
    val newRollPrompt: StateFlow<String?> = _newRollPrompt.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = OmrRepository(database.omrDao())

        students = repository.allStudents.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        exams = repository.allExams.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allResults = repository.allResults.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        seedInitialDataIfNeeded()
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            val existingExams = repository.allExams.first()
            if (existingExams.isEmpty()) {
                // Seed Default 100-Question Exam
                val defaultKeys = (1..100).map { i ->
                    when (i % 5) {
                        1 -> "A"
                        2 -> "B"
                        3 -> "C"
                        4 -> "D"
                        else -> "E"
                    }
                }.joinToString(",")

                val sampleExam = Exam(
                    title = "RIP Model Test 01",
                    totalQuestions = 100,
                    passPercentage = 80.0,
                    negativeMark = 0.25,
                    answerKeys = defaultKeys
                )
                val id = repository.insertExam(sampleExam)
                _selectedExam.value = sampleExam.copy(id = id)
            } else {
                _selectedExam.value = existingExams.first()
            }

            val existingStudents = repository.allStudents.first()
            if (existingStudents.isEmpty()) {
                repository.insertStudent(Student(name = "Nahid Hasan", roll = "1001"))
                repository.insertStudent(Student(name = "Tanvir Ahmed", roll = "1002"))
                repository.insertStudent(Student(name = "Sumon Barman", roll = "1003"))
                repository.insertStudent(Student(name = "Sadia Islam", roll = "1004"))
            }
        }
    }

    fun selectExam(exam: Exam) {
        _selectedExam.value = exam
    }

    fun createOrUpdateExam(
        id: Long = 0,
        title: String,
        totalQuestions: Int,
        passPercentage: Double,
        negativeMark: Double,
        answerKeys: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val exam = Exam(
                id = id,
                title = title,
                totalQuestions = totalQuestions,
                passPercentage = passPercentage,
                negativeMark = negativeMark,
                answerKeys = answerKeys
            )
            if (id == 0L) {
                val newId = repository.insertExam(exam)
                _selectedExam.value = exam.copy(id = newId)
                _message.value = "নতুন পরীক্ষা সফলভাবে তৈরি হয়েছে!"
            } else {
                repository.updateExam(exam)
                _selectedExam.value = exam
                _message.value = "পরীক্ষা আপডেট করা হয়েছে!"
            }
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExam(exam)
            if (_selectedExam.value?.id == exam.id) {
                val remaining = exams.value.filter { it.id != exam.id }
                _selectedExam.value = remaining.firstOrNull()
            }
            _message.value = "পরীক্ষা মুছে ফেলা হয়েছে"
        }
    }

    fun addStudent(name: String, roll: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStudent(Student(name = name, roll = roll))
            _message.value = "স্টুডেন্ট ($name - $roll) যুক্ত হয়েছে"
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
            _message.value = "স্টুডেন্ট মুছে ফেলা হয়েছে"
        }
    }

    fun evaluateBitmap(bitmap: Bitmap) {
        val currentExam = _selectedExam.value ?: exams.value.firstOrNull()
        if (currentExam == null) {
            _message.value = "দয়া করে প্রথমে একটি পরীক্ষা নির্বাচন করুন"
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            val eval = withContext(Dispatchers.Default) {
                OmrProcessor.evaluateOmr(bitmap, currentExam)
            }
            _currentEvaluation.value = eval
            _isProcessing.value = false

            // Check if detected roll exists in students list
            withContext(Dispatchers.IO) {
                val existing = repository.getStudentByRoll(eval.studentRoll)
                if (existing == null) {
                    _newRollPrompt.value = eval.studentRoll
                } else {
                    // Auto-save result for this known student
                    autoSaveResult(eval, existing.name)
                }
            }
        }
    }

    fun registerNewStudentAndSaveResult(roll: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val studentName = name.ifBlank { "Student $roll" }
            repository.insertStudent(Student(name = studentName, roll = roll))
            _newRollPrompt.value = null

            val eval = _currentEvaluation.value
            if (eval != null && eval.studentRoll == roll) {
                autoSaveResult(eval, studentName)
            }
            _message.value = "নতুন স্টুডেন্ট যুক্ত হয়েছে এবং রেজাল্ট সেভ হয়েছে!"
        }
    }

    fun dismissNewRollPrompt() {
        val eval = _currentEvaluation.value
        if (eval != null) {
            viewModelScope.launch(Dispatchers.IO) {
                autoSaveResult(eval, "Student ${eval.studentRoll}")
            }
        }
        _newRollPrompt.value = null
    }

    private suspend fun autoSaveResult(eval: OmrEvaluationResult, studentName: String) {
        val exam = _selectedExam.value ?: return
        val answersGivenStr = eval.questions.joinToString(",") { it.selectedOption }

        val result = ExamResult(
            examId = exam.id,
            examTitle = exam.title,
            studentRoll = eval.studentRoll,
            studentName = studentName,
            totalQuestions = eval.totalQuestions,
            correctCount = eval.correctCount,
            wrongCount = eval.wrongCount,
            blankCount = eval.blankCount,
            score = eval.totalScore,
            percentage = eval.percentage,
            isPassed = eval.isPassed,
            negativeMarkDeducted = eval.negativeMarksDeducted,
            answersGiven = answersGivenStr
        )
        repository.insertResult(result)
    }

    fun clearEvaluation() {
        _currentEvaluation.value = null
    }

    fun getStudentAnalytics(student: Student, results: List<ExamResult>): StudentAnalytics {
        val studentResults = results.filter { it.studentRoll == student.roll }
        val totalTaken = studentResults.size
        val passed = studentResults.count { it.isPassed }
        val avgScore = if (totalTaken > 0) studentResults.sumOf { it.score } / totalTaken else 0.0
        val highest = studentResults.maxOfOrNull { it.score } ?: 0.0
        val passRate = if (totalTaken > 0) (passed.toDouble() / totalTaken) * 100.0 else 0.0

        return StudentAnalytics(
            student = student,
            totalExamsTaken = totalTaken,
            passedExamsCount = passed,
            averageScore = (Math.round(avgScore * 10.0) / 10.0),
            highestScore = highest,
            passRatePercentage = (Math.round(passRate * 10.0) / 10.0),
            resultsHistory = studentResults
        )
    }

    fun importExamFromQr(qrContent: String): Boolean {
        val exam = QrCodeUtil.parseExamFromQr(qrContent) ?: return false
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repository.insertExam(exam)
            _selectedExam.value = exam.copy(id = newId)
            _message.value = "QR কোড থেকে পরীক্ষা '${exam.title}' সফলভাবে সেভ হয়েছে!"
        }
        return true
    }

    fun clearMessage() {
        _message.value = null
    }
}
