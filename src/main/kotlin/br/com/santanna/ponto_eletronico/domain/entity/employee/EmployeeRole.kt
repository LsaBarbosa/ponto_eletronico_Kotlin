package br.com.santanna.ponto_eletronico.domain.entity.employee

enum class EmployeeRole(val role: String) {
    CTO("cto"),
    ADMIN("admin"),
    MANAGER("manager"),
    USER("user")
}