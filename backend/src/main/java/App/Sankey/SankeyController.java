package App.Sankey;

import App.upload.FileUploadService;
import org.deckfour.xes.model.XLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class SankeyController {
    private final FileUploadService storageService;
    private final SankeyGenerator sankeyGenerator;

    @Autowired
    public SankeyController(FileUploadService storageService, SankeyGenerator sankeyGenerator) {
        this.storageService = storageService;
        this.sankeyGenerator = sankeyGenerator;
    }


    @PostMapping("/createSankey")
    public ResponseEntity<String> createSankey(@RequestParam("fileName") String fileName,
                                               @RequestParam("attributeKey") String attributeKey,
                                               @RequestParam("operation") String operator,
                                               @RequestParam("aggregationFunc") String aggregationFunc,
                                               @RequestParam("isGrouped") Boolean isGrouped) {
        try {
            File file = storageService.loadFile(fileName);
            XLog log = storageService.parseFile(file);
            SankeyModel sankeyModel = sankeyGenerator.createSankey(log, attributeKey, operator, aggregationFunc, isGrouped);
            System.out.println(sankeyModel.toJSONString());
            return new ResponseEntity<>(sankeyModel.toJSONString().toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
