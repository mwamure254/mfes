package com.mfano.mfes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mfes.auth.models.Branch;
import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Group;
import com.mfano.mfes.repositories.ClientRepository;

@Service 
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public List<Client> getAll(){
        return clientRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Client> getByGroup(Group g){
        return clientRepository.findByGroup(g);
    }

    public List<Client> getByBranch(Branch b){
        return clientRepository.findByBranch(b);
    }

    public Client getById(Long id){
        return clientRepository.findById(id).orElse(null);
    }

    public Client getByNumber(String n){
        return clientRepository.findByIdNumber(n);
    }
    
    public void delete(Long id){
        Client cl = getById(id);
        clientRepository.delete(cl);
    }

    public void save(Client cs){
        clientRepository.save(cs);
    }

    public Client getByPhone(String phone){
        return clientRepository.findByPhone(phone);
    }

     public void toggleActive(Client id) {
        id.setActive(!Boolean.TRUE.equals(id.isActive()));
        save(id);
    }
}
