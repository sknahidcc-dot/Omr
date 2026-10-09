package com.example.omr

import android.graphics.Bitmap
import android.graphics.Color
import com.example.data.model.Exam
import kotlin.math.max
import kotlin.math.min

data class QuestionEvaluation(
    val questionNumber: Int,
    val selectedOption: String, // "A", "B", "C", "D", "E", "BLANK", "MULTIPLE"
    val correctOption: String,
    val status: QuestionStatus,
    val scoreDelta: Double
)

enum class QuestionStatus {
    CORRECT,
    WRONG,
    MULTIPLE_MARKED,
    UNATTEMPTED
}

data class OmrEvaluationResult(
    val studentRoll: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val blankCount: Int,
    val totalScore: Double,
    val negativeMarksDeducted: Double,
    val percentage: Double,
    val isPassed: Boolean,
    val questions: List<QuestionEvaluation>,
    val processedBitmap: Bitmap? = null
)

object OmrProcessor {

    private val OPTIONS = listOf("A", "B", "C", "D", "E")

    fun evaluateOmr(
        bitmap: Bitmap,
        exam: Exam
    ): OmrEvaluationResult {
        val totalQuestions = exam.totalQuestions
        val answerKeys = exam.getAnswerKeyList()

        // Normalize / downscale bitmap for fast, consistent processing
        val targetWidth = 1000
        val targetHeight = (targetWidth * (bitmap.height.toFloat() / bitmap.width)).toInt()
        val scaled = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

        val width = scaled.width
        val height = scaled.height

        // Extract grayscale luminances
        val pixels = IntArray(width * height)
        scaled.getPixels(pixels, 0, width, 0, 0, width, height)

        val gray = FloatArray(width * height)
        for (i in pixels.indices) {
            val p = pixels[i]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            // Luminance: 0.0 (dark) to 1.0 (bright)
            gray[i] = (0.299f * r + 0.587f * g + 0.114f * b) / 255f
        }

        // Measure paper background brightness (sampling upper center region)
        var sampleSum = 0f
        var sampleCount = 0
        val sampleStartY = (height * 0.1f).toInt()
        val sampleEndY = (height * 0.2f).toInt()
        val sampleStartX = (width * 0.2f).toInt()
        val sampleEndX = (width * 0.5f).toInt()
        for (y in sampleStartY until sampleEndY step 4) {
            for (x in sampleStartX until sampleEndX step 4) {
                sampleSum += gray[y * width + x]
                sampleCount++
            }
        }
        val paperBrightness = if (sampleCount > 0) sampleSum / sampleCount else 0.85f

        // 1. Detect 4-digit Roll Number
        val detectedRoll = detectRollNumber(gray, width, height, paperBrightness)

        // 2. Detect 100 MCQs in 3 columns
        val evaluatedQuestions = mutableListOf<QuestionEvaluation>()
        var correctCount = 0
        var wrongCount = 0
        var blankCount = 0

        // Column mapping according to sheet:
        // Col 0: Q1 to 35
        // Col 1: Q36 to 70
        // Col 2: Q71 to 100
        val colWidthRatio = 0.265f
        val colStarts = floatArrayOf(0.088f, 0.380f, 0.672f)
        val questionsStartY = 0.285f
        val questionsEndY = 0.940f
        val rowHeightRatio = (questionsEndY - questionsStartY) / 35f

        for (qIdx in 0 until totalQuestions) {
            val col = qIdx / 35
            val rowInCol = qIdx % 35

            if (col > 2) break

            val colXStart = colStarts[col]
            val rowYCenter = questionsStartY + (rowInCol + 0.5f) * rowHeightRatio

            // Option bubble centers for this question:
            // 5 options evenly spaced in the options area (right 78% of column)
            val optAreaStart = colXStart + (colWidthRatio * 0.22f)
            val optAreaWidth = colWidthRatio * 0.76f
            val optSpacing = optAreaWidth / 5f

            val optionDarkness = FloatArray(5)
            for (opt in 0 until 5) {
                val cx = optAreaStart + (opt + 0.5f) * optSpacing
                val cy = rowYCenter
                optionDarkness[opt] = sampleBubbleDarkness(gray, width, height, cx, cy, paperBrightness)
            }

            // Fill threshold: A filled pencil/pen bubble will have high relative darkness
            val fillThreshold = 0.35f
            val filledIndices = mutableListOf<Int>()
            for (opt in 0 until 5) {
                if (optionDarkness[opt] > fillThreshold) {
                    filledIndices.add(opt)
                }
            }

            val correctKey = if (qIdx < answerKeys.size) answerKeys[qIdx] else "A"

            val qEval: QuestionEvaluation = when {
                filledIndices.isEmpty() -> {
                    blankCount++
                    QuestionEvaluation(
                        questionNumber = qIdx + 1,
                        selectedOption = "BLANK",
                        correctOption = correctKey,
                        status = QuestionStatus.UNATTEMPTED,
                        scoreDelta = 0.0
                    )
                }
                filledIndices.size > 1 -> {
                    // Rule #3: Multiple bubbles filled = WRONG answer with negative mark!
                    wrongCount++
                    QuestionEvaluation(
                        questionNumber = qIdx + 1,
                        selectedOption = "MULTIPLE",
                        correctOption = correctKey,
                        status = QuestionStatus.MULTIPLE_MARKED,
                        scoreDelta = -exam.negativeMark
                    )
                }
                else -> {
                    val selected = OPTIONS[filledIndices[0]]
                    if (selected.equals(correctKey, ignoreCase = true)) {
                        correctCount++
                        QuestionEvaluation(
                            questionNumber = qIdx + 1,
                            selectedOption = selected,
                            correctOption = correctKey,
                            status = QuestionStatus.CORRECT,
                            scoreDelta = 1.0
                        )
                    } else {
                        wrongCount++
                        QuestionEvaluation(
                            questionNumber = qIdx + 1,
                            selectedOption = selected,
                            correctOption = correctKey,
                            status = QuestionStatus.WRONG,
                            scoreDelta = -exam.negativeMark
                        )
                    }
                }
            }

            evaluatedQuestions.add(qEval)
        }

        val negativeMarksDeducted = wrongCount * exam.negativeMark
        val totalScore = max(0.0, (correctCount * 1.0) - negativeMarksDeducted)
        val percentage = if (totalQuestions > 0) (totalScore / totalQuestions.toDouble()) * 100.0 else 0.0
        val isPassed = percentage >= exam.passPercentage

        return OmrEvaluationResult(
            studentRoll = detectedRoll,
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            wrongCount = wrongCount,
            blankCount = blankCount,
            totalScore = (Math.round(totalScore * 100.0) / 100.0),
            negativeMarksDeducted = (Math.round(negativeMarksDeducted * 100.0) / 100.0),
            percentage = (Math.round(percentage * 10.0) / 10.0),
            isPassed = isPassed,
            questions = evaluatedQuestions,
            processedBitmap = scaled
        )
    }

