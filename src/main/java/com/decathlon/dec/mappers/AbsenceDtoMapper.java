package com.decathlon.dec.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.decathlon.dec.absences.dto.CreateAbsenceDto;
import com.decathlon.dec.absences.dto.UpdateAbsenceDto;
import com.decathlon.dec.absences.models.Absence;

@Mapper(componentModel = "spring")
public interface AbsenceDtoMapper {
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target="user", ignore= true)
    @Mapping(target="status", ignore= true)
    Absence createDtoToAbsence(CreateAbsenceDto createAbsenceDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target="user", ignore= true)
    @Mapping(target="status", ignore= true)
    void updateAbsenceFromDto(UpdateAbsenceDto updateAbsenceDto, @MappingTarget Absence absence);
} 
