package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.domain.service.PdfGeneratorService
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
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
@RequestMapping("/api/ponto")
@Tag(name = "Controle de Horas", description = "End-point para gestão da empresa")
@SecurityRequirement(name = "Bearer Authentication")
class TimeRecordController(
    private val timeRecordService: TimeRecordService, private val employeeService: EmployeeService,
    private val modelMapper: ModelMapper, private val pdfGeneratorService: PdfGeneratorService
) {

    @PostMapping("/entrada")
    @Operation(summary = "Registra a entrada do funcionário")
    fun checkin(): ResponseEntity<RecordCheckinDto> {
        return ResponseEntity.ok(timeRecordService.registerCheckin())
    }

    @PostMapping("/saida")
    @Operation(summary = "Registra a saída do funcionário")
    fun checkout(): ResponseEntity<RecordCheckoutDto> {
        return ResponseEntity.ok(timeRecordService.registerCheckout())
    }
    @PutMapping
    @Operation(summary = "Altera o registro do funcionário")
    fun updateTimeRecord(@Valid @RequestBody updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): ResponseEntity<UpdateTimeRecordDto> {
        val updatedTimeRecordDto = timeRecordService.updateTimeRecord(updateTimeRecordRequestDto)
        return ResponseEntity.ok(updatedTimeRecordDto)
    }

    @GetMapping("/registros")
    @Operation(summary = "Busca o registro de horas do funcionário")
    fun getTimeRecordsByEmployeeNameAndDateRange(
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String,
        @PageableDefault(size = 5) pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        return timeRecordService.getTimeRecordsByEmployeeCpfAndDateRangePageable( startDate, endDate, pageable)
    }


    @GetMapping("/hora-extra")
    @Operation(summary = "Busca as horas extras do funcionário")
    fun getBalanceHoursByDateByEmployeeNameAndDateRange(
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<BalanceHoursDto> {

        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        val timeRecords = timeRecordService.balanceHoursByDate( startDate, endDate)
        val balanceHoursDto = modelMapper.map(timeRecords, BalanceHoursDto::class.java)
        return ResponseEntity.ok().body(balanceHoursDto)

    }


    @GetMapping("/registros/gestao")
    @Operation(summary = "Administrador busca o regostro de horas do funcionário")
    fun getTimeRecordsByEmployeeNameAndDateRangeForManager(
        @RequestBody searchByDateTimeRecordRequestDto: SearchByDateTimeRecordRequestDto,
        @PageableDefault(size = 5) pageable: Pageable
    ): Page<DetailedTimeRecordDto> {

        return timeRecordService.getTimeRecordsByEmployeeCpfAndDateRangePageableForManager(searchByDateTimeRecordRequestDto, pageable)
    }


    @GetMapping("/hora-extra/gestao")
    @Operation(summary = "Administrador busca as horas extras do funcionário")
    fun getBalanceHoursByDateByEmployeeNameAndDateRangeForManager(
        @RequestBody searchByDateTimeRecordRequestDto: SearchByDateTimeRecordRequestDto
    ): ResponseEntity<BalanceHoursDto> {
        val timeRecords = timeRecordService.balanceHoursByDateForManager(searchByDateTimeRecordRequestDto)
        val balanceHoursDto = modelMapper.map(timeRecords, BalanceHoursDto::class.java)
        return ResponseEntity.ok().body(balanceHoursDto)

    }

    @DeleteMapping
    @Operation(summary = "Administrador deleta o registro de horas do funcionário")
    fun deleteTimeRecord(@RequestBody deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto): ResponseEntity<Void> {
        timeRecordService.deleteTimeRecord(deleteTimeRecordRequestDto)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/export")
    @Operation(summary = "Relatório das horas do funcionário")
    fun exportTimeRecords(
        @RequestParam("cpf") cpf: String,
        @RequestParam("startDate") startDate: String,
        @RequestParam("endDate") endDate: String
    ): ResponseEntity<ByteArray> {
        val employee = employeeService.getEmployeeEntityByCpf(cpf) ?: throw IllegalArgumentException("Employee not found")
        val records = timeRecordService.getTimeRecordsByEmployeeCpfAndDateRangePageableForPDF(cpf, LocalDate.parse(startDate), LocalDate.parse(endDate), PageRequest.of(0, Int.MAX_VALUE)).content
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