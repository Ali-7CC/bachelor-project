package App.Storage;


import App.Shared.LogProcessor;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles file uploading
 */
@Service
public class FileStorageService {
    private Path path = Paths.get("src/main/uploads");


    public ResponseEntity<?> store(MultipartFile file) {
        try {
            // Create a path for the new file
            Path destinationPath = this.path.resolve(Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            // Saving the MultipartFile using a try-with-resources statement
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationPath,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity(new FileStorageResponse(null, "Error during file saving.",
                        LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Parsing the file
            File newFile = destinationPath.toFile();
            XesXmlParser parser = new XesXmlParser();
            try {
                List<XLog> logs = parser.parse(newFile);
                XLog log = logs.get(0);
                // Creating the return response
                List<String> validAttributes = LogProcessor.getValidAttributeKeys(log);
                System.out.println(validAttributes);
                return new ResponseEntity(new FileStorageResponse(validAttributes, null,
                        LocalDateTime.now()), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity(new FileStorageResponse(null, "Error during parsing the file.",
                        LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(new FileStorageResponse(null, "Something went wrong during file storing",
                    LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public void deleteDir(){
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    public void initDir(){
        try {
            Files.createDirectories(path);
        }
        catch (Exception e) {
            System.out.println("Could not initialize temporary storage directory: " + path);
            e.printStackTrace();
        }
    }


}
