package br.com.santanna.ponto_eletronico.controller

import br.com.santanna.ponto_eletronico.model.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.service.EmployeeService
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
    fun registerCheckin(@RequestParam name: String): ResponseEntity<RecordCheckinDto> {
        return try {
            val checkin = timeRecordService.registerCheckin(name)
            ResponseEntity.ok().body(modelMapper.map(checkin, RecordCheckinDto::class.java))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @PostMapping("/saida")
    fun registerCheckout(@RequestParam name: String): ResponseEntity<RecordCheckoutDto> {
        return try {
            val checkout = timeRecordService.registerCheckout(name)
            ResponseEntity.ok().body(modelMapper.map(checkout, RecordCheckoutDto::class.java))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @PutMapping("/{id}")
    fun updateTimeRecord(
        @PathVariable id: Long,
        @RequestBody updateTimeRecordDto: UpdateTimeRecordDto
    ): ResponseEntity<UpdateTimeRecordDto> {
        return try {
            updateTimeRecordDto.id = id
            val updatedRecord = timeRecordService.updateTimeRecord(updateTimeRecordDto)
            val recordDto = modelMapper.map(updatedRecord, UpdateTimeRecordDto::class.java)
            ResponseEntity.ok().body(recordDto)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @GetMapping("/registros")
    fun getTimeRecordsByEmployeeNameAndDateRange(
        @RequestParam name: String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<List<DetailedTimeRecordDto>> {
        return try {
            val startDate = LocalDate.parse(startDateStr)
            val endDate = LocalDate.parse(endDateStr)
            val timeRecords = timeRecordService.getTimeRecordsByEmployeeNameAndDateRange(name, startDate, endDate)
            val detailedTimeRecordDtos = timeRecords.map {
                modelMapper.map(it, DetailedTimeRecordDto::class.java)
            }
            return ResponseEntity.ok(detailedTimeRecordDtos)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @GetMapping("/hora-extra")
    fun getOvertimeByEmployeeNameAndDateRange(
        @RequestParam name: String,
        @RequestParam("startDate") startDateStr: String,
        @RequestParam("endDate") endDateStr: String
    ): ResponseEntity<OvertimeDto> {
        return try {
            val startDate = LocalDate.parse(startDateStr)
            val endDate = LocalDate.parse(endDateStr)
            val timeRecords = timeRecordService.overtimeByDate(name, startDate, endDate)
            val overtimeDto = modelMapper.map(timeRecords, OvertimeDto::class.java)
            return ResponseEntity.ok().body(overtimeDto)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }
}