package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.DetailedTimeRecordDto
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee

interface PdfGeneratorService {
    fun generateTimeRecordsPdf(records: List<DetailedTimeRecordDto>, employee: Employee, startDate: String, endDate: String): ByteArray
}