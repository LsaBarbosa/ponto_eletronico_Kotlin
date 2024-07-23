package br.com.santanna.ponto_eletronico.domain.entity.employee

enum class EmployeeRole(val role: String) {
    ADMIN("admin"),
    MANAGER("manager"),
    USER("user")
}