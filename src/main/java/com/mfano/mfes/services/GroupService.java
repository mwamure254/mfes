package com.mfano.mfes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mfes.models.Group;
import com.mfano.mfes.models.Portfolio;
import com.mfano.mfes.repositories.GroupRepository;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group getById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public List<Group> getByUser(String user) {
        return groupRepository.findByCreatedBy(user);
    }

    public Group getByName(String name) {
        return groupRepository.findByName(name);
    }

    public List<Group> getByFolio(Portfolio p){
        return groupRepository.findByFolio(p);
    }

    public Group save(Group g){
        return groupRepository.save(g);
    }

    public List<Group> getAll(){
        return groupRepository.findAllByOrderByCreatedAtDesc();
    }

     public void toggleActive(Group id) {
        id.setActive(!Boolean.TRUE.equals(id.isActive()));
        save(id);
    }

    public void delete(Long g){
        Group existing = getById(g);
        groupRepository.delete(existing);
    }
}
