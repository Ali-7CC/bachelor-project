package App.Storage;

import App.Shared.LogProcessor;
import App.Shared.VariantMap;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
public class FileStorageController {
    private FileStorageService storageService;

    @Autowired
    public FileStorageController(FileStorageService storageService) {
        this.storageService = storageService;
    }
    @CrossOrigin
    @PostMapping("/upload")
    public ResponseEntity<HashMap<String, List<String>>> fileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Storing the original file
            storageService.storeFile(file, file.getOriginalFilename());
            // Retrieving the original file
            File newFile = storageService.loadFile(file.getOriginalFilename());
            // Getting the valid attributes
            XLog log = storageService.parseFile(newFile);
            List<String> validAttributes = LogProcessor.getValidAttributeKeys(log);
            HashMap<String, List<String>> response = new HashMap<>();
            response.put("validAttributes", validAttributes);
            VariantMap variants = LogProcessor.findVariants(log);
            HashMap<Double, XLog> sublogs = LogProcessor.createSubLogs(log, variants);
            List<Double> percentages = new ArrayList<>();
            for(Map.Entry<Double, XLog> entry : sublogs.entrySet()){
                XLog sublog = entry.getValue();
                double percentage = entry.getKey();
                String name = file.getOriginalFilename().split(".xes")[0] + "_" + percentage + ".xes";
                storageService.storeXLog(sublog, name);
                percentages.add(percentage);
            }
            response.put("percentages", percentages.stream().sorted().map(d -> String.valueOf(d)).collect(Collectors.toList()));
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