    private fun detectRollNumber(
        gray: FloatArray,
        width: Int,
        height: Int,
        paperBrightness: Float
    ): String {
        // Roll grid is at top right: x from ~0.65 to 0.90, y from ~0.08 to ~0.24
        val rollXStart = 0.655f
        val rollXEnd = 0.895f
        val rollYStart = 0.085f
        val rollYEnd = 0.235f

        val colWidth = (rollXEnd - rollXStart) / 4f
        val rowHeight = (rollYEnd - rollYStart) / 10f

        val rollBuilder = StringBuilder()

        for (col in 0 until 4) {
            val cx = rollXStart + (col + 0.5f) * colWidth
            var maxDarkness = -1f
            var bestDigit = 0

            for (digit in 0..9) {
                val cy = rollYStart + (digit + 0.5f) * rowHeight
                val darkness = sampleBubbleDarkness(gray, width, height, cx, cy, paperBrightness)
                if (darkness > maxDarkness) {
                    maxDarkness = darkness
                    bestDigit = digit
                }
            }

            // If darkness is noticeable, take best digit; else default to 0
            if (maxDarkness > 0.20f) {
                rollBuilder.append(bestDigit)
            } else {
                rollBuilder.append(bestDigit)
            }
        }

        val result = rollBuilder.toString()
        return if (result.isBlank()) "1001" else result
    }

    private fun sampleBubbleDarkness(
        gray: FloatArray,
        width: Int,
        height: Int,
        normX: Float,
        normY: Float,
        paperBrightness: Float
    ): Float {
        val px = (normX * width).toInt()
        val py = (normY * height).toInt()
        val radius = max(3, (width * 0.009f).toInt())

        var darkPixels = 0
        var totalPixels = 0

        for (dy in -radius..radius) {
            val y = py + dy
            if (y !in 0 until height) continue
            for (dx in -radius..radius) {
                if (dx * dx + dy * dy <= radius * radius) {
                    val x = px + dx
                    if (x in 0 until width) {
                        val brightness = gray[y * width + x]
                        // Relative darkness compared to paper
                        val darkness = max(0f, paperBrightness - brightness)
                        if (darkness > 0.22f) {
                            darkPixels++
                        }
                        totalPixels++
                    }
                }
            }
        }

        return if (totalPixels > 0) darkPixels.toFloat() / totalPixels.toFloat() else 0f
    }
}
