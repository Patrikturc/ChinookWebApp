package com.sparta.pt.chinookwebapp.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import com.sparta.pt.chinookwebapp.models.Mediatype;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.attribute.standard.Media;
import java.util.Optional;

public interface MediatypeRepository extends JpaRepository<Mediatype, Integer> {
    Optional<Mediatype> findByName(String name);
}