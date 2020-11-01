package App.Storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FileStorageInit implements CommandLineRunner {
    @Autowired
    private FileStorageService storageService;

    // Runs before spring boot application starts up
    @Override
    public void run(String... args) throws Exception {
        storageService.deleteDir();
        storageService.initDir();

    }
}
