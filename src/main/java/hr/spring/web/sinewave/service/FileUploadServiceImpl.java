package hr.spring.web.sinewave.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Path fileStorageLocation;
    private final String configuredUploadDir; // To construct the relative path for DB

    // Constructor to initialize the file storage location
    public FileUploadServiceImpl(@Value("${app.music.upload-dir:music/}") String uploadDir) {
        this.configuredUploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        this.fileStorageLocation = Paths.get(this.configuredUploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null. Cannot store.");
        }

        // Normalize and clean the filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFilename.contains("..")) {
            // This is a security check for path traversal
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + originalFilename);
        }

        // Create a unique filename to prevent overwrites and add some randomness
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i); // e.g., ".mp3"
        }
        // Remove extension for base name, then sanitize
        String baseName = originalFilename.substring(0, i > 0 ? i : originalFilename.length())
                .replaceAll("[^a-zA-Z0-9.\\-_]", "_");


        String uniqueFilename = UUID.randomUUID().toString() + "_" + baseName + extension;

        try {
            // Resolve the target path
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);

            // Copy file to the target location (Replacing existing file with the same name is a choice here)
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return the relative path to be stored in the database
            // This should match how you want to access/reference the file later
            // If configuredUploadDir was "music/", this will be "music/uniqueFilename.mp3"
            return Paths.get(configuredUploadDir).getFileName().resolve(uniqueFilename).toString().replace("\\", "/");


        } catch (IOException ex) {
            // Log the exception
            throw new IOException("Could not store file " + uniqueFilename + ". Please try again!", ex);
        }
    }
}