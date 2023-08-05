package com.decathlon.dec.documents;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.decathlon.dec.documents.models.Document;
import com.decathlon.dec.users.models.User;

public interface DocumentsRepository extends JpaRepository<Document, Long> {


  public Optional<Document> findByIdAndUser(long id, User user);

  public Iterable<Document> findByUser(User user);

  public Page<Document> findByUser(User user, Pageable pageable);

  public Page<Document> findByUserId(long userId, Pageable pageable);


}
