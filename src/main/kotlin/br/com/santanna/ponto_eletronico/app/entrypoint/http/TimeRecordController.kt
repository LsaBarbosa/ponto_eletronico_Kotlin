package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.domain.service.PdfGeneratorService
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/ponto")
class TimeRecordController(
    private val timeRecordService: TimeRecordService, private val employeeService: EmployeeService,
    private val modelMapper: ModelMapper, private val pdfGeneratorService: PdfGeneratorService
) {

    @PostMapping("/entrada")
    fun registerCheckin(@RequestParam("cpf") cpf: String): ResponseEntity<RecordCheckinDto> {

        val checkin = timeRecordService.registerCheckin(cpf)
        return ResponseEntity.ok().body(modelMapper.map(checkin, RecordCheckinDto::class.java))

    }

    @PostMapping("/saida")
    fun registerCheckout(@RequestParam("cpf") cpf: String): ResponseEntity<RecordCheckoutDto> {

        val checkout = timeRecordService.registerCheckout(cpf)
        return ResponseEntity.ok().body(modelMapper.map(checkout, RecordCheckoutDto::class.java))

    }

    @PutMapping("/{id}")
    fun updateTimeRecord(
        @PathVariable id: Long,
        @RequestParam("cpf") cpf: String,
        @RequestBody updateTimeRecordDto: UpdateTimeRecordDto
    ): ResponseEntity<UpdateTimeRecordDto> {

        updateTimeRecordDto.id = id
        val updatedRecord = timeRecordService.updateTimeRecord(cpf, updateTimeRecordDto)
        val recordDto = modelMapper.map(updatedRecord, UpdateTimeRecordDto::class.java)
        return ResponseEntity.ok().body(recordDto)

    }

    @GetMapping("/registros")
    fun getTimeRecordsByEmployeeNameAndDateRange(
        @RequestParam("cpf") cpf: String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String,
        @PageableDefault(size = 5) pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        return timeRecordService.getTimeRecordsByEmployeeCpfAndDateRangePageable(cpf, startDate, endDate, pageable)
    }


    @GetMapping("/hora-extra")
    fun getBalanceHoursByDateByEmployeeNameAndDateRange(
        @RequestParam("cpf") cpf: String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<BalanceHoursDto> {

        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        val timeRecords = timeRecordService.balanceHoursByDate(cpf, startDate, endDate)
        val balanceHoursDto = modelMapper.map(timeRecords, BalanceHoursDto::class.java)
        return ResponseEntity.ok().body(balanceHoursDto)

    }

    @DeleteMapping("/{id}")
    fun deleteTimeRecord(
        @PathVariable("id") id: Long,
        @RequestBody deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto
    ): ResponseEntity<Void> {
        timeRecordService.deleteTimeRecord(deleteTimeRecordRequestDto, id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/export")
    fun exportTimeRecords(
        @RequestParam("cpf") cpf: String,
        @RequestParam("startDate") startDate: String,
        @RequestParam("endDate") endDate: String
    ): ResponseEntity<ByteArray> {
        val employee = employeeService.getEmployeeEntityByCpf(cpf) ?: throw IllegalArgumentException("Employee not found")
        val records = timeRecordService.getTimeRecordsByEmployeeCpfAndDateRangePageable(cpf, LocalDate.parse(startDate), LocalDate.parse(endDate), PageRequest.of(0, Int.MAX_VALUE)).content
        val pdfData = pdfGeneratorService.generateTimeRecordsPdf(records, employee, startDate, endDate)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_PDF
            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Detalhamento_de_horas_${cpf}_${startDate}_to_${endDate}.pdf\"")
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfData)
    }
}