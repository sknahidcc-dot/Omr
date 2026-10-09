package com.example.data.repository

import com.example.data.dao.OmrDao
import com.example.data.model.Exam
import com.example.data.model.ExamResult
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

class OmrRepository(private val dao: OmrDao) {

    // Students
    val allStudents: Flow<List<Student>> = dao.getAllStudents()

    suspend fun getStudentByRoll(roll: String): Student? = dao.getStudentByRoll(roll)

    suspend fun insertStudent(student: Student): Long = dao.insertStudent(student)

    suspend fun updateStudent(student: Student) = dao.updateStudent(student)

    suspend fun deleteStudent(student: Student) = dao.deleteStudent(student)

    suspend fun deleteStudentById(id: Long) = dao.deleteStudentById(id)

    // Exams
    val allExams: Flow<List<Exam>> = dao.getAllExams()

    suspend fun getExamById(id: Long): Exam? = dao.getExamById(id)

    suspend fun insertExam(exam: Exam): Long = dao.insertExam(exam)

    suspend fun updateExam(exam: Exam) = dao.updateExam(exam)

    suspend fun deleteExam(exam: Exam) = dao.deleteExam(exam)

    // Results
    val allResults: Flow<List<ExamResult>> = dao.getAllResults()

    fun getResultsForStudent(roll: String): Flow<List<ExamResult>> = dao.getResultsForStudent(roll)

    fun getResultsForExam(examId: Long): Flow<List<ExamResult>> = dao.getResultsForExam(examId)

    suspend fun insertResult(result: ExamResult): Long = dao.insertResult(result)

    suspend fun deleteResult(result: ExamResult) = dao.deleteResult(result)

    suspend fun deleteResultById(id: Long) = dao.deleteResultById(id)
}
