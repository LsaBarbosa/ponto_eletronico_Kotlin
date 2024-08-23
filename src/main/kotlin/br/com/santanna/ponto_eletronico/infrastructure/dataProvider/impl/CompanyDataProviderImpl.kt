package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import br.com.santanna.ponto_eletronico.infrastructure.repository.CompanyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

private const val CNPJ_NOT_FOUND= "Não foi encontrado o CNPJ:"

@Service
class CompanyDataProviderImpl(val companyRepository: CompanyRepository): CompanyDataprovider {

    override fun findByCompanyCNPJ(companyCNPJ: String?): Company? {
        return companyRepository.findByCompanyCNPJ(companyCNPJ)

    }

    override fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company {
     return companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
    }

    override fun existsByNameCompanyIgnoreCase(nameCompany: String): Boolean {
        return companyRepository.existsByNameCompanyIgnoreCase(nameCompany)
    }

    override fun deleteByCompanyCNPJ(companyCNPJ: String) {
        val companyToDelete = companyRepository.findByCompanyCNPJ(companyCNPJ)
        companyRepository.delete(companyToDelete)
    }

    override fun findAll(pageable: Pageable): Page<Company> {
        return companyRepository.findAll(pageable)
    }

    override fun save(company: Company): Company {
      return companyRepository.save(company)
    }


}