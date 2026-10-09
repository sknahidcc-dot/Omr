package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Exam
import com.example.data.model.ExamResult
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface OmrDao {
    // Students
    @Query("SELECT * FROM students ORDER BY roll ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE roll = :roll LIMIT 1")
    suspend fun getStudentByRoll(roll: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    // Exams
    @Query("SELECT * FROM exams ORDER BY createdAt DESC")
    fun getAllExams(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE id = :id LIMIT 1")
    suspend fun getExamById(id: Long): Exam?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    // Results
    @Query("SELECT * FROM exam_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<ExamResult>>

    @Query("SELECT * FROM exam_results WHERE studentRoll = :roll ORDER BY timestamp DESC")
    fun getResultsForStudent(roll: String): Flow<List<ExamResult>>

    @Query("SELECT * FROM exam_results WHERE examId = :examId ORDER BY score DESC")
    fun getResultsForExam(examId: Long): Flow<List<ExamResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ExamResult): Long

    @Delete
    suspend fun deleteResult(result: ExamResult)

    @Query("DELETE FROM exam_results WHERE id = :id")
    suspend fun deleteResultById(id: Long)
}
