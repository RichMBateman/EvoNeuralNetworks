package com.bateman.richard.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Class with helper functions for text i/o.
 * http://www.javapractices.com/topic/TopicAction.do?Id=42
 */
public class TextFileHelper {
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    /**
     * Reads all lines in the specified file, and returns them as an ArrayList.
     * @param filePath
     * @return
     */
    public static ArrayList<String> readFile(String filePath) throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();
        Path p = Paths.get(filePath);
        try(BufferedReader reader = Files.newBufferedReader(p, ENCODING)) {
            String line;
            while((line = reader.readLine()) != null) {
                arrayList.add(line);
            }
        }
        return arrayList;
    }

    /**
     * Writes all the supplies lines to the specified file.
     * @param filePath
     * @param lines
     * @throws IOException
     */
    public static void writeFile(String filePath, ArrayList<String> lines) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
            for(String line : lines){
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Writes the string content to the supplied file.
     * @param filePath
     * @param content
     * @throws IOException
     */
    public static void writeFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
                writer.write(content);
        }
    }
}
