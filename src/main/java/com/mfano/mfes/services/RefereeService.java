package com.mfano.mfes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Referee;
import com.mfano.mfes.repositories.ClientRepository;
import com.mfano.mfes.repositories.RefereeRepository;

@Service
public class RefereeService {
    private final RefereeRepository refereeRepository;
    private final ClientRepository clientRepository;

    public RefereeService(RefereeRepository refereeRepository, ClientRepository clientRepository) {
        this.refereeRepository = refereeRepository;
        this.clientRepository = clientRepository;
    }

    public List<Referee> findByClient(Long id) {
        return refereeRepository.findByClient(id);
    }

    public void saveReferee(Long id, Referee referee) {
        Client client = clientRepository.findById(id).orElse(null);
        Referee nef = new Referee();
        if (client != null) {
            nef.setClient(id);
            nef.setName(referee.getName());
            nef.setPhone(referee.getPhone());
            nef.setRelationship(referee.getRelationship());
            refereeRepository.save(nef);
        }
    }
}
