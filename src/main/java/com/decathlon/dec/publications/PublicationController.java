package com.decathlon.dec.publications;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.decathlon.dec.documents.dto.EditDocumentDto;
import com.decathlon.dec.publications.dto.CreatePublicationDto;
import com.decathlon.dec.publications.dto.UpdatePublicatioDto;
import com.decathlon.dec.publications.models.Publication;
import com.decathlon.dec.users.dto.PaginatedResponse;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.utils.MessageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/publications")
public class PublicationController {

    @Autowired
    PublicationService publicationService;
    
    @PostMapping(path = "",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public Publication createNewPublication(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @RequestPart(name = "image", required = true) MultipartFile image,
        @Valid @RequestPart CreatePublicationDto data
        
    )
    {
        return publicationService.uploadPublicationForUser(userDetails.getUser(),image,data);
    }

    @GetMapping(path = "")
    public PaginatedResponse<Publication> getAllUserPublications(
            @AuthenticationPrincipal MyUserDetails userDetails,
            Pageable pageable
    ){
        Page<Publication> results = publicationService.getAllUserPubicationsPaginated(
            userDetails.getUser(),
            pageable
        );
        PaginatedResponse<Publication> response = PaginatedResponse.<Publication>builder()
        .results(results.getContent())
        .page(results.getNumber())
        .totalPages(results.getTotalPages())
        .count(results.getNumberOfElements())
        .totalItems(results.getTotalElements())
        .last(results.isLast())
        .build();

        return response;
    }

    @GetMapping("/{id}")
    public Publication getUserPublication(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id){
        return publicationService.getPublicationByIdAndUser(id, userDetails.getUser());
    }

    @PatchMapping("/{id}")
    public Publication editUserPulication(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id,
      @Valid @RequestBody UpdatePublicatioDto updatePublicatioDto) {
    return publicationService.editUserPublication(id,updatePublicatioDto , userDetails.getUser());
  }

    @DeleteMapping("/{id}")
    public MessageResponse deleteUserPublication(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable long id){
        publicationService.deleteUserPublication(id, userDetails.getUser());
        return MessageResponse.builder().message("Publication deleted successfully").build();

    }






    
}
