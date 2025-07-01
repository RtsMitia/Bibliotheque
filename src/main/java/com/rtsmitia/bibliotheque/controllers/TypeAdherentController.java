package com.rtsmitia.bibliotheque.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rtsmitia.bibliotheque.models.TypeAdherent;
import com.rtsmitia.bibliotheque.services.TypeAdherentService;

@RestController
@RequestMapping("/api/typeAdherents")
public class TypeAdherentController {

    private final TypeAdherentService typeAdherentService;

    public TypeAdherentController(TypeAdherentService typeAdherentService) {
        this.typeAdherentService = typeAdherentService;
    }

    @GetMapping
    public List<TypeAdherent> getAllTypes() {
        return typeAdherentService.getAllTypes();
    }
}
