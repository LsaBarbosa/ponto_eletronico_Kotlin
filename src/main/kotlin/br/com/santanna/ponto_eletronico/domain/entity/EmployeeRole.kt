package br.com.santanna.ponto_eletronico.domain.entity

enum class EmployeeRole(val role: String) {
    ADMIN("admin"),
    MANAGER("manager"),
    USER("user")
}