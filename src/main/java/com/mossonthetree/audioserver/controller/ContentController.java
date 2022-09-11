package com.mossonthetree.audioserver.controller;

import com.mossonthetree.audioserver.file.FileInStream;
import com.mossonthetree.audioserver.file.FileOutStream;
import com.mossonthetree.audioserver.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/content")
public class ContentController {
    @Autowired
    private ContentService service;

    @GetMapping("")
    public ResponseEntity<?> getArtists() {
        return ResponseEntity.ok(service.getArtists());
    }

    @GetMapping("{artist}")
    public ResponseEntity<?> getAlbums(@PathVariable String artist) {
        return ResponseEntity.ok(service.getAlbums(artist));
    }

    @GetMapping("{artist}/{album}")
    public ResponseEntity<?> getSongs(@PathVariable String artist, @PathVariable String album) {
        return ResponseEntity.ok(service.getSongs(artist, album));
    }
}
