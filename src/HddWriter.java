import java.io.*;
import java.util.Scanner;

public class HddWriter {

    private String fileName = "BookRepository.txt";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        StringBuilder stringToReturn = new StringBuilder();
        try {
            Scanner myReader = new Scanner(new File(fileName));
            while (myReader.hasNextLine()) {
                stringToReturn.append(myReader.nextLine()).append("\n");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
        }
        return stringToReturn.toString();
    }

    public void clearTheFile(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addElementToFile(String[] fieldDescriptions, String[] fieldValues){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append("New Book:");
            for (int i = 0; i < fieldDescriptions.length; i++) {
                writer.append('\n');
                writer.append(fieldDescriptions[i]).append(":");
                writer.append('\n');
                writer.append(fieldValues[i]);
                writer.append('\n');
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while adding content to the file.");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            System.out.println("Null Pointer Exception Occured. Try checking if number of field values is equal to the number of field descriptions");
        }
    }

    public void addLineToFile(String contentToAdd) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(contentToAdd);
            writer.append(" \n");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while adding content to the file.");
        }
    }

}
