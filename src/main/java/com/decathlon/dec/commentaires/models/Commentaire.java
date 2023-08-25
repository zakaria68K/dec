package com.decathlon.dec.commentaires.models;


import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.decathlon.dec.publications.models.Publication;
import com.decathlon.dec.users.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "commentaires")
public class Commentaire {
    
    @Id
    @Column(name = "commentaire_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Publication publication;

    @Column(name = "contenu", nullable = false)
    private String contenu;

    @CreationTimestamp
    @Column(name = "date", nullable = false)
    private Date date;
}
