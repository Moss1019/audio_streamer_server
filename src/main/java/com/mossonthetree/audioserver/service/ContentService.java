package com.mossonthetree.audioserver.service;

import com.mossonthetree.audioserver.file.DirectoryInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {
    public List<String> getArtists() {
        DirectoryInfo dInfo = new DirectoryInfo("content");
        return dInfo.getDirectories();
    }

    public List<String> getAlbums(String artist) {
        String directoryPath = String.format("content/%s", artist);
        DirectoryInfo dInfo = new DirectoryInfo(directoryPath);
        return dInfo.getDirectories();
    }

    public List<String> getSongs(String artist, String album) {
        String directoryPath = String.format("content/%s/%s", artist, album);
        DirectoryInfo dInfo = new DirectoryInfo(directoryPath);
        return dInfo.getFiles();
    }
}
