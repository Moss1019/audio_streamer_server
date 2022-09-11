package com.mossonthetree.audioserver.controller;

import com.mossonthetree.audioserver.service.WavService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/wav-songs")
public class WavController {
    @Autowired
    private WavService service;

    @GetMapping("{artist}/{album}/{songname}")
    public ResponseEntity<?> getSongsDetails(@PathVariable String artist,
                                             @PathVariable String album,
                                             @PathVariable(name = "songname") String songName) {
        return ResponseEntity.ok(service.getSongLength(artist, album, songName));
    }

    @GetMapping("{artist}/{album}/{songname}/{length}/{offset}")
    public ResponseEntity<?> getSongsChunk(@PathVariable String artist,
                                           @PathVariable String album,
                                           @PathVariable(name = "songname") String songName,
                                           @PathVariable int length,
                                           @PathVariable int offset) {
        HttpHeaders headers = new HttpHeaders();
        List<String> values = new ArrayList<>();
        values.add("application/octet-stream");
        headers.put("Content-Type", values);
        return ResponseEntity.ok()
                .headers(headers)
                .body(service.getSongChunk(offset, length, artist, album, songName));
    }

    @GetMapping("ex")
    public ResponseEntity<?> getExampleBuffer() {
        byte[] buffer = new byte[16];
        buffer[0] = 100;
        buffer[1] = -127;
        buffer[2] = -1;
        buffer[3] = -2;
        buffer[4] = 10;
        buffer[5] = 10;
        HttpHeaders headers = new HttpHeaders();
        List<String> values = new ArrayList<>();
        values.add("application/octet-stream");
        headers.put("Content-Type", values);
        return ResponseEntity.ok()
                .headers(headers)
                .body(buffer);
    }
}
