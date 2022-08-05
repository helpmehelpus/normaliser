package au.edu.adelaide.set.normaliser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import au.edu.adelaide.set.writer.FW;
import au.edu.adelaide.set.writer.FileManager;

public class Normaliser {

    static final String INPUT_DIR = "src/main/resources/input";
    static final String OUTPUT_DIR = "normalised";
    static final String INPUT_ROOT = "input/";
    static final String USER_DIR = "user.dir";
    static final String FILE_EXT_JAVA = ".java";
    static final String FILE_EXT_TXT = ".txt";
    static final String LINE_SEPARATOR = "line.separator";
    static final String ABSTRACT_METHOD_MODIFIER = "abstract";

    public static void main(String[] args) throws IOException {
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Normaliser.class).resolve(INPUT_DIR));
        String outputMainDirectory = System.getProperty(USER_DIR) + File.separator + OUTPUT_DIR;
        File outputDirectory = new File(outputMainDirectory);
        if (outputDirectory.exists()) {
            FileUtils.deleteDirectory(new File(outputMainDirectory));
        }
        try (Stream<Path> walk = Files.walk(sourceRoot.getRoot())) {
            walk.filter(path -> path.toString().endsWith(FILE_EXT_JAVA))
                .forEach(path -> {
                    String fileNameWithOutExt = FilenameUtils.removeExtension(path.getFileName().toString());
                    String outputFilePath = path.toString().split(INPUT_ROOT)[1].replace(path.getFileName().toString(), "");
                    String outputFileName = fileNameWithOutExt + FILE_EXT_TXT;
                    String outputRoot = outputMainDirectory + File.separator + outputFilePath;
                    try {
                        CompilationUnit cu = sourceRoot.parse("", path.toString());
                        File outputFile = new File(outputRoot + outputFileName);
                        FileManager.instance().createProjectOutputDirectory(new File(outputRoot));
                        try {
                            FileManager.instance().createOutputFile(outputFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cu.findAll(MethodDeclaration.class).forEach(m -> {
                            try {
                                FW.instance().writeMethodStringToFile(outputFile.toString(), createMethodString(m));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        System.out.println("Javaparser unable to parse this file, skipping");
                        return;
                    }
            });
        }
    }

    private static String createMethodString (MethodDeclaration m) {
        StringBuilder methodString = new StringBuilder();
        methodString.append(m.getDeclarationAsString());
        Optional<BlockStmt> block = m.getBody();
        if (!isMethodAbstract(m)) {
            methodString.append(" { ");
            if (block.isPresent()) {
                NodeList<Statement> statements = block.get().getStatements();
                for (Statement stmt: statements) {
                    stmt.removeComment();
                    methodString.append(stmt.toString().trim().replaceAll("  +", ""));
                }
                methodString.append(" }");
            }
        } else {
            methodString.append(";");
        }
        methodString = new StringBuilder(methodString.toString().replaceAll(System.getProperty(LINE_SEPARATOR), " "));
        return formatMethodString(methodString);
    }

    private static boolean isMethodAbstract (MethodDeclaration m) {
        return m.getDeclarationAsString().contains(ABSTRACT_METHOD_MODIFIER);
    }

    /*
    Certainly not the most efficient code. Please forgive me Donald Knuth
    Adds a space between any Java operator and separator, then removes extra spaces
     */
    private static String formatMethodString(StringBuilder methodString) {
        return methodString
                .toString()
                .replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ")
                .replaceAll(";", " ; ")
                .replaceAll("\\[", " [ ")
                .replaceAll("]", " ] ")
                .replaceAll("\\{", " { ")
                .replaceAll("}", " } ")
                .replaceAll("\\.", " . ")
                .replaceAll(",", " , ")
                .replaceAll("(?<!\\+)(\\+)(?![+,=])", " + ")
                .replaceAll("(?<!\\+)(\\+{2})", " ++ ")
                .replaceAll("(?<!-)(-{2})", " -- ")
                .replaceAll("(!)(?!=)", " ! ")
                .replaceAll("~", " ~ ")
                .replaceAll("  +", " ")
                .trim();
    }
}