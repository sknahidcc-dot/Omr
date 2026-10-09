package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_results")
data class ExamResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val examId: Long,
    val examTitle: String,
    val studentRoll: String,
    val studentName: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val blankCount: Int,
    val score: Double,
    val percentage: Double,
    val isPassed: Boolean,
    val negativeMarkDeducted: Double,
    // Comma-separated student answers e.g. "A,B,MULTIPLE,BLANK,C"
    val answersGiven: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
