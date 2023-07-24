package com.decathlon.dec.conges;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.conges.models.Conge;
import com.decathlon.dec.users.models.MyUserDetails;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/conges")
public class CongeController {

    @Autowired
    CongeService congeService;
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public Conge createConge(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @Valid @RequestBody CreateCongeDto createCongeDto){
            if(createCongeDto.getStartDate().after(createCongeDto.getEndDate())){
                throw new IllegalArgumentException("La date de début doit être avant la date de fin");
            } //check if the total is bigger than the days between the end and the start date.
            else if(createCongeDto.getEndDate().getTime() - createCongeDto.getStartDate().getTime() > userDetails.getUser().getTotal() * 86400000){
                throw new IllegalArgumentException("Le nombre de jours demandés est supérieur au nombre de jours entre la date de début et la date de fin");
            }
            else{
                return congeService.createConge(userDetails.getUser(), createCongeDto);
            }

        }
    
    
}
