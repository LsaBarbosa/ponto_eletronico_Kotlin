package br.com.santanna.infrastructure.impl

import br.com.santanna.domain.dataprovider.CompanyDataprovider
import br.com.santanna.domain.entity.Company
import br.com.santanna.infrastructure.exception.model.DataIntegrityViolationException
import br.com.santanna.infrastructure.exception.model.ObjectNotFoundException
import br.com.santanna.infrastructure.model.toCompany
import br.com.santanna.infrastructure.model.toCompanyModel
import br.com.santanna.infrastructure.repository.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyDataProviderImpl(val companyRepository: CompanyRepository) : CompanyDataprovider {

    override fun findByCompanyCNPJ(companyCNPJ: String?): Company {
        val company = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw ObjectNotFoundException("Não foi encontrado o CNPJ: $companyCNPJ no sistema")
        return company.toCompany()
    }

    override fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company {
        val company = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
            ?: throw ObjectNotFoundException("Não foi encontrado a empresa de nome $nameCompany no sistema")
        return company.toCompany()
    }

    override fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean {
        val company = companyRepository.existsByNameCompanyIgnoreCase(nameCompany)
        if (company) throw DataIntegrityViolationException("Empresa com o mesmo nome já está cadastrada.")
        return company
    }

    override fun deleteByNameCompany(nameCompany: String?) {
        val companyToDelete = findByNameCompanyContainsIgnoreCase(nameCompany)
        companyRepository.delete(companyToDelete.toCompanyModel())
    }

    override fun findAll(): List<Company> {
        val allCompany = companyRepository.findAll()
        return allCompany.map { it.toCompany() }.toList()
    }

    override fun save(company: Company): Company {
        val saveCompany = companyRepository.save(company.toCompanyModel())
        return saveCompany.toCompany()
    }


}