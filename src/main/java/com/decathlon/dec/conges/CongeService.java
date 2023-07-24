package com.decathlon.dec.conges;

import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;

import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.conges.models.Conge;
import com.decathlon.dec.mappers.CongeDtoMapper;
import com.decathlon.dec.users.models.User;

import jakarta.validation.Valid;

@Service
public class CongeService {

    @Autowired
    private CongeRepository congeRepository;
    @Autowired
    private CongeDtoMapper congeDtoMapper;

    public Conge createConge(User user, CreateCongeDto createCongeDto) {
        
        Conge conge = congeDtoMapper.createDtoToConge(createCongeDto);
        conge.setUser(user);
        return congeRepository.save(conge);
    }
    
}
