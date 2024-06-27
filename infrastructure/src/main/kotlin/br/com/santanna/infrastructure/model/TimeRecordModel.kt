package br.com.santanna.infrastructure.model

import br.com.santanna.domain.entity.TimeRecord
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity

data class TimeRecordModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var startWorkTime: LocalDateTime? = null,
    var endWorkTime: LocalDateTime? = null,
    var timeWorked: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    var employeeModel: EmployeeModel? = null
)
fun TimeRecordModel.toTimeRecord(): TimeRecord {
    return  TimeRecord(
        id = this.id,
        startWorkTime = this.startWorkTime,
        endWorkTime = this.endWorkTime,
        timeWorked = this.timeWorked,
        employee = this.employeeModel?.toEmployee()
    )
}fun  TimeRecord.TimeRecordModel(): TimeRecordModel{
    return TimeRecordModel(
      id = this.id,
        startWorkTime = this.startWorkTime,
        endWorkTime = this.endWorkTime,
        timeWorked = this.timeWorked,
        employeeModel = this.employee?.toEmployeeModel()
    )
}
