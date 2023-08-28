package com.decathlon.dec.absences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import com.decathlon.dec.absences.dto.CreateAbsenceDto;
import com.decathlon.dec.absences.dto.UpdateAbsenceDto;
import com.decathlon.dec.absences.models.Absence;
import com.decathlon.dec.mappers.AbsenceDtoMapper;
import com.decathlon.dec.users.dto.PaginatedResponse;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.users.models.User;
import com.decathlon.dec.utils.MessageResponse;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/absences")
public class AbsenceController {
    
    @Autowired
    AbsenceService absenceService;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private AbsenceDtoMapper absenceDtoMapper;

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public Absence createAbsence(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @Valid @RequestBody CreateAbsenceDto createAbsenceDto){
            if(createAbsenceDto.getStartDate().after(createAbsenceDto.getEndDate())){
                throw new IllegalArgumentException("La date de début doit être avant la date de fin");
            } //check if the total is bigger than the days between the end and the start date.

            else{
                return absenceService.createAbsence(userDetails.getUser(), createAbsenceDto);
            }

        }

        @GetMapping(path = "")
        public PaginatedResponse<Absence> getAllUserAbsences(@AuthenticationPrincipal MyUserDetails userDetails,
        Pageable pageable){
                
                Page<Absence> results = absenceService.getAllUserAbsencesPaginated(userDetails.getUser(), pageable);
                PaginatedResponse<Absence> response = PaginatedResponse.<Absence>builder()
                .results(results.getContent())
                .page(results.getNumber())
                .count(results.getNumberOfElements())
                .totalPages(results.getTotalPages())
                .totalItems(results.getTotalElements())
                .last(results.isLast())
                .build();
            return response;
        }
        //get all the absences that the status is pending
        @GetMapping(path = "/pendingabsences")
        public PaginatedResponse<Absence> getAllPendingAbsences(Pageable pageable){
            Page<Absence> results = absenceService.getAllPendingAbsences(pageable);
            PaginatedResponse<Absence> response = PaginatedResponse.<Absence>builder()
            .results(results.getContent())
            .page(results.getNumber())
            .count(results.getNumberOfElements())
            .totalPages(results.getTotalPages())
            .totalItems(results.getTotalElements())
            .last(results.isLast())
            .build();
        return response;
        
    }

    //get all the absences that the status is accepted
        @GetMapping(path = "/acceptedabsences")
        public PaginatedResponse<Absence> getAllConfirmedAbsences(Pageable pageable){
            Page<Absence> results = absenceService.getAllConfirmedAbsences(pageable);
            PaginatedResponse<Absence> response = PaginatedResponse.<Absence>builder()
            .results(results.getContent())
            .page(results.getNumber())
            .count(results.getNumberOfElements())
            .totalPages(results.getTotalPages())
            .totalItems(results.getTotalElements())
            .last(results.isLast())
            .build();
        return response;

    }

        @GetMapping(value = "/{id}")
        Absence getUserAbsence(@PathVariable("id") Long id) {
            return absenceService.getAbsenceById(id);
        }

        
        @PatchMapping("/{id}")
        Absence editUserAbsence(@PathVariable("id") Long id, @Valid @RequestBody UpdateAbsenceDto updateAbsenceDto, User user){
           return absenceService.editUserAbsence(id, updateAbsenceDto, user);
        }

        @PatchMapping("/{id}/cancel")
        Absence cancelUserAbsence(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id) throws Exception{
            return absenceService.cancelUserAbsence(userDetails.getUser(), id);
        }
        
    
    
    
        @PatchMapping("/{id}/accept")
        Absence acceptUserAbsence(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id) throws Exception{
            return absenceService.acceptUserAbsence(userDetails.getUser(), id);
        }

        @DeleteMapping("/{id}")
        public MessageResponse deleteUserAbsence(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id){
            absenceService.deleteUserAbsence(userDetails.getUser(), id);
            return  new MessageResponse("Absence deleted successfully");
        }



    }
