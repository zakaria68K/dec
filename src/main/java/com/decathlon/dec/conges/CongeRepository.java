package com.decathlon.dec.conges;
import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.dec.conges.models.Conge;

public interface CongeRepository  extends JpaRepository<Conge, Long>{
    
}
