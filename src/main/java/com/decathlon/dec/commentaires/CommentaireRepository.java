package com.decathlon.dec.commentaires;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.dec.commentaires.models.Commentaire;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    Iterable<Commentaire> findAllByPublicationId(Long id);
    
}
