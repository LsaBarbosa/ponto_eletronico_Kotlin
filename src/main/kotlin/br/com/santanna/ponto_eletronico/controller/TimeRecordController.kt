package br.com.santanna.ponto_eletronico.controller

import br.com.santanna.ponto_eletronico.model.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.service.TimeRecordService
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
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
    fun registerCheckin(@RequestParam name: String, surname: String): ResponseEntity<RecordCheckinDto> {

        val checkin = timeRecordService.registerCheckin(name, surname)
        return ResponseEntity.ok().body(modelMapper.map(checkin, RecordCheckinDto::class.java))

    }

    @PostMapping("/saida")
    fun registerCheckout(@RequestParam name: String, surname: String): ResponseEntity<RecordCheckoutDto> {

        val checkout = timeRecordService.registerCheckout(name, surname)
        return ResponseEntity.ok().body(modelMapper.map(checkout, RecordCheckoutDto::class.java))

    }

    @PutMapping("/{id}")
    fun updateTimeRecord(
        @PathVariable id: Long,
        @RequestBody updateTimeRecordDto: UpdateTimeRecordDto
    ): ResponseEntity<UpdateTimeRecordDto> {

        updateTimeRecordDto.id = id
        val updatedRecord = timeRecordService.updateTimeRecord(updateTimeRecordDto)
        val recordDto = modelMapper.map(updatedRecord, UpdateTimeRecordDto::class.java)
        return ResponseEntity.ok().body(recordDto)

    }

    @GetMapping("/registros")
    fun getTimeRecordsByEmployeeNameAndDateRange(
        @RequestParam name: String,
        @RequestParam surname: String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<List<DetailedTimeRecordDto>> {

        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        val timeRecords = timeRecordService.getTimeRecordsByEmployeeNameAndDateRange(name, surname, startDate, endDate)
        val detailedTimeRecordDtos = timeRecords.map {
            modelMapper.map(it, DetailedTimeRecordDto::class.java)
        }
        return ResponseEntity.ok(detailedTimeRecordDtos)

    }

    @GetMapping("/hora-extra")
    fun getOvertimeByEmployeeNameAndDateRange(
        @RequestParam name: String,
        @RequestParam surname: String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<OvertimeDto> {

        val startDate = LocalDate.parse(startDateStr)
        val endDate = LocalDate.parse(endDateStr)
        val timeRecords = timeRecordService.overtimeByDate(name, surname, startDate, endDate)
        val overtimeDto = modelMapper.map(timeRecords, OvertimeDto::class.java)
        return ResponseEntity.ok().body(overtimeDto)

    }
}