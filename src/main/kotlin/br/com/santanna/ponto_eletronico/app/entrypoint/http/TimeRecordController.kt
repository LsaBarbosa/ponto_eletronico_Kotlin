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
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/time")
@Tag(name = "Controle de Horas", description = "End-point para gestão do banco de horas")
@SecurityRequirement(name = "Bearer Authentication")
class TimeRecordController(
    private val timeRecordService: TimeRecordService, private val employeeService: EmployeeService,
    private val modelMapper: ModelMapper, private val pdfGeneratorService: PdfGeneratorService
) {

    @PostMapping("/checkin")
    @Operation(summary = "Registra a entrada do funcionário")
    fun checkin(): ResponseEntity<RecordCheckinDto> {
        return ResponseEntity.ok(timeRecordService.registerCheckin())
    }

    @PostMapping("/checkout")
    @Operation(summary = "Registra a saída do funcionário")
    fun checkout(): ResponseEntity<RecordCheckoutDto> {
        return ResponseEntity.ok(timeRecordService.registerCheckout())
    }


    @GetMapping("/search/report")
    @Operation(summary = "Busca o registro de horas do funcionário")
    fun getTimeRecordsByEmployeeIdAndDateRange(
        @RequestParam startDate: String?,
        @RequestParam endDate: String?,
        pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val searchByDateTimeRecordDto = SearchByDateTimeRecordDto(
            startDate = startDate,
            endDate = endDate
        )
        return timeRecordService.getTimeRecordsByEmployeeIdAndDateRangePageable(searchByDateTimeRecordDto, pageable)
    }



    @GetMapping("/search/balance")
    @Operation(summary = "Busca as horas extras do funcionário")
    fun getBalanceHoursByDateByEmployeeIdAndDateRange(
        @RequestParam startDate: String?,
        @RequestParam endDate: String?
    ): ResponseEntity<BalanceHoursDto> {
        val searchByDateTimeRecordDto = SearchByDateTimeRecordDto(
            startDate = startDate,
            endDate = endDate
        )

        val timeRecords = timeRecordService.balanceHoursByDate(searchByDateTimeRecordDto)
        val balanceHoursDto = modelMapper.map(timeRecords, BalanceHoursDto::class.java)
        return ResponseEntity.ok().body(balanceHoursDto)
    }




    @GetMapping("/search/adm/report")
    @Operation(summary = "Administrador busca o registro de horas do funcionário")
    fun getTimeRecordsByEmployeeIdAndDateRangeForManager(@RequestParam employeeIdTarget: UUID, @RequestParam passwords: String, @RequestParam startDate: String?, @RequestParam endDate: String?, pageable: Pageable): Page<DetailedTimeRecordDto> {
        val searchByDateTimeRecordRequestDto = SearchByDateTimeRecordRequestDto(
            employeeIdTarget = employeeIdTarget,
            passwords = passwords,
            searchByDateTimeRecordDto = SearchByDateTimeRecordDto(startDate, endDate)
        )
        return timeRecordService.getTimeRecordsByEmployeeIdAndDateRangePageableAsManager(searchByDateTimeRecordRequestDto, pageable)
    }


    @GetMapping("/search/adm/balance")
    @Operation(summary = "Administrador busca as horas extras do funcionário")
    fun getBalanceHoursByDateByEmployeeIdAndDateRangeForManager(
        @RequestParam employeeIdTarget: UUID,
        @RequestParam passwords: String,
        @RequestParam startDate: String?,
        @RequestParam endDate: String?
    ): ResponseEntity<BalanceHoursDto> {
        val searchByDateTimeRecordRequestDto = SearchByDateTimeRecordRequestDto(
            employeeIdTarget = employeeIdTarget,
            passwords = passwords,
            searchByDateTimeRecordDto = SearchByDateTimeRecordDto(startDate, endDate)
        )
        val timeRecords = timeRecordService.balanceHoursByDateAsManager(searchByDateTimeRecordRequestDto)
        val balanceHoursDto = modelMapper.map(timeRecords, BalanceHoursDto::class.java)
        return ResponseEntity.ok().body(balanceHoursDto)
    }

    @GetMapping("/search/adm/report/export")
    @Operation(summary = "Relatório das horas do funcionário")
    fun exportTimeRecords(
        @RequestParam("employeeId") employeeId: UUID,
        @RequestParam("startDate") startDate: String,
        @RequestParam("endDate") endDate: String
    ): ResponseEntity<ByteArray> {
        val employee = employeeService.getEmployeeEntityById(employeeId) ?: throw IllegalArgumentException("Employee not found")
        val records = timeRecordService.getTimeRecordsByEmployeeIdAndDateRangePageableToPDFAsManager(employeeId, LocalDate.parse(startDate), LocalDate.parse(endDate), PageRequest.of(0, Int.MAX_VALUE)).content
        val pdfData = pdfGeneratorService.generateTimeRecordsPdf(records, employee, startDate, endDate)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_PDF
            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Detalhamento_de_horas_${employeeId}_${startDate}_to_${endDate}.pdf\"")
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfData)
    }

    @PutMapping("/adm/update")
    @Operation(summary = "Altera o registro do funcionário")
    fun updateTimeRecord(@Valid @RequestBody updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): ResponseEntity<UpdateTimeRecordDto> {
        val updatedTimeRecordDto = timeRecordService.updateTimeRecordAsManager(updateTimeRecordRequestDto)
        return ResponseEntity.ok(updatedTimeRecordDto)
    }

    @DeleteMapping("/adm/delete")
    @Operation(summary = "Administrador deleta o registro de horas do funcionário")
    fun deleteTimeRecord(@RequestBody deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto): ResponseEntity<Void> {
        timeRecordService.deleteTimeRecordAsManager(deleteTimeRecordRequestDto)
        return ResponseEntity.noContent().build()
    }

}
