package app.backup;

import app.AppConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MySharedFile implements Serializable {
    private final boolean isPublic; // Flag indicating if the file is public or private
    private final String filePath; // Path to the file
    private int ownerPort; // Port of the owner node

    // Constructor to initialize the distributed file with its properties
    public MySharedFile(String filePath, boolean isPublic, int ownerPort){
        this.isPublic = isPublic;
        this.filePath = filePath;
        this.ownerPort = ownerPort;
    }

    // Method to return a string representation of the distributed file
    @Override
    public String toString() {
        return (isPublic ? "[Public]" : "[Private]") + " " + filePath;
    }

    // Getter for the isPublic property
    public boolean isPublic() {
        return isPublic;
    }

    // Getter for the filePath property
    public String getFilePath() {
        return filePath;
    }

    // Getter for the ownerPort property
    public int getOwnerPort() {
        return ownerPort;
    }

    public String getContent() {
        Path path = Paths.get(filePath);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Error while reading file: " + filePath);
            throw new RuntimeException(e);
        }
    }
}
