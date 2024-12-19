package ch.zhaw.freelancer4u.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.freelancer4u.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    public boolean companyExists(String companyId) {
        return companyRepository.existsById(companyId);
    }
}
