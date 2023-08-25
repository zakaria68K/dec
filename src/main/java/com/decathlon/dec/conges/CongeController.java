package com.decathlon.dec.conges;

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

import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.conges.dto.UpdateCongeDto;
import com.decathlon.dec.conges.models.Conge;
import com.decathlon.dec.mappers.CongeDtoMapper;
import com.decathlon.dec.users.dto.PaginatedResponse;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.users.models.User;
import com.decathlon.dec.utils.MessageResponse;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/conges")
public class CongeController {

    @Autowired
    CongeService congeService;

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private CongeDtoMapper congeDtoMapper;

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public Conge createConge(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @Valid @RequestBody CreateCongeDto createCongeDto){
            if(createCongeDto.getStartDate().after(createCongeDto.getEndDate())){
                throw new IllegalArgumentException("La date de début doit être avant la date de fin");
            } //check if the total is bigger than the days between the end and the start date. 86,400,000
            else if(createCongeDto.getEndDate().getTime() - createCongeDto.getStartDate().getTime() > userDetails.getUser().getTotal()* 86400000){
                throw new IllegalArgumentException("Le nombre de jours demandés est supérieur au nombre total de jours ");
            }//
            else{
                return congeService.createConge(userDetails.getUser(), createCongeDto);
            }

        }

        @GetMapping(path = "")
        public PaginatedResponse<Conge> getAllUserConges(@AuthenticationPrincipal MyUserDetails userDetails,
                Pageable pageable){

                    Page<Conge> results = congeService.getAllUserCongesPaginated(userDetails.getUser(), pageable);
                    PaginatedResponse<Conge> response = PaginatedResponse.<Conge>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .count(results.getNumberOfElements())
                    .totalPages(results.getTotalPages())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
        }

        //get all conges that the status is pending
        @GetMapping(path = "/pendingconges")
        public PaginatedResponse<Conge> getAllPendingConges(Pageable pageable){
            Page<Conge> results = congeService.getAllPendingConges(pageable);
            PaginatedResponse<Conge> response = PaginatedResponse.<Conge>builder()
            .results(results.getContent())
            .page(results.getNumber())
            .count(results.getNumberOfElements())
            .totalPages(results.getTotalPages())
            .totalItems(results.getTotalElements())
            .last(results.isLast())
            .build();
        return response;
    
        }

        //get all conges that the status is accepted
        @GetMapping(path = "/acceptedconges")
        public PaginatedResponse<Conge> getAllConfirmedConges(Pageable pageable){
            Page<Conge> results = congeService.getAllConfirmedConges(pageable);
            PaginatedResponse<Conge> response = PaginatedResponse.<Conge>builder()
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
        Conge getUserConge(@PathVariable("id") Long id) {
            return congeService.getCongeById(id);
        }

        @PatchMapping(value = "/{id}")
        public Conge editUserConge(@PathVariable("id") Long id, UpdateCongeDto updateCongeDto, User user){
            Conge conge = congeRepository.findByIdAndUser(id,user).orElseThrow(() -> new IllegalArgumentException("Conge not found"));
            congeDtoMapper.updateCongeFromDto(updateCongeDto, conge);
            congeRepository.save(conge);
            return conge;
        }

        @DeleteMapping(value = "/{id}")
        public MessageResponse deleteUserConge(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id){
            congeService.deleteUserConge(userDetails.getUser(), id);
            return  new MessageResponse("Conge deleted successfully");
       
        }

        @PatchMapping("/{id}/cancel")
        Conge cancelUserConge(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id) throws Exception{
            return congeService.cancelUserConge(userDetails.getUser(), id);
        }

        @PatchMapping("/{id}/accept")
        Conge acceptUserConge(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id) throws Exception{
            return congeService.acceptUserConge(userDetails.getUser(), id);
        }




    
}
