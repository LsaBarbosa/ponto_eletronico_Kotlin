package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/time")
@Tag(name = "Controle de Horas", description = "End-point para gestão do banco de horas")
@SecurityRequirement(name = "Bearer Authentication")
class TimeRecordController(
    private val timeRecordService: TimeRecordService,
    private val modelMapper: ModelMapper
) {

    @PostMapping("/checkin")
    @Operation(summary = "Registra a entrada do funcionário")
    @PreAuthorize("hasRole('USER')")
    fun checkin(): ResponseEntity<RecordCheckinDto> {
        return ResponseEntity.ok(timeRecordService.registerCheckin())
    }

    @PostMapping("/checkout")
    @Operation(summary = "Registra a saída do funcionário")
    @PreAuthorize("hasRole('USER')")
    fun checkout(): ResponseEntity<RecordCheckoutDto> {
        return ResponseEntity.ok(timeRecordService.registerCheckout())
    }


    @GetMapping("/search/report")
    @Operation(summary = "Busca o registro de horas do funcionário")
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Busca as horas extras do funcionário")
    fun getBalanceHoursByDateByEmployeeIdAndDateRange(
        @RequestParam startDate: String?,
        @RequestParam endDate: String?,
        @RequestParam referenceMinutes: Long?,
    ): ResponseEntity<BalanceHoursDto> {
        val searchByDateTimeRecordDto = SearchByDateTimeRecordDto(
            startDate = startDate,
            endDate = endDate,
            referenceMinutes = referenceMinutes
        )
        val timeRecords = timeRecordService.balanceHoursByDate(searchByDateTimeRecordDto)
        val balanceHoursDto = modelMapper.map(timeRecords, BalanceHoursDto::class.java)
        return ResponseEntity.ok().body(balanceHoursDto)
    }

    @GetMapping("/search/adm/report")
    @Operation(summary = "Administrador busca o registro de horas do funcionário")
    @PreAuthorize("hasRole('MANAGER')")
    fun getTimeRecordsByEmployeeIdAndDateRangeForManager(@RequestParam employeeIdTarget: UUID,
                                                         @RequestParam passwords: String,
                                                         @RequestParam startDate: String?,
                                                         @RequestParam endDate: String?,
                                                         pageable: Pageable): Page<DetailedTimeRecordDto> {
        val fixedPageable = PageRequest.of(pageable.pageNumber, 365)
        val searchByDateTimeRecordRequestDto = SearchByDateTimeRecordRequestDto(
            employeeIdTarget = employeeIdTarget,
            passwords = passwords,
            searchByDateTimeRecordDto = SearchByDateTimeRecordDto(startDate, endDate)
        )
        return timeRecordService.getTimeRecordsByEmployeeIdAndDateRangePageableAsManager(searchByDateTimeRecordRequestDto, fixedPageable)
    }


    @GetMapping("/search/adm/balance")
    @Operation(summary = "Administrador busca as horas extras do funcionário")
    @PreAuthorize("hasRole('MANAGER')")
    fun getBalanceHoursByDateByEmployeeIdAndDateRangeForManager(
        @RequestParam employeeIdTarget: UUID,
        @RequestParam passwords: String,
        @RequestParam startDate: String?,
        @RequestParam endDate: String?,
        @RequestParam referenceMinutes: Long
    ): ResponseEntity<BalanceHoursDto> {
        val searchByDateTimeRecordRequestDto = SearchByDateTimeRecordRequestDto(
            employeeIdTarget = employeeIdTarget,
            passwords = passwords,
            searchByDateTimeRecordDto = SearchByDateTimeRecordDto(startDate, endDate),
            referenceMinutes = referenceMinutes
        )
        val timeRecords = timeRecordService.balanceHoursByDateAsManager(searchByDateTimeRecordRequestDto)
        val balanceHoursDto = modelMapper.map(timeRecords, BalanceHoursDto::class.java)
        return ResponseEntity.ok().body(balanceHoursDto)
    }

    @PutMapping("/adm/update")
    @Operation(summary = "Altera o registro do funcionário")
    @PreAuthorize("hasRole('MANAGER')")
    fun updateTimeRecord(@Valid @RequestBody updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): ResponseEntity<UpdateTimeRecordDto> {
        val updatedTimeRecordDto = timeRecordService.updateTimeRecordAsManager(updateTimeRecordRequestDto)
        return ResponseEntity.ok(updatedTimeRecordDto)
    }

    @DeleteMapping("/adm/delete")
    @Operation(summary = "Administrador deleta o registro de horas do funcionário")
    @PreAuthorize("hasRole('MANAGER')")
    fun deleteTimeRecord(@RequestBody deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto): ResponseEntity<Void> {
        timeRecordService.deleteTimeRecordAsManager(deleteTimeRecordRequestDto)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/adm/doctor-apointment")
    fun createByRange(@RequestBody request: CreateTimeRecordByRangeRequest): ResponseEntity<Void> {
        timeRecordService.createTimeRecordsByRange(request)
        return ResponseEntity.ok().build()
    }

}
