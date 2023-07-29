package com.decathlon.dec.conges;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.conges.enumerations.CongeStatus;
import com.decathlon.dec.conges.models.Conge;
import com.decathlon.dec.mappers.CongeDtoMapper;
import com.decathlon.dec.users.models.User;

import jakarta.transaction.Transactional;

@Service
public class CongeService {

private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
      return new ResponseStatusException(HttpStatus.NOT_FOUND, "Conge not found");
   };

    @Autowired
    private CongeRepository congeRepository;
    @Autowired
    private CongeDtoMapper congeDtoMapper;

    public Conge createConge(User user, CreateCongeDto createCongeDto) {
        
        Conge conge = congeDtoMapper.createDtoToConge(createCongeDto);
        conge.setUser(user);
        return congeRepository.save(conge);
    }
  
    @Transactional
    public Page<Conge> getAllUserCongesPaginated(User user, Pageable pageable) {
        return congeRepository.findAllByUser(user, pageable);
    }

    @Transactional
    public Page<Conge> getAllPendingConges(Pageable pageable) {
        return congeRepository.findByStatus(CongeStatus.PENDING, pageable);
    }

    @Transactional
    public Page<Conge> getAllConfirmedConges(Pageable pageable) {
        return congeRepository.findByStatus(CongeStatus.CONFIRMED, pageable);
    }

    public void deleteUserConge(User user, Long id ){
        congeRepository.delete(congeRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER));
    }

    public Conge getCongeById(Long id) {
        return congeRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    }

    
    Conge cancelUserConge(User user,Long id) throws Exception {
        Conge conge = congeRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);
        if(conge.getStatus() == CongeStatus.PENDING){
            conge.setStatus(CongeStatus.CANCELLED);
            return congeRepository.save(conge);
        }
        else{
            throw new Exception("Vous ne pouvez pas annuler ce congé");
        }
    }

    Conge acceptUserConge(User user,Long id) {
        Conge conge = congeRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);

        //check if the conge is already confirmed
        if(conge.getStatus() == CongeStatus.CONFIRMED){
            throw new IllegalArgumentException("Ce congé est déjà confirmé");
        }
        conge.setStatus(CongeStatus.CONFIRMED);
        return congeRepository.save(conge);


    }

}

    
