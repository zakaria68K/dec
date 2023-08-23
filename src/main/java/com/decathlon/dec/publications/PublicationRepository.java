package com.decathlon.dec.publications;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.dec.publications.models.Publication;
import com.decathlon.dec.users.models.User;

public interface PublicationRepository  extends JpaRepository<Publication, Long> {
    
    public Page<Publication> findByUser(User user, Pageable pageable);

    public Optional<Publication> findByIdAndUser(long id, User user);
}
