package org.am;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Utils {

    /*
     * createFile(String filename, String content, File directory)
     *
     * @param filename - Name of the file we want to create
     * @param content - Content of the file we want to create
     * @param directory - Place of the file we want to create
     *
     * This method creates a file in the directory specified with the content specified.
     *
     */
    public static void createFile(String filename, String[] content, File directory) throws IOException {
        String path = directory.getPath() + "\\" + filename;
        File newFile = new File(path);
        newFile.createNewFile();
        PrintWriter fileOutput = new PrintWriter(path);
        for (String line: content){
            fileOutput.println(line);
        }
        fileOutput.close();
    }

    /*
     * isInDir(String filename, File directory)
     *
     * @param filename - Name of the file we are looking for
     * @param directory - Directory we are looking in
     *
     * This method returns true is the file can be found in the specified directory
     *
     */
    public static boolean isInDir(String filename, File directory){
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    /*
     * getFromDir(String filename, File directory)
     *
     * @param filename - Name of the file we want
     * @param directory - Directory we want it from
     *
     * This method returns the file from the specified directory
     *
     */
    public static File getFromDir(String filename, File directory){
        File searchedFile = null;
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().equals(filename)) {
                searchedFile = file;
            }
        }
        return searchedFile;
    }

    /*
     * getFileContent(File file)
     *
     * @param file - File we want the content from
     *
     * This method returns the content of the file in a string
     *
     */
    public static String getFileContent(File file) throws IOException{
        String content = "";

        String currentLine;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext() && null != (currentLine = scanner.nextLine())) {
            content += currentLine +"\r\n";

        }


        scanner.close();

        return content;
    }

}
