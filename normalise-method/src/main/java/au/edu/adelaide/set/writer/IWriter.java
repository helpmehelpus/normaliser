package au.edu.adelaide.set.writer;

import java.io.FileNotFoundException;

public interface IWriter {
    void writeMethodStringToFile(String outputFile, String methodString) throws FileNotFoundException;
}
