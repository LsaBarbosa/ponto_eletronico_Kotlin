package br.com.santanna.ponto_eletronico.controller

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.service.EmployeeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/colaborador")
class EmployeeController(val employeeService: EmployeeService) {

    @GetMapping
    fun allEmployees(): List<Employee> {
        return employeeService.getAllEmployees()
    }

    @GetMapping("/{id}")
    fun getEmployeeById(@PathVariable("id") id: Long): ResponseEntity<Employee?> {
        try {
        val employee =employeeService.getEmployeeById(id)
        return ResponseEntity.ok(employee)
        }catch (ex:Exception){
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun registerNewEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        try {
            val employeeCreated = employeeService.registerEmployee(employeeDto)
            val uri = URI.create("/employees/${employeeCreated.id}")
            return ResponseEntity.created(uri).body(employeeCreated)
        } catch (ex: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @PutMapping
    fun updateEmployee(@RequestParam name: String, @RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
      try {
        employeeService.updateEmployee(employeeDto)
        return ResponseEntity.ok(employeeDto)
      }catch (ex:Exception){
          return ResponseEntity.badRequest().build()
      }
    }

    @DeleteMapping("/{name}")
    fun deleteEmployee(@PathVariable name: String): ResponseEntity<Void> {
        try {
            employeeService.deleteEmployee(name)
            return ResponseEntity.noContent().build()
        } catch (ex: Exception) {
            return ResponseEntity.notFound().build()
        }
    }


}