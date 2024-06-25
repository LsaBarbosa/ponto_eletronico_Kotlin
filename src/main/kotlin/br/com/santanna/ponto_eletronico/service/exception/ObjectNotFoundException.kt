package br.com.santanna.ponto_eletronico.service.exception

class ObjectNotFoundException:RuntimeException {
    constructor(message: String?): super(message)
}