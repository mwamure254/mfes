package com.mfano.mfes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mfes.auth.models.User;
import com.mfano.mfes.models.Portfolio;
import com.mfano.mfes.repositories.PortfolioRepository;

@Service 
public class PortfolioService {
      private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public Portfolio getById(Long id) {
        return portfolioRepository.findById(id).orElse(null);
    }

    public List<Portfolio> getByUser(User user) {
        return portfolioRepository.findByUser(user);
    }

    public List<Portfolio> getByCreater(String p){
        return portfolioRepository.findByCreatedBy(p);
    }

    public List<Portfolio> getAll(){
        return portfolioRepository.findAllByOrderByCreatedAtDesc();
    }

    public Portfolio save(Portfolio g){
        return portfolioRepository.save(g);
    }

    public void toggleActive(Portfolio id) {
        id.setActive(!Boolean.TRUE.equals(id.isActive()));
        save(id);
    }

    public void delete(Long g){
        Portfolio existing = getById(g);
        portfolioRepository.delete(existing);
    }

    public Portfolio getByName(String name) {
        return portfolioRepository.findByName(name);
    }
}
