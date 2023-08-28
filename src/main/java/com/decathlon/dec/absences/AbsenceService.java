package com.decathlon.dec.absences;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.decathlon.dec.absences.dto.CreateAbsenceDto;
import com.decathlon.dec.absences.dto.UpdateAbsenceDto;
import com.decathlon.dec.absences.enumerations.AbsenceStatus;
import com.decathlon.dec.absences.models.Absence;
import com.decathlon.dec.mappers.AbsenceDtoMapper;
import com.decathlon.dec.users.models.User;

import jakarta.validation.Valid;

@Service
public class AbsenceService {

    private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
      return new ResponseStatusException(HttpStatus.NOT_FOUND, "Absence not found");
   };

    @Autowired
    private AbsenceRepository AbsenceRepository;

    @Autowired
    private AbsenceDtoMapper absenceDtoMapper;

    public Absence createAbsence(User user, CreateAbsenceDto createAbsenceDto) {
        Absence absence = absenceDtoMapper.createDtoToAbsence(createAbsenceDto);
        absence.setUser(user);
        return AbsenceRepository.save(absence);
  
    }

    public Page<Absence> getAllUserAbsencesPaginated(User user, Pageable pageable) {
        return AbsenceRepository.findAllByUser(user, pageable);
    }

    public Page<Absence> getAllPendingAbsences(Pageable pageable) {
        return AbsenceRepository.findByStatus(AbsenceStatus.PENDING, pageable);
    }

    public Page<Absence> getAllConfirmedAbsences(Pageable pageable) {
        return AbsenceRepository.findByStatus(AbsenceStatus.APPROVED, pageable);
    }

    public Absence getAbsenceById(Long id) {
        return AbsenceRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    }

    public Absence cancelUserAbsence(User user, Long id) {
        Absence absence = AbsenceRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);
        absence.setStatus(AbsenceStatus.REJECTED);
        return AbsenceRepository.save(absence);
    }

    public Absence acceptUserAbsence(User user, Long id) {
        Absence absence = AbsenceRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);
        absence.setStatus(AbsenceStatus.APPROVED);
        return AbsenceRepository.save(absence);
    }

    public void deleteUserAbsence(User user, Long id) {
        AbsenceRepository.delete(AbsenceRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER));
    }

    public Absence editUserAbsence(Long id, @Valid UpdateAbsenceDto updateAbsenceDto, User user) {
        Absence absence = AbsenceRepository.findByIdAndUser(id,user).orElseThrow(() -> new IllegalArgumentException("Absence not found"));
        absenceDtoMapper.updateAbsenceFromDto(updateAbsenceDto, absence);
        AbsenceRepository.save(absence);
        return absence;
    }

    
}
