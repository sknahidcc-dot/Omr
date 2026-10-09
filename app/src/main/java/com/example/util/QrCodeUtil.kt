package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import com.example.data.model.Exam
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrCodeUtil {

    private const val PREFIX = "RIP_OMR_EXAM_V1:"

    fun formatExamToQrPayload(exam: Exam): String {
        // Compact format: RIP_OMR_EXAM_V1:TITLE|TOTAL|PASS|NEG|KEYS
        return "$PREFIX${exam.title}|${exam.totalQuestions}|${exam.passPercentage}|${exam.negativeMark}|${exam.answerKeys}"
    }

    fun parseExamFromQr(payload: String): Exam? {
        val trimmed = payload.trim()
        if (!trimmed.startsWith(PREFIX)) return null
        val data = trimmed.removePrefix(PREFIX).split("|")
        if (data.size < 5) return null

        return try {
            val title = data[0].ifBlank { "Scanned Exam" }
            val totalQuestions = data[1].toIntOrNull() ?: 100
            val passPercentage = data[2].toDoubleOrNull() ?: 80.0
            val negativeMark = data[3].toDoubleOrNull() ?: 0.25
            val answerKeys = data[4]
            Exam(
                title = title,
                totalQuestions = totalQuestions,
                passPercentage = passPercentage,
                negativeMark = negativeMark,
                answerKeys = answerKeys
            )
        } catch (e: Exception) {
            null
        }
    }

    fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
        val hints = HashMap<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
        hints[EncodeHintType.MARGIN] = 2

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    fun decodeQrFromBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (e: Exception) {
            null
        }
    }
}
