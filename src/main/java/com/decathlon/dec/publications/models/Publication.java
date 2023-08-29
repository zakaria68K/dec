package com.decathlon.dec.publications.models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.decathlon.dec.commentaires.models.Commentaire;
import com.decathlon.dec.users.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "publications")
public class Publication {

    @Id
    @Column(name = "pub_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "image", nullable = true)
    @JsonIgnore
    private byte[] image;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    private List<Commentaire> comments = new ArrayList<>();

    //date
    @CreationTimestamp
    @Column(name = "date", nullable = false)
    private Date date;

}
