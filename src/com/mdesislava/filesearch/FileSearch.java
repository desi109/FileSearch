package com.mdesislava.filesearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileSearch {
    String path;
    String regex;
    String zipFileName;
    Pattern pattern;
    List<File> zipFiles = new ArrayList<>();


    public void walkDirectory(String path) throws IOException {
        Files.walk(Paths.get(path))
                .forEach(f -> processFile(f.toFile()));
        zipFilesMethod();
    }

    public boolean searchFile(File file) throws IOException {
        return searchFileMethod(file);
    }

    public void addFileToZip(File file) {
        if(getZipFileName() != null){
            zipFiles.add(file);
        }
    }

    public void processFile(File file) {
        try {
            if (searchFile(file)) {
                addFileToZip(file);
            }
        } catch (IOException|UncheckedIOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error processing file: " +
                    file + ": " + e);
        }
    }



    public boolean searchFileMethod(File file) throws IOException {
        return Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .anyMatch(t -> searchText(t));
    }


    public boolean searchText(String text) {
        return (this.getRegex() == null) ?  true :
                this.pattern.matcher(text).matches();
    }



    public String getRelativeFilename(File file, File baseDir) {
        String fileName = file.getAbsolutePath().substring(
                baseDir.getAbsolutePath().length());

        // IMPORTANT: the ZipEntry file name must use "/", not "\".
        fileName = fileName.replace('\\', '/');

        while (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        return fileName;
    }



    public void zipFilesMethod() throws IOException {
        try (ZipOutputStream out =
                     new ZipOutputStream(new FileOutputStream(getZipFileName())) ) {
            File baseDir = new File(getPath());

            for (File file : zipFiles) {
                // fileName must be a relative path, not an absolute one.
                String fileName = getRelativeFilename(file, baseDir);

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);

                Files.copy(file.toPath(), out);

                out.closeEntry();
            }
        }
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

}