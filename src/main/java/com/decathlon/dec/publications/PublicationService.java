package com.decathlon.dec.publications;

import java.io.IOException;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.decathlon.dec.mappers.PublicationMapper;
import com.decathlon.dec.publications.dto.CreatePublicationDto;
import com.decathlon.dec.publications.dto.UpdatePublicatioDto;
import com.decathlon.dec.publications.models.Publication;
import com.decathlon.dec.users.models.User;

import jakarta.transaction.Transactional;

@Service
public class PublicationService {
    

    @Autowired 
    PublicationMapper publicationMapper;

    @Autowired
    PublicationRepository publicationRepository;



    private static Supplier<ResponseStatusException> PUBLICATION_NOT_FOUND_HANDLER = () -> {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publication not found");
      };
    @Transactional
    public Publication uploadPublicationForUser(User user, MultipartFile image, CreatePublicationDto createPublicationDto){

        Publication newPublication = publicationMapper.createDtoToPublication(createPublicationDto);
        try{
            newPublication.setImage(image.getBytes());
        } catch(IOException exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occured while saving the image");
        }

        newPublication.setUser(user);

        publicationRepository.save(newPublication);
        return newPublication;
    }

    @Transactional
    public Page<Publication> getAllUserPubicationsPaginated(User user, Pageable pageable){
        return publicationRepository.findByUser(user, pageable);
    }

    @Transactional
    public Publication getPublicationByIdAndUser(long pubId, User user){
        return publicationRepository.findByIdAndUser(pubId,user).orElseThrow(PUBLICATION_NOT_FOUND_HANDLER);
    }

    @Transactional
    public Publication editUserPublication(long id, UpdatePublicatioDto updatePublicatioDto, User user){
        Publication publication = publicationRepository.findByIdAndUser(id, user).orElseThrow(PUBLICATION_NOT_FOUND_HANDLER);
        publicationMapper.updatePublicationFromDto(updatePublicatioDto,publication);
        publicationRepository.save(publication);

        return publication;
    }

    public void deleteUserPublication(long id , User user){
        publicationRepository.delete(
            publicationRepository.findById(id).orElseThrow(PUBLICATION_NOT_FOUND_HANDLER));
        
    }
}
