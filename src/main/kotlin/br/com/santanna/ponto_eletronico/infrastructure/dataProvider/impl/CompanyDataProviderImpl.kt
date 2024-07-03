package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.infrastructure.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import org.springframework.stereotype.Service

@Service
class CompanyDataProviderImpl(val companyRepository: CompanyRepository): CompanyDataprovider {

    override fun findByCompanyCNPJ(companyCNPJ: String?): Company {
        val company = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw ObjectNotFoundException("Não foi encontrado o CNPJ: $companyCNPJ no sistema")
        return company
    }

    override fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company {
        val company = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
            ?: throw ObjectNotFoundException("Não foi encontrado a empresa de nome $nameCompany no sistema")
        return company
    }

    override fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean {
        return companyRepository.existsByNameCompanyIgnoreCase(nameCompany)
    }

    override fun deleteByCompanyCNPJ(companyCNPJ: String) {
        val companyToDelete = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw ObjectNotFoundException("Não foi encontrado o CNPJ: $companyCNPJ no sistema")
        companyRepository.delete(companyToDelete)
    }

    override fun findAll(): MutableList<Company> {
      val  allCompany = companyRepository.findAll()
         return allCompany
    }

    override fun save(company: Company): Company {
      return companyRepository.save(company)
    }


}