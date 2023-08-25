package com.decathlon.dec.conges.models;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;

import com.decathlon.dec.conges.enumerations.CongeStatus;
import com.decathlon.dec.users.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conges")
public class Conge {
   
    @Id
    @Column(name = "conge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
   // @Column(name = "user_id", nullable = false)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private CongeStatus status = CongeStatus.PENDING;

   
}
