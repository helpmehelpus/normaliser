package au.edu.adelaide.set.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class FW implements IWriter {

    private static FW instance;

    public static FW instance() {
        if (instance == null) {
            instance = new FW();
        }
        return instance;
    }

    private FW() {

    }

    @Override
    public void writeMethodStringToFile(String outputFile, String methodString) throws FileNotFoundException {
//        try (PrintWriter pw = new PrintWriter(new FileOutputStream(outputFile, true))) {
//            pw.println(methodString);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        File file = new File(outputFile);
        try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw);) {
            bw.write(methodString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
