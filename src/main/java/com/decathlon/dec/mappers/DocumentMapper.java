package com.decathlon.dec.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.decathlon.dec.documents.dto.CreateDocumentDto;
import com.decathlon.dec.documents.dto.EditDocumentDto;
import com.decathlon.dec.documents.models.Document;

@Mapper(componentModel = "spring")
public abstract class DocumentMapper {
    
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "file", ignore = true)
  @Mapping(target = "uploadedAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract Document createDtoToDocument(CreateDocumentDto createDocumentDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "file", ignore = true)
  @Mapping(target = "uploadedAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract void updateDocumentFromDto(EditDocumentDto editDocumentDto,@MappingTarget Document document);
}
