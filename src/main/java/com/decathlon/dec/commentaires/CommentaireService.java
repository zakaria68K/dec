package com.decathlon.dec.commentaires;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.decathlon.dec.commentaires.dto.CreateCommentaireDto;
import com.decathlon.dec.commentaires.dto.EditCommentaireDto;
import com.decathlon.dec.commentaires.models.Commentaire;
import com.decathlon.dec.mappers.CommentaireMapper;
import com.decathlon.dec.publications.PublicationRepository;
import com.decathlon.dec.publications.models.Publication;
import com.decathlon.dec.users.models.User;

import jakarta.validation.Valid;

@Service
public class CommentaireService {

    @Autowired
    CommentaireRepository commentaireRepository;
    @Autowired
    CommentaireMapper commentaireMapper;

    @Autowired
    PublicationRepository publicationRepository;
    
    public Commentaire createNewCommentaireForUser(User user, CreateCommentaireDto createCommentaireDto, Long id) {
        Commentaire commentaire = commentaireMapper.createDtoToCommentaire(createCommentaireDto);
        commentaire.setUser(user);
        //get the publication by id
        Publication publication = publicationRepository.findById(id).orElseThrow();
        commentaire.setPublication(publication);
        return commentaireRepository.save(commentaire);
        
    }
    public Iterable<Commentaire> getAllCommentairesPublication(Long id) {
        
    //get all the commentaires of a specefic publication
        return commentaireRepository.findAllByPublicationId(id);



        
    }
    public Commentaire updateCommentaire(User user, Long id,  EditCommentaireDto editCommentaireDto) {
        Commentaire commentaire = commentaireRepository.findById(id).orElseThrow();
        if(commentaire.getUser().getId() != user.getId()) {
            throw new RuntimeException("You are not allowed to edit this commentaire");
        }
        commentaireMapper.updateCommentaireFromDto(editCommentaireDto, commentaire);
        commentaireRepository.save(commentaire);
        return commentaire;
    }
    public void deleteCommentaire(User user, Long id) {
        Optional<Commentaire> commentaire = commentaireRepository.findById(id);
        if(commentaire.isPresent()) {
            if(commentaire.get().getUser().getId() != user.getId()) {
                throw new RuntimeException("You are not allowed to delete this commentaire");
            }
            commentaireRepository.delete(commentaire.get());
        }
    }

    
    
}
