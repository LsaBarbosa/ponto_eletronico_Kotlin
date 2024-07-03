package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/ponto")
class TimeRecordController(
    private val timeRecordService: TimeRecordService,
    private val modelMapper: ModelMapper
) {

    @PostMapping("/entrada")
    fun registerCheckin(@RequestParam ("cpf") cpf:String): ResponseEntity<RecordCheckinDto> {

        val checkin = timeRecordService.registerCheckin(cpf)
        return ResponseEntity.ok().body(modelMapper.map(checkin, RecordCheckinDto::class.java))

    }

    @PostMapping("/saida")
    fun registerCheckout(@RequestParam ("cpf") cpf:String): ResponseEntity<RecordCheckoutDto> {

        val checkout = timeRecordService.registerCheckout(cpf)
        return ResponseEntity.ok().body(modelMapper.map(checkout, RecordCheckoutDto::class.java))

    }

    @PutMapping("/{id}")
    fun updateTimeRecord(
        @PathVariable id: Long,
        @RequestParam ("cpf") cpf: String,
        @RequestBody updateTimeRecordDto: UpdateTimeRecordDto
    ): ResponseEntity<UpdateTimeRecordDto> {

        updateTimeRecordDto.id = id
        val updatedRecord = timeRecordService.updateTimeRecord(cpf,updateTimeRecordDto)
        val recordDto = modelMapper.map(updatedRecord, UpdateTimeRecordDto::class.java)
        return ResponseEntity.ok().body(recordDto)

    }

    @GetMapping("/registros")
    fun getTimeRecordsByEmployeeNameAndDateRange(
        @RequestParam ("cpf") cpf:String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<List<DetailedTimeRecordDto>> {

        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        val timeRecords = timeRecordService.getTimeRecordsByEmployeeCpfAndDateRange(cpf, startDate, endDate)
        val detailedTimeRecordDtos = timeRecords.map {
            modelMapper.map(it, DetailedTimeRecordDto::class.java)
        }
        return ResponseEntity.ok(detailedTimeRecordDtos)

    }

    @GetMapping("/hora-extra")
    fun getOvertimeByEmployeeNameAndDateRange(
        @RequestParam ("cpf") cpf:String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<OvertimeDto> {

        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        val timeRecords = timeRecordService.overtimeByDate(cpf, startDate, endDate)
        val overtimeDto = modelMapper.map(timeRecords, OvertimeDto::class.java)
        return ResponseEntity.ok().body(overtimeDto)

    }
}