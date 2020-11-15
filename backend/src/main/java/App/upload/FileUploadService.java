package App.upload;


import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Handles file uploading
 */
@Service
public class FileUploadService {
    private Path path = Paths.get("src/main/uploads");


    public void storeFile(MultipartFile file) throws IOException {
        // Check if file is empty (could break dir otherwise)
        if (file.isEmpty()) {
            throw new EmptyFileException("The uploaded file: " + file.getOriginalFilename() + " is empty.");
        }
        // Create a path for the new file
        Path destinationPath = this.path.resolve(Paths.get(file.getOriginalFilename()))
                .normalize().toAbsolutePath();
        // Saving the MultipartFile using a try-with-resources statement
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw e;
        }
    }


    public File loadFile(String fileName) {
        try {
            File file = path.resolve(fileName).toFile();
            return file;
        } catch (Exception e) {
            throw new CouldNotLoadFileException("Could not find the file to load.", e);
        }
    }


    public XLog parseFile(File file) {
        XesXmlParser parser = new XesXmlParser();
        try {
            List<XLog> logs = parser.parse(file);
            return logs.get(0);
        } catch (Exception e) {
            throw new CouldNotParseException("Could not parse the uploaded log.", e);
        }

    }


    public void deleteDir() {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    public void initDir() {
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            System.out.println("Could not initialize new directory in : " + path);
            e.printStackTrace();
        }
    }



/*
    public List<String> getValidAttributes(File file) throws Exception {
        XesXmlParser parser = new XesXmlParser();
            List<XLog> logs = parser.parse(file);
            XLog log = logs.get(0);
            return LogProcessor.getValidAttributeKeys(log);
    }
*/









/*    public List<String> store(MultipartFile file) {
        List<String> badResult = new ArrayList<>();
        // Create a path for the new file
        Path destinationPath = this.path.resolve(Paths.get(file.getOriginalFilename()))
                .normalize().toAbsolutePath();

        // Saving the MultipartFile using a try-with-resources statement
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something went wrong during file saving");
            return badResult;
        }

        // Parsing the file
        File newFile = destinationPath.toFile();
        XesXmlParser parser = new XesXmlParser();
        try {
            List<XLog> logs = parser.parse(newFile);
            XLog log = logs.get(0);
            List<String> validAttributes = LogProcessor.getValidAttributeKeys(log);
            return validAttributes;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong during XES parsing");
            return badResult;
        }

    }*/


}