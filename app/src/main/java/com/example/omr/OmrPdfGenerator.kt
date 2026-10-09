package com.example.omr

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object OmrPdfGenerator {

    // Standard A4 dimensions in points (72 DPI)
    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842

    fun generateOmrPdf(context: Context): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawOmrSheet(canvas)

        pdfDocument.finishPage(page)

        val outputFile = File(context.cacheDir, "RIP_Exam_OMR_Sheet.pdf")
        val outputStream = FileOutputStream(outputFile)
        pdfDocument.writeTo(outputStream)
        outputStream.flush()
        outputStream.close()
        pdfDocument.close()

        return outputFile
    }

    fun drawOmrSheet(canvas: Canvas) {
        // Background
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // Alignment corner markers (ArUco / Fiducial style high-contrast black squares)
        val markerPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
        val markerSize = 24f
        val margin = 20f

        // 4 Corner Alignment Fiducials
        canvas.drawRect(margin, margin, margin + markerSize, margin + markerSize, markerPaint)
        canvas.drawRect(PAGE_WIDTH - margin - markerSize, margin, PAGE_WIDTH - margin, margin + markerSize, markerPaint)
        canvas.drawRect(margin, PAGE_HEIGHT - margin - markerSize, margin + markerSize, PAGE_HEIGHT - margin, markerPaint)
        canvas.drawRect(PAGE_WIDTH - margin - markerSize, PAGE_HEIGHT - margin - markerSize, PAGE_WIDTH - margin, PAGE_HEIGHT - margin, markerPaint)

        // Top sync blocks (between header and columns)
        val syncY = 198f
        val syncBlockW = 12f
        val syncBlockH = 12f
        val centerX = PAGE_WIDTH / 2f
        val syncXs = floatArrayOf(centerX - 35f, centerX - 12f, centerX + 12f, centerX + 35f)
        for (sx in syncXs) {
            canvas.drawRect(sx - syncBlockW / 2, syncY, sx + syncBlockW / 2, syncY + syncBlockH, markerPaint)
        }

        // Header Title: RIP Exam
        val titlePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 28f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("RIP Exam", PAGE_WIDTH / 2f - 40f, 52f, titlePaint)

        val subTitlePaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 11f
            isFakeBoldText = false
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Developed by Nahid Hasan", PAGE_WIDTH / 2f - 40f, 68f, subTitlePaint)

        // Border paint for boxes (Pinkish/Magenta border matching original PDF)
        val themeBorderPaint = Paint().apply {
            color = Color.parseColor("#BE185D") // Deep Magenta
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        val textPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 10f
            isFakeBoldText = true
        }

        // Left box 1: Name Box (নাম:)
        val nameBoxLeft = 52f
        val nameBoxTop = 86f
        val nameBoxRight = 360f
        val nameBoxBottom = 132f
        canvas.drawRect(nameBoxLeft, nameBoxTop, nameBoxRight, nameBoxBottom, themeBorderPaint)
        canvas.drawText("নাম:", nameBoxLeft + 8f, nameBoxTop + 16f, textPaint)

        // Left box 2: Total Marks (মোট নম্বর:)
        val marksBoxTop = 142f
        val marksBoxBottom = 188f
        canvas.drawRect(nameBoxLeft, marksBoxTop, nameBoxRight, marksBoxBottom, themeBorderPaint)
        canvas.drawText("মোট নম্বর:", nameBoxLeft + 8f, marksBoxTop + 16f, textPaint)

        // Right side: Roll Number Grid (রোল নম্বর)
        val rollLeft = 385f
        val rollRight = 535f
        val rollTop = 35f
        val rollWidth = rollRight - rollLeft
        val rollColWidth = rollWidth / 4f
        val rollHeaderH = 18f
        val digitBoxH = 18f
        val bubbleRowH = 15f
        val rollBottom = rollTop + rollHeaderH + digitBoxH + (10 * bubbleRowH)

        // Roll outer box
        canvas.drawRect(rollLeft, rollTop, rollRight, rollBottom, themeBorderPaint)
        // Roll Header: "রোল নম্বর"
        val rollHeaderPaint = Paint().apply {
            color = Color.parseColor("#BE185D")
            textSize = 10f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("রোল নম্বর", rollLeft + rollWidth / 2f, rollTop + 13f, rollHeaderPaint)
        canvas.drawLine(rollLeft, rollTop + rollHeaderH, rollRight, rollTop + rollHeaderH, themeBorderPaint)

        // Empty digit writing boxes
        val digitBoxesBottom = rollTop + rollHeaderH + digitBoxH
        canvas.drawLine(rollLeft, digitBoxesBottom, rollRight, digitBoxesBottom, themeBorderPaint)
        for (i in 1..3) {
            val cx = rollLeft + i * rollColWidth
            canvas.drawLine(cx, rollTop + rollHeaderH, cx, rollBottom, themeBorderPaint)
        }

        // Draw Roll Bubbles (digits 0 to 9 in Bengali: ০, ১, ২, ৩, ৪, ৫, ৬, ৭, ৮, ৯)
        val bengaliDigits = arrayOf("০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯")
        val rollBubblePaint = Paint().apply {
            color = Color.parseColor("#BE185D")
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        val digitLabelPaint = Paint().apply {
            color = Color.parseColor("#BE185D")
            textSize = 7f
            textAlign = Paint.Align.CENTER
        }

        for (row in 0..9) {
            val cy = digitBoxesBottom + row * bubbleRowH + (bubbleRowH / 2f)
            // Horizontal row line
            if (row > 0) {
                canvas.drawLine(rollLeft, digitBoxesBottom + row * bubbleRowH, rollRight, digitBoxesBottom + row * bubbleRowH, themeBorderPaint)
            }
            for (col in 0..3) {
                val cx = rollLeft + col * rollColWidth + (rollColWidth / 2f)
                canvas.drawCircle(cx, cy, 5.5f, rollBubblePaint)
                canvas.drawText(bengaliDigits[row], cx, cy + 2.5f, digitLabelPaint)
            }
        }

        // 100 Questions in 3 columns:
        // Col 1: Q1 to 35
        // Col 2: Q36 to 70
        // Col 3: Q71 to 100
        val colWidth = 158f
        val colGap = 10f
        val startY = 220f
        val qRowH = 15.5f
        val totalRows = 35
        val tableBottom = startY + (totalRows + 1) * qRowH

        val columnStarts = floatArrayOf(
            margin + 32f,
            margin + 32f + colWidth + colGap,
            margin + 32f + (colWidth + colGap) * 2
        )

        val optionsBengali = arrayOf("ক", "খ", "গ", "ঘ", "ঙ")

        for (c in 0..2) {
            val left = columnStarts[c]
            val right = left + colWidth
            val startQ = c * 35 + 1
            val endQ = minOf((c + 1) * 35, 100)

            // Table Border
            canvas.drawRect(left, startY, right, tableBottom, themeBorderPaint)

            // Header row
            val headerBottom = startY + qRowH
            canvas.drawLine(left, headerBottom, right, headerBottom, themeBorderPaint)

            val qNumColWidth = 32f
            canvas.drawLine(left + qNumColWidth, startY, left + qNumColWidth, tableBottom, themeBorderPaint)

            // Header text
            val thPaint = Paint().apply {
                color = Color.parseColor("#BE185D")
                textSize = 9f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("ক্র.নং", left + qNumColWidth / 2f, startY + 11f, thPaint)
            canvas.drawText("উত্তর", left + qNumColWidth + (colWidth - qNumColWidth) / 2f, startY + 11f, thPaint)

            // Questions in this column
            val optionsWidth = colWidth - qNumColWidth
            val optionSpacing = optionsWidth / 5f

            for (r in 0 until totalRows) {
                val qIndex = startQ + r
                val y1 = headerBottom + r * qRowH
                val y2 = y1 + qRowH
                val cy = y1 + qRowH / 2f

                // Row divider
                canvas.drawLine(left, y2, right, y2, themeBorderPaint)

                if (qIndex <= 100) {
                    // Q Number (e.g. ০১, ০২, ... ১০০)
                    val qNumStr = toBengaliNumber(qIndex)
                    val qNumPaint = Paint().apply {
                        color = Color.parseColor("#0F172A")
                        textSize = 8f
                        isFakeBoldText = true
                        textAlign = Paint.Align.CENTER
                    }
                    canvas.drawText(qNumStr, left + qNumColWidth / 2f, cy + 2.8f, qNumPaint)

                    // 5 Option Bubbles: ক, খ, গ, ঘ, ঙ
                    for (opt in 0..4) {
                        val cx = left + qNumColWidth + opt * optionSpacing + (optionSpacing / 2f)
                        canvas.drawCircle(cx, cy, 5.2f, rollBubblePaint)

                        val optPaint = Paint().apply {
                            color = Color.parseColor("#BE185D")
                            textSize = 6.5f
                            textAlign = Paint.Align.CENTER
                        }
                        canvas.drawText(optionsBengali[opt], cx, cy + 2.2f, optPaint)
                    }
                }
            }
        }

        // Bottom footer note
        val footerPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 8f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("RIP OMR precision sheet • 100 MCQs • Black square markers must be visible when scanning", PAGE_WIDTH / 2f, PAGE_HEIGHT - 12f, footerPaint)
    }

    fun toBengaliNumber(number: Int): String {
        val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val formatted = if (number < 10) "0$number" else number.toString()
        val sb = StringBuilder()
        for (ch in formatted) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
