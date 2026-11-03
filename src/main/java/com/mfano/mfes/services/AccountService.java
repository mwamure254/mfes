package com.mfano.mfes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mfes.models.Accounts;
import com.mfano.mfes.repositories.AccountRepository;
import com.mfano.mfes.repositories.ClientRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    public List<Accounts> findByClient(Long id) {
        return accountRepository.findByClient(id);
    }

}
