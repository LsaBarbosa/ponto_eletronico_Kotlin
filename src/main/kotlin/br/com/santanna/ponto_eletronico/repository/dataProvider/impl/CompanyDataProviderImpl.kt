package br.com.santanna.ponto_eletronico.repository.dataProvider.impl

import br.com.santanna.ponto_eletronico.model.Company
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.repository.dataProvider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.service.exception.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.service.exception.ObjectNotFoundException
import org.springframework.stereotype.Service

@Service
class CompanyDataProviderImpl(val companyRepository: CompanyRepository): CompanyDataprovider  {

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
        val company = companyRepository.existsByNameCompanyIgnoreCase(nameCompany)
        if (company) throw DataIntegrityViolationException("Empresa com o mesmo nome já está cadastrada.")
        return company
    }

    override fun deleteByNameCompany(nameCompany: String?) {
        val companyToDelete = findByNameCompanyContainsIgnoreCase(nameCompany)
        companyRepository.delete(companyToDelete)
    }

    override fun findAll(): MutableList<Company> {
      val  allCompany = companyRepository.findAll()
         return allCompany
    }

    override fun save(company: Company):Company {
      return companyRepository.save(company)
    }


}