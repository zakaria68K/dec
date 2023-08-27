package com.decathlon.dec.commentaires;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.decathlon.dec.commentaires.dto.CreateCommentaireDto;
import com.decathlon.dec.commentaires.dto.EditCommentaireDto;
import com.decathlon.dec.commentaires.models.Commentaire;
import com.decathlon.dec.users.models.MyUserDetails;
import com.decathlon.dec.utils.MessageResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@RestController
@RequestMapping("/commentaires")
public class CommentaireController {
    
    
    @Autowired
    CommentaireService commentaireService;

    @PostMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Commentaire createNewCommentaire(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @Valid @RequestBody CreateCommentaireDto createCommentaireDto,
        @PathVariable("id") Long id
    )
    {
        return commentaireService.createNewCommentaireForUser(userDetails.getUser(), createCommentaireDto, id);
    }

    @GetMapping(path = "/{id}")
    public Iterable<Commentaire> getAllCommentairesPublication(@PathVariable("id") Long id) {
        return commentaireService.getAllCommentairesPublication(id);
    }
    
    @PatchMapping(path = "/{id}")
    public Commentaire updateCommentaire(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @PathVariable("id") Long id,
        @Valid @RequestBody EditCommentaireDto editCommentaireDto
    )
    {
        return commentaireService.updateCommentaire(userDetails.getUser(), id, editCommentaireDto);
    }

    @DeleteMapping(path = "/{id}")
    public MessageResponse deleteCommentaire(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @PathVariable("id") Long id
    )
    {
        commentaireService.deleteCommentaire(userDetails.getUser(), id);
        return  new MessageResponse("Comment deleted successfully");
       
    }
    
}
