import java.io.*;

public class CSVwriter
{

    private String separatorDefault = ";";
    private String fileName;
    private int separatorCount;

    CSVwriter(String fileName) throws IOException {
        this.fileName = fileName;

        File tmpDir = new File(fileName);
        boolean exists = tmpDir.exists();

        if (!exists) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.close();
        }

        this.separatorCount = getMaxSeparators();
    }

    public String getFileName() {
        return fileName;
    }

    private void updateSeparatorCount() throws IOException {
        this.separatorCount = getMaxSeparators();
    }

    private void updateSeparatorCount(int quantity) throws IOException {
        this.separatorCount = quantity;
    }

    public void addElementToFile(String className, String[] fieldDescriptions, String[] fieldValues) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

        if (!areArrayContentsSameQuantity(fieldDescriptions, fieldValues)) {

            String warning;
            warning = String.format("Class >%1$s< is defined by %2$d fields: %3$s \n" +
                            "Number of provided values (%4$d: %5$s) doesn't match these fields. Adding new element was interrupted.",
                    className, arrayWithoutEmptyFields(fieldDescriptions).length, fieldsToString(arrayWithoutEmptyFields(fieldDescriptions)),
                    arrayWithoutEmptyFields(fieldValues).length, fieldsToString(arrayWithoutEmptyFields(fieldValues)));
            System.out.println(warning);

            return;
        }

        if (doesClassExist(className)) {    //jeśli klasa istnieje

            BufferedReader reader;

            String upToDivisionFileName = "before.csv";
            String postDivisionFileName = "after.csv";
            String mergedFileName = "merge.csv";

            int divideOn = getLastClassEntryLineNumber(className);

            splitCSV(divideOn, upToDivisionFileName, postDivisionFileName);

            addEmptyLine(upToDivisionFileName);
            enterFromArray(upToDivisionFileName, fieldValues);

            mergeCSV(upToDivisionFileName, postDivisionFileName, mergedFileName);

            writer = new BufferedWriter(new FileWriter(fileName));
            reader = new BufferedReader(new FileReader(mergedFileName));

            copyContents(reader, writer, "", false);

            deleteFile(upToDivisionFileName);
            deleteFile(postDivisionFileName);
            deleteFile(mergedFileName);

            System.out.println(String.format("Added new row: (%1$s) to database of class >%2$s< (%3$s)", fieldsToString(fieldValues), className, getClassDescriptionsText(className)));

            //todo: może dodać sprawdzenie czy dany wpis już istnieje?

        } else {    //jeśli klasy nie ma w bazie

            addNewClassWithDescriptions(className, fieldDescriptions);
            addEmptyLine();
            addElementToFile(className, fieldValues);

        }

        fillMissingSeparators();
    }

    public void addElementToFile(String className, String[] fieldValues) throws IOException {

        if (doesClassExist(className)) {
            String[] descriptionsArr = getClassDescriptionsArray(className);
            addElementToFile(className, descriptionsArr, fieldValues);
            //System.out.println(String.format("Added new row: (%1$s) to database of class >%2$s<", fieldsToString(fieldValues), className));
        } else {
            System.out.println("The class: >" + className + "< doesn't exist in database. Element addition cannot be performed without defining Description-fields beforehand.");
        }

    }

    private String fieldsToString(String[] fields) {
        String outputStr = "";
        String[] fieldsPacked = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldsPacked[i] = "<" + fields[i] + ">";
        }

        for (int i = 0; i < fieldsPacked.length; i++) {
            outputStr += fieldsPacked[i];
            if (i < fieldsPacked.length - 1) {
                outputStr += " | ";
            }
        }

        return outputStr;
    }

    public String getClassDescriptionsText(String className) throws IOException {
        return fieldsToString(getClassDescriptionsArray(className));
    }


    public boolean areFieldDescriptionsCorrect(String className, String[] checkedDescriptions) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        boolean sameDescriptions = true;

        if (doesClassExist(className)) {
            while ((line = reader.readLine()) != null) {
                if (line.equals(getClassLineString(className))) {
                    break;
                }
            }
        } else {
            System.out.println("This class: >" + className + "< doesn't exist in database. Adding new class was interrupted.");
            return false;
        }

        line = reader.readLine();   //nastepna linia po znalezieniu linii klasy, wiec tu są deskrypcje
        String[] classDescriptions = getFieldValsFromLine(line);
        String[][] differenceArr = new String[classDescriptions.length][2];

        if (checkedDescriptions.length > classDescriptions.length) {
            System.out.println("Entered more fields than database element is defined by.");
            System.out.println("Excess fields are: ");  //todo: dodać opis nadmiarowych pól
            return false;
        }

        int checkedFieldsCounter;
        for (checkedFieldsCounter = 0; checkedFieldsCounter < checkedDescriptions.length; checkedFieldsCounter++) {
            if (!classDescriptions[checkedFieldsCounter].equals(checkedDescriptions[checkedFieldsCounter])) {     //jeśli element tablicy się różni
                sameDescriptions = false;
                differenceArr[checkedFieldsCounter][0] = checkedDescriptions[checkedFieldsCounter];
                differenceArr[checkedFieldsCounter][1] = classDescriptions[checkedFieldsCounter];
            }
        }

        boolean theRestFieldsAreEmpty = true;
        for (int i = checkedFieldsCounter; i < classDescriptions.length; i++) {
            if (classDescriptions[i] != null && !classDescriptions[i].equals("")) {
                theRestFieldsAreEmpty = false;
            }
        }

        if (!theRestFieldsAreEmpty) {
            System.out.println("Entered less fields than database element is defined by.");
            System.out.println("Missing fields are: "); //todo dodać opis brakujacych pól
            return false;
        }

        if (sameDescriptions) {
            System.out.println("Entered fields match the class description in database.");
        } else {
            System.out.println("Entered fields are different than the ones in database:");
            //todo tutaj wylistować różnice w polach  wpisano -> powinno być
            //todo pododawać takie opisy dla różnych wariantów
        }
        return sameDescriptions;
    }

    private boolean areArrayContentsSameQuantity(String[] arr1, String[] arr2) {
        return arrayWithoutEmptyFields(arr1).length == arrayWithoutEmptyFields(arr2).length;
    }

    private String[] arrayWithoutEmptyFields(String[] arr) {
        String[] outArrayExtended = new String[arr.length];
        String[] outputArray;
        int nonEmptyFieldCounter = 0;
        for (int i = 0; i < arr.length; i++) {
            if (!(arr[i].equals("") || arr[i] == null)) {
                outArrayExtended[nonEmptyFieldCounter] = arr[i];
                nonEmptyFieldCounter++;
            }
        }

        outputArray = new String[nonEmptyFieldCounter];
        for (int i = 0; i < nonEmptyFieldCounter; i++) {
            outputArray[i] = outArrayExtended[i];
        }

        return outputArray;
    }

    public int getMaxSeparators() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        int separatorsInLineQty;
        int maxSeparatorQty = 0;

        while ((line = reader.readLine()) != null) {

            separatorsInLineQty = countSeparatorsInLine(line);
            if (maxSeparatorQty < separatorsInLineQty) {
                maxSeparatorQty = separatorsInLineQty;
            }
        }

        return maxSeparatorQty;
    }

    private void addClassLine(String className) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String firstLineCheck = reader.readLine();

        if (!(firstLineCheck == null || firstLineCheck.equals(""))) {
            addEmptyLine();
        }

        writer.append(getClassLineString(className));
        writer.close();

    }

    public void addNewClassWithDescriptions(String className, String[] fieldDescriptions) throws IOException {

        if (doesClassExist(className)) {
            System.out.println(String.format("This class >%s< already exists in database.", className));
            return;
        }

        addClassLine(className);

        addEmptyLine();
        enterFromArray(fieldDescriptions);
        if ((fieldDescriptions.length - 1) > separatorCount) {
            updateSeparatorCount(fieldDescriptions.length - 1);
        }
        addSeparatorLine();

        fillMissingSeparators();

        System.out.println(String.format("Added new class >%1$s<: (%2$s).",className, getClassDescriptionsText(className)));

    }

    private void enterFromArray(String[] strArray) throws IOException {
        enterFromArray(fileName, strArray);
    }

    private void enterFromArray(String destinationFileName, String[] strArray) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFileName, true));

        for (int i = 0; i < strArray.length; i++) {

            if (strArray[i].contains(separatorDefault)) {     //jeżeli pole = ;
                writer.append("\"").append(strArray[i]).append("\"");
            } else if (strArray[i].contains("\"")) {  //jeżeli pole zawiera znak cudzysłowu "
                writer.append("\"").append(strArray[i].replace("\"", "\"\"")).append("\""); //znak cudzysłowu sie duplikuje: " -> ""
            } else {
                writer.append(strArray[i]);
            }

            if (i < strArray.length - 1) {
                writer.append(separatorDefault);
                //System.out.println(separatorDefault);
            }
        }
        writer.close();
    }

    private void addEmptyLine() throws IOException {
        addEmptyLine(fileName);
    }

    private void addSeparatorLine() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        addEmptyLine(fileName);
        writer.append(getSeparatorsString());
        writer.close();
    }

    private void addEmptyLine(String destinationFileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFileName, true));
        writer.append("\n");
        writer.close();
    }

    private void fillMissingSeparators() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter("temp.csv"));
        int separatorCount = getMaxSeparators();

        String line;
        String missingSeparators;
        int missingSepQty;
        int lineCounter = 0;

        while ((line = reader.readLine()) != null) {
            if (lineCounter > 0) {
                writer.append("\n");
            }

            missingSeparators = "";
            if (countSeparatorsInLine(line) < separatorCount) {
                missingSepQty = separatorCount - countSeparatorsInLine(line);
                for (int i = 0; i < missingSepQty; i++) {
                    missingSeparators += separatorDefault;
                }
                writer.append(line).append(missingSeparators);
            } else {
                writer.append(line);
            }
            lineCounter++;
        }

        writer.close();
        reader.close();

        reader = new BufferedReader(new FileReader("temp.csv"));
        writer = new BufferedWriter(new FileWriter(fileName));

        copyContents(reader, writer, "", false);

        deleteFile("temp.csv");
    }

    private void deleteFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

    private int countSeparatorsInLine(String line) {
        String left;
        String right;
        int separatorIndex;
        int separatorCount = 0;

        right = line;

        while (!right.equals("")) {
            separatorIndex = right.indexOf(separatorDefault);
            if (separatorIndex >= 0) {
                left = right.substring(0, separatorIndex);
                if (left.contains("\"")) {   //ten separator musi być "cytowany"
                    right = right.substring(separatorIndex + 2); //+2, bo standardowo +1, ale omijamy jeszcze zamykający cudzysłów
                } else {    //prawdziwy separator
                    right = right.substring(separatorIndex + 1);
                    separatorCount++;
                }
            } else {
                break;
            }
        }

        return separatorCount;
    }

    private String[] getFieldValsFromLine(String line) {
        int fieldNumbers = countSeparatorsInLine(line) + 1;
        String[] outputArray = new String[fieldNumbers];

        String left;
        String right;
        String quoted = "";
        boolean wasQuoted = false;
        int separatorIndex;
        int fieldCounter = 0;

        right = line;

        while (!right.equals("")) {
            separatorIndex = right.indexOf(separatorDefault);
            if (separatorIndex >= 0) {
                left = right.substring(0, separatorIndex);
                if (left.contains("\"")) {   //ten separator musi być "cytowany"
                    right = right.substring(separatorIndex + 2); //+2, bo standardowo +1, ale omijamy jeszcze zamykający cudzysłów
                    quoted = left + separatorDefault + "\"";
                    wasQuoted = true;
                } else {    //prawdziwy separator
                    right = right.substring(separatorIndex + 1);
                    if (wasQuoted) {
                        outputArray[fieldCounter] = quoted;
                        wasQuoted = false;
                        quoted = "";
                    } else {
                        outputArray[fieldCounter] = left;
                    }
                    fieldCounter++;
                }
            } else {
                outputArray[fieldCounter] = right;
                break;
            }
        }

        if (right.equals("")) {
            outputArray[fieldCounter] = "";
        }

        return outputArray;
    }

    public String[] getClassDescriptionsArray(String className) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        if (doesClassExist(className)) {
            while ((line = reader.readLine()) != null) {
                if (line.equals(getClassLineString(className))) {
                    break;
                }
            }
        } else {
            return null;
        }

        line = reader.readLine();   //nastepna linia po znalezieniu linii klasy, wiec tu są deskrypcje

        return getFieldValsFromLine(line);
    }

    public boolean doesClassExist(String className) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.equals(getClassLineString(className))) {
                return true;
            }
        }

        return false;
    }

    private String getSeparatorsString() {
        String separators = "";
        for (int i = 0; i < this.separatorCount; i++) {
            separators += separatorDefault;
        }
        return separators;
    }

    private String getClassLineString(String className)
    {
        return className + ":" + getSeparatorsString();
    }

    private int getLastClassEntryLineNumber(String className) throws IOException {
        int lineNumber = 0;
        String currentLine;
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        while ((currentLine = reader.readLine()) != null) {
            lineNumber++;
            if (currentLine.equals(getClassLineString(className))) {
                break;
            }
        }

        while ((currentLine = reader.readLine()) != null) {
            lineNumber++;
            if (currentLine.equals(getSeparatorsString())) {
                break;
            }
        }

        return lineNumber - 1;
    }

    private void splitCSV(int dividingLineNumber, String beforeFileName, String afterFileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        BufferedWriter beforeWriter = new BufferedWriter(new FileWriter(beforeFileName));
        BufferedWriter afterWriter = new BufferedWriter(new FileWriter(afterFileName));

        String currentLine;
        int currentLineNumber;

        BufferedWriter writer = beforeWriter;
        boolean justSwitchedFile = false;

        currentLine = reader.readLine();
        currentLineNumber = 1;

        while (currentLine != null) {
            writer.append(currentLine);

            if (currentLineNumber == dividingLineNumber) {
                writer = afterWriter;
                justSwitchedFile = true;
            }

            currentLine = reader.readLine();
            currentLineNumber++;

            if (!justSwitchedFile) {
                if (currentLine != null) {
                    writer.append("\n");
                }
            } else {
                justSwitchedFile = false;
            }

        }

        afterWriter.close();
        beforeWriter.close();

    }

    public void copyContents(BufferedReader fromFile, BufferedWriter toFile, String additionText, boolean append) throws IOException {

        String currentLine = fromFile.readLine();
        if (append) {
            toFile.append("\n");
        }

        while (currentLine != null) {
            toFile.append(currentLine).append(additionText);

            currentLine = fromFile.readLine();
            if (currentLine != null) {
                toFile.append("\n");
            }
        }

        toFile.close();
        fromFile.close();

    }

    public void mergeCSV(String firstFileName, String secondFileName, String mergedFileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFileName));

        BufferedReader reader = new BufferedReader(new FileReader(firstFileName));

        copyContents(reader, writer, "", false);

        reader = new BufferedReader(new FileReader(secondFileName));
        writer = new BufferedWriter(new FileWriter(mergedFileName, true));

        copyContents(reader, writer, "", true);
    }

}
