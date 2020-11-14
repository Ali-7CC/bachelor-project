package App.upload;

import App.Shared.LogProcessor;
import org.deckfour.xes.model.XLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@RestController
public class FileUploadController {
    private FileUploadService storageService;

    @Autowired
    public FileUploadController(FileUploadService storageService) {
        this.storageService = storageService;
    }
    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/upload")
    public ResponseEntity<HashMap<String, List<String>>> fileUpload(@RequestParam("file") MultipartFile file) {
        try {
            storageService.storeFile(file);
            File newFile = storageService.loadFile(file.getOriginalFilename());
            XLog log = storageService.parseFile(newFile);
            List<String> validAttributes = LogProcessor.getValidAttributeKeys(log);
            HashMap<String, List<String>> response = new HashMap<>();
            response.put("validAttributes", validAttributes);
            return ResponseEntity.ok(response);
        } catch (EmptyFileException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End point does not accept empty files" + file.getOriginalFilename(), e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not copy the file " + file.getOriginalFilename() + " to server.", e);
        } catch (CouldNotLoadFileException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No file exists with name " + file.getOriginalFilename() + " on the server. ", e);
        } catch (CouldNotParseException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Could not parse file " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Something went wrong during upload" + file.getOriginalFilename(), e);
        }
    }

}
