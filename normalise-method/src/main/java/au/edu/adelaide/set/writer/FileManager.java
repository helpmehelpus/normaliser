package au.edu.adelaide.set.writer;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private static FileManager instance;

    public static FileManager instance() {
        if(instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    private FileManager() {

    }

    public void createProjectOutputDirectory(File projectOutputDirectory) {
        projectOutputDirectory.mkdirs();
    }

    public void createOutputFile(File outputFile) throws IOException {
        if (!outputFile.createNewFile()) {
            System.out.println("File already exists. Consider deleting/moving the 'normalised' folder");
        }
    }
}
