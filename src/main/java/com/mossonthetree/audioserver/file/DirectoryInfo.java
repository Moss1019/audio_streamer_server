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
        if(filesInFolder == null) {
            return files;
        }
        for(File f : filesInFolder) {
            if(!f.isDirectory()) {
                files.add(f.getName());
            }
        }
        return files;
    }

    public List<String> getDirectories() {
        File folder = new File(folderName);
        if(!folder.isDirectory()) {
            return null;
        }
        List<String> subDirectories = new ArrayList<>();
        File[] filesInFolder = folder.listFiles();
        if(filesInFolder == null) {
            return subDirectories;
        }
        for(File f : filesInFolder) {
            if(f.isDirectory()) {
                subDirectories.add(f.getName());
            }
        }
        return subDirectories;
    }
}
