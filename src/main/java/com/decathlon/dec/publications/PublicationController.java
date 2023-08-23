package com.decathlon.dec.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicationController {

    @Autowired
    PublicationService publicationService;
    
    
    
}
