package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.app.handler.model.NotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import br.com.santanna.ponto_eletronico.infrastructure.repository.CompanyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

private const val CNPJ_NOT_FOUND= "Não foi encontrado o CNPJ:"

@Service
class CompanyDataProviderImpl(val companyRepository: CompanyRepository): CompanyDataprovider {

    override fun findByCompanyCNPJ(companyCNPJ: String?): Company {
        val company = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw NotFoundException("$CNPJ_NOT_FOUND: $companyCNPJ")
        return company
    }

    override fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company {
        val company = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
            ?: throw NotFoundException("Não foi encontrado a empresa de nome $nameCompany no sistema")
        return company
    }

    override fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean {
        return companyRepository.existsByNameCompanyIgnoreCase(nameCompany)
    }

    override fun deleteByCompanyCNPJ(companyCNPJ: String) {
        val companyToDelete = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw NotFoundException("$CNPJ_NOT_FOUND $companyCNPJ")
        companyRepository.delete(companyToDelete)
    }

    override fun findAll(pageable: Pageable): Page<Company> {
        return companyRepository.findAll(pageable)
    }

    override fun save(company: Company): Company {
      return companyRepository.save(company)
    }


}