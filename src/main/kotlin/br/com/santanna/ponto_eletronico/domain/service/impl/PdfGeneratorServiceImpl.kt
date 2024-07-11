package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.DetailedTimeRecordDto
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.service.PdfGeneratorService
import br.com.santanna.ponto_eletronico.infrastructure.util.FontCache
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs


@Service
class PdfGeneratorServiceImpl : PdfGeneratorService {
    override fun generateTimeRecordsPdf(
        records: List<DetailedTimeRecordDto>, employee: Employee, startDate: String, endDate: String
    ): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val pdfWriter = PdfWriter(byteArrayOutputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)


        val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        addEmployeeDetails(document, employee, font, boldFont)
        addTitleAndDateRange(document, startDate, endDate, font, boldFont, dateFormatter)

        val table = createTable(font, boldFont)
        var totalWorkedMinutes = 0L
        var totalOvertimeMinutes = 0L

        records.forEach { record ->
            table.addCell(createCell(record.id.toString(), font))
            table.addCell(createCell(record.startWorkTime ?: "", font))
            table.addCell(createCell(record.endWorkTime ?: "", font))
            table.addCell(createCell(record.startWorkDate?.let { LocalDate.parse(it).format(dateFormatter) } ?: "",
                font))
            table.addCell(createCell(record.endWorkDate?.let { LocalDate.parse(it).format(dateFormatter) } ?: "", font))
            table.addCell(createCell(record.timeWorked ?: "", font))
            val overtime = calculateOvertime(record.timeWorked ?: "00:00")
            table.addCell(createOvertimeCell(overtime, font))

            totalWorkedMinutes += calculateMinutes(record.timeWorked ?: "00:00")
            totalOvertimeMinutes += calculateOvertimeMinutes(record.timeWorked ?: "00:00")
        }

        document.add(table)
        addTotalHoursAndOvertimeBalance(document, totalWorkedMinutes, totalOvertimeMinutes, font, boldFont)
        document.close()

        return byteArrayOutputStream.toByteArray()
    }

    private fun addEmployeeDetails(document: Document, employee: Employee, font: PdfFont, boldFont: PdfFont) {
        val employeeDetails =
            Paragraph("Detalhamento de Horas").setFont(boldFont).setFontSize(22f)
                .setMarginBottom(10f)

        val employeeInfo = Paragraph().setFont(font).setFontSize(12f).setMarginBottom(10f)

        employeeInfo.add("Nome: ${employee.name} ${employee.surname}\n")
        employeeInfo.add("CPF: ${employee.cpf}\n")
        employeeInfo.add("Função: ${employee.position}\n")
        employeeInfo.add("Empresa: ${employee.company?.nameCompany ?: "N/A"}\n")
        employeeInfo.add("CNPJ: ${employee.company?.companyCNPJ ?: "N/A"}\n")

        document.add(employeeDetails)
        document.add(employeeInfo)
    }

    private fun addTitleAndDateRange(
        document: Document,
        startDate: String,
        endDate: String,
        font: PdfFont,
        boldFont: PdfFont,
        dateFormatter: DateTimeFormatter
    ) {
        val title = Paragraph("Registro de Horas").setFont(boldFont).setFontSize(18f).setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(8f)

        val dateRange = Paragraph(
            "Data inicial ${LocalDate.parse(startDate).format(dateFormatter)}\n Data final ${
                LocalDate.parse(endDate).format(dateFormatter)
            }"
        ).setFont(font).setFontSize(12f).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20f)

        document.add(title)

        document.add(dateRange)
    }

    private fun createTable(font: PdfFont, boldFont: PdfFont): Table {
        val table = Table(floatArrayOf(1f, 2f, 2f, 2f, 2f, 2f, 2f)).useAllAvailableWidth()
        table.addHeaderCell(createHeaderCell("ID", boldFont))
        table.addHeaderCell(createHeaderCell("Data de Entrada", boldFont))
        table.addHeaderCell(createHeaderCell("Entrada", boldFont))
        table.addHeaderCell(createHeaderCell("Data de Saída", boldFont))
        table.addHeaderCell(createHeaderCell("Saída", boldFont))
        table.addHeaderCell(createHeaderCell("Horas Trabalhadas", boldFont))
        table.addHeaderCell(createHeaderCell("Saldo de Horas", boldFont))
        return table
    }

    private fun addTotalHoursAndOvertimeBalance(
        document: Document, totalWorkedMinutes: Long, totalOvertimeMinutes: Long, font: PdfFont, boldFont: PdfFont
    ) {
        document.add(Paragraph("\n"))

        val totalTable = Table(floatArrayOf(1f, 1f)).useAllAvailableWidth()
        totalTable.addCell(createSummaryCell("Total de Horas Trabalhadas", boldFont))
        totalTable.addCell(createSummaryCell(formatMinutes(totalWorkedMinutes), font))

        totalTable.addCell(createSummaryCell("Balanço de Horas", boldFont))
        totalTable.addCell(formatOvertimeMinutes(totalOvertimeMinutes, font))

        document.add(totalTable)
    }

    private fun createHeaderCell(text: String, font: PdfFont): Cell {
        return Cell().add(Paragraph(text).setFont(font).setFontSize(10f).setFontColor(ColorConstants.WHITE))
            .setBackgroundColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
    }

    private fun createCell(text: String, font: PdfFont): Cell {
        return Cell().add(Paragraph(text).setFont(font).setFontSize(10f)).setTextAlignment(TextAlignment.CENTER)
    }

    private fun createSummaryCell(text: String, font: PdfFont): Cell {
        return Cell().add(Paragraph(text).setFont(font).setFontSize(12f)).setTextAlignment(TextAlignment.CENTER)
            .setBorderTop(null).setBorderBottom(null).setBorderLeft(null).setBorderRight(null)
    }

    private fun createOvertimeCell(overtime: String, font: PdfFont): Cell {
        val paragraph = Paragraph(overtime).setFont(font).setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
        if (overtime.startsWith("-")) {
            paragraph.setFontColor(ColorConstants.RED)
        } else {
            paragraph.setFontColor(ColorConstants.GREEN)
        }
        return Cell().add(paragraph).setTextAlignment(TextAlignment.CENTER)
    }

    private fun calculateOvertime(timeWorked: String): String {
        val timeWorkedDuration = time(timeWorked)
        val regularHours = LocalTime.of(8, 0)

        val durationWorked = Duration.between(LocalTime.MIN, timeWorkedDuration)
        val durationRegular = Duration.between(LocalTime.MIN, regularHours)

        val difference = durationWorked.minus(durationRegular)

        val hours = abs(difference.toHours())
        val minutes = abs(difference.toMinutesPart())

        val formattedDifference = String.format("%02d:%02d", hours, minutes)

        return if (difference.isNegative) {
            "-$formattedDifference"
        } else {
            formattedDifference
        }
    }

    private fun calculateMinutes(time: String): Long {
        val localTime = time(time)
        return Duration.between(LocalTime.MIN, localTime).toMinutes()
    }

    private fun calculateOvertimeMinutes(timeWorked: String): Long {
        val timeWorkedDuration = time(timeWorked)
        val regularHours = LocalTime.of(8, 0)

        val durationWorked = Duration.between(LocalTime.MIN, timeWorkedDuration)
        val durationRegular = Duration.between(LocalTime.MIN, regularHours)

        return durationWorked.minus(durationRegular).toMinutes()
    }

    private fun time(timeWorked: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(timeWorked, formatter)
    }

    private fun formatMinutes(minutes: Long): String {
        val sign = if (minutes < 0) "-" else ""
        val absMinutes = abs(minutes)
        val hours = absMinutes / 60
        val remainingMinutes = absMinutes % 60
        return String.format("%s%02d:%02d", sign, hours, remainingMinutes)
    }

    private fun formatOvertimeMinutes(minutes: Long, font: PdfFont): Cell {
        val sign = if (minutes < 0) "-" else ""
        val absMinutes = abs(minutes)
        val hours = absMinutes / 60
        val remainingMinutes = absMinutes % 60
        val formattedTime = String.format("%s%02d:%02d", sign, hours, remainingMinutes)
        val paragraph = Paragraph(formattedTime).setFont(font).setFontSize(12f).setTextAlignment(TextAlignment.CENTER)

        if (minutes < 0) {
            paragraph.setFontColor(ColorConstants.RED)
        } else {
            paragraph.setFontColor(ColorConstants.GREEN)
        }

        return Cell().add(paragraph).setTextAlignment(TextAlignment.CENTER).setBorderTop(null).setBorderBottom(null).setBorderLeft(null).setBorderRight(null)
    }
}