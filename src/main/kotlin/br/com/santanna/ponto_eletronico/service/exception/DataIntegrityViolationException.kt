package br.com.santanna.ponto_eletronico.service.exception

class DataIntegrityViolationException:RuntimeException {
    constructor(message: String?): super(message)
}