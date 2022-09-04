package com.mossonthetree.audioserver.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryInfo {
    private String folderName;

    public DirectoryInfo(String folderName) {
        this.folderName = folderName;
    }

    public List<String> getFiles() {
        File folder = new File(folderName);
        if(!folder.isDirectory()) {
            return null;
        }
        List<String> files = new ArrayList<>();
        File[] filesInFolder = folder.listFiles();
        for(File f : filesInFolder) {
            if(!f.isDirectory()) {
                files.add(f.getName());
            }
        }
        return files;
    }
}
