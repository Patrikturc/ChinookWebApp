package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.MediatypeDTO;
import com.sparta.pt.chinookwebapp.services.MediatypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mediatypes")
public class MediatypeController {

    private final MediatypeService mediatypeService;

    @Autowired
    public MediatypeController(MediatypeService mediatypeService) {
        this.mediatypeService = mediatypeService;
    }

    @GetMapping
    public ResponseEntity<List<MediatypeDTO>> getAllMediatypes() {
        return ResponseEntity.ok(mediatypeService.getAllMediatypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediatypeDTO> getMediatypeById(@PathVariable Integer id) {
        Optional<MediatypeDTO> mediatypeDTO = mediatypeService.getMediatypeById(id);
        return mediatypeDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MediatypeDTO> createMediatype(@RequestBody MediatypeDTO mediatypeDTO) {
        MediatypeDTO createdMediatypeDTO = mediatypeService.createMediatype(mediatypeDTO);
        return ResponseEntity.ok(createdMediatypeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MediatypeDTO> updateMediatype(@PathVariable Integer id, @RequestBody MediatypeDTO mediatypeDTO) {
        Optional<MediatypeDTO> updatedMediatypeDTO = Optional.ofNullable(mediatypeService.upsertMediatype(id, mediatypeDTO));
        return updatedMediatypeDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MediatypeDTO> patchMediatype(@PathVariable Integer id, @RequestBody MediatypeDTO mediatypeDTO) {
        Optional<MediatypeDTO> patchedMediatypeDTO = mediatypeService.patchMediatype(id, mediatypeDTO);
        return patchedMediatypeDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMediatype(@PathVariable Integer id) {
        boolean isDeleted = mediatypeService.deleteMediatype(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}