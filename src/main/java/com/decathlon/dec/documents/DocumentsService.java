package com.decathlon.dec.documents;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.decathlon.dec.documents.dto.CreateDocumentDto;
import com.decathlon.dec.documents.dto.EditDocumentDto;
import com.decathlon.dec.documents.models.Document;
import com.decathlon.dec.mappers.DocumentMapper;
import com.decathlon.dec.users.UserRepository;
import com.decathlon.dec.users.models.User;
import com.decathlon.dec.users.services.UserService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


@Service
public class DocumentsService {



    @Autowired
    DocumentsRepository documentsRepository;

 

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    DocumentMapper documentMapper;

    private static Supplier<ResponseStatusException> DOCUMENT_NOT_FOUND_HANDLER = () -> {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
      };


    @Transactional
    public Document uploadDocumentForUser(User user, MultipartFile file, CreateDocumentDto createDocumentDto) {
         // Create the doc from DTO
    Document newDocument = documentMapper.createDtoToDocument(createDocumentDto);

    

    try {
      // Save the file
      newDocument.setFile(file.getBytes());
    } catch (IOException exception) {
      exception.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occured while saving the file");
    }

    // Set the ownership to the user
    newDocument.setUser(user);

    // Persist in db
    documentsRepository.save(newDocument);
    return newDocument;
    }


    @Transactional
    public Page<Document> getAllUserDocumentsPaginated(User user, Pageable pageable) {
     
      return documentsRepository.findByUser(user, pageable);
    }


    @Transactional
    public Document getDocumentByIdAndUser(long documentId, User user) {
      return documentsRepository.findByIdAndUser(documentId, user).orElseThrow(DOCUMENT_NOT_FOUND_HANDLER);
    }


    @Transactional
    public Document editUserDocument(long id, EditDocumentDto editDocumentDto, User user) {
      Document document = documentsRepository.findByIdAndUser(id, user).orElseThrow(DOCUMENT_NOT_FOUND_HANDLER);  
      documentMapper.updateDocumentFromDto(editDocumentDto, document);
      documentsRepository.save(document);
  
      return document;
    }
  


    public void deleteUserDocument(long id, User user) {
        documentsRepository.delete(
            documentsRepository.findById(id).orElseThrow(DOCUMENT_NOT_FOUND_HANDLER));
      }


      @Transactional
      public Document getDocumentForDownload(long documentId, User user) {
        Document document = documentsRepository.findById(documentId).orElseThrow(DOCUMENT_NOT_FOUND_HANDLER);
        if (document.getUser().getId().equals(user.getId())) {
          return document;
        } else {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
      }


      @Transactional
      public Page<Document> getPublicUserDocuments(long userId, Pageable pageable) {
        userService.getUser(userId);
        return documentsRepository.findByUserId(userId, pageable);
      }
}
