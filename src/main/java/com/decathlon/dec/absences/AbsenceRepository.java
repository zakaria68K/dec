package com.decathlon.dec.absences;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.dec.absences.enumerations.AbsenceStatus;
import com.decathlon.dec.absences.models.Absence;
import com.decathlon.dec.users.models.User;


public interface AbsenceRepository  extends JpaRepository<Absence, Long> {
    
    public Page<Absence> findAllByUser(User user, Pageable pageable);
    public Page<Absence> findByStatus(AbsenceStatus status,Pageable pageable);
    public Optional<Absence> findByIdAndUser(long id, User user);
}
