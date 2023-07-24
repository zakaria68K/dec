package com.decathlon.dec.conges;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.dec.conges.models.Conge;
import com.decathlon.dec.users.models.User;
import com.decathlon.dec.conges.enumerations.CongeStatus;


public interface CongeRepository  extends JpaRepository<Conge, Long>{
    
    public Page<Conge> findAllByUser(User user, Pageable pageable);
    public Page<Conge> findByStatus(CongeStatus status,Pageable pageable);
    public Optional<Conge> findByIdAndUser(long id, User user);
}
