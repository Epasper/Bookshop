/**
 * LEGENDA:
 * + BAZA DANYCH: domyslny plik .csv, tworzony w konstruktorze klasy CSVwriter
 *
 *
 * + SEPARATOR: znak specjalny formatu .csv, który informuje, że tekst znajdujący się za separatoren
 * należy już do nowej kolumny danych (w arkuszu kalkulacyjnym).
 * ---UWAGA: format .csv wymaga, aby w każdym wierszu teksu znajdowała sie
 * ta sama ilość separatorów
 *
 *
 * + LINIA KLASY: wiersz bazy danych, który informuje nas, że kolejne wersy pod nim
 * (aż do pierwszej następnej pustej linii) zawierają informacje dotyczące tej klasy.
 * Linia klasy składa się z trzech składowych:
 * - Nazwy klasy (np. "Book")
 * - dwukropka
 * - odpowiedniej ilości separatorów, np.:
 * -----------
 * Book:;;
 * -----------
 * W praktyce oznacza to, że po klasie >Book< spodziewamy się, że tworzy ona
 * obiekty opisane informacjami, znajdującymi się w trzech kolumnach danych, np. <Autor;Tytuł;Cena>
 * (oddzielonych dwoma separatorami)
 * --- UWAGA: powyższe stwierdzenie jest prawdziwe, tylko jeśli w bazie danych mamy do czynienia
 * z wpisami jednej klasy. Jeśli klas jest więcej i mają one różne ilości kolumn danych (np.
 * poza wymienioną klasą "Book" mamy też klasę "User", opisaną przez <Name;Password;Email;Address>),
 * to ilość separatorów zawsze będzie determinować klasa wymagająca więcej danych do opisu.
 * W drugim przypadku, pomimo że klasa "Book" wymaga 3 kolumn danych klasa, to i tak jej
 * LINIA KLASY "Book" będzie miała aż 3 separatory (a nie 2, jak oryginalnie), ponieważ
 * klasa "User" wymaga 4 kolumn danych.
 *
 *
 * + POLA OPISOWE: każda klasa zapisana w bazie danych, oprócz nazwy, definiowana jest przez jej pola opisowe,
 * które zawierają nazwy kolejnych kolumn danych tej klasy, np:
 * -------------------
 * Book:;;
 * Tytuł;Autor;Rok
 * ;;
 * -------------------
 * Oznacza to, że nowe elementy tej klasy, dodawane do bazy danych, powinny zawierać pola/informacje pasujące
 * do pól opisowych, np. String[]{"Book","Potop",Sienkiewicz","1886"}:
 * ----------------------
 * Book:;;
 * Tytuł;Autor;Rok
 * Potop;Sienkiewicz;1886
 * ;;
 * ----------------------
 */


import java.io.*;
import java.util.Scanner;

public class CSVwriter
{

    //TODO: sprawdzic co sie stanie, jesli klasa istnieje, a ktos chce jej wpisac nowy element o blednych deskrypcjach
    // - teraz nic sie nie dzieje - sprawdzic czy deskrypcje sie zgadzają (co do tresci, nie ilosci)

    // - może jeśli deskrypcje się zgadzają, ale są w innej kolejności, to je przesortować?
    //TODO: dodać metodę mogącą zupdtować/zmienić deskryptory klasy

    // - jest podstawa, dodać rozszerzoną funkcjonalność
    //TODO: dodać metodę usuwającą wpisy?
    // - jeśli zostanie dodana, trzeba wprowadzić funkcjonalność updatowania separatorów poprzez ich odejmowanie

    private String separatorDefault = ";";
    private String fileName;
    private int separatorCount;

    CSVwriter(String fileName) throws IOException
    {
        this.fileName = fileName;

        File tmpDir = new File(fileName);
        boolean exists = tmpDir.exists();

        if (!exists)
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.close();
        }

        this.separatorCount = getMaxSeparators();
    }

    public String getFileName()
    {
        return fileName;
    }

    /**
     * Metoda zliczająca ilość separatorów w linii tekstu
     *
     * @param line - wiersz z pliku .csv
     * @return - ilość separatorów (w tym przypadku średników),
     * które oddzielają od siebie kolejne kolumny danych. Np. jeżeli mamy w linii
     * dwa separatory, oznacza to że mamy do czynienia z trzema kolumnami danych
     */
    private int countSeparatorsInLine(String line)
    {
        String left;
        String right;
        int separatorIndex;
        int separatorCount = 0;

        right = line;

        while (!right.equals(""))
        {
            separatorIndex = right.indexOf(separatorDefault);
            if (separatorIndex >= 0)
            {
                left = right.substring(0, separatorIndex);
                if (left.contains("\""))
                {   //ten separator musi być "cytowany"
                    right = right.substring(separatorIndex + 2); //+2, bo standardowo +1, ale omijamy jeszcze zamykający cudzysłów
                }
                else
                {    //prawdziwy separator
                    right = right.substring(separatorIndex + 1);
                    separatorCount++;
                }
            }
            else
            {
                break;
            }
        }

        return separatorCount;
    }

    /**
     * Metoda skanuje wszystkie linie domyślnego pliku .csv
     *
     * @return - zwraca maksymalną ilość separatorów spośród wszystkich linii
     * w pliku (w praktyce oznacza to z iloma kolumnami danych mamy do czynienia,
     * np. 3 separatory => maksymalnie 4 kolumny danych)
     */
    public int getMaxSeparators() throws IOException
    {
        Scanner myReader = new Scanner(new File(fileName));

        String currentLine;

        int separatorsInLineQty;
        int maxSeparatorQty = 0;

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();

            separatorsInLineQty = countSeparatorsInLine(currentLine);
            if (maxSeparatorQty < separatorsInLineQty)
            {
                maxSeparatorQty = separatorsInLineQty;
            }
        }

        return maxSeparatorQty;
    }

    /**
     * Metoda - setter, ustawiająca licznik separatorów bazy danych na podstawie
     * maksymalnej znalezionej ilości separatorów w wierszach w pliku
     */
    private void updateSeparatorCount() throws IOException
    {
        this.separatorCount = getMaxSeparators();
    }

    /**
     * (przeciążenie)
     * Metoda - setter, ustawiająca licznik separatorów klasy na podstawie
     * podanej wartości
     *
     * @param quantity - docelowa ilość separatorów w klasie
     */
    private void updateSeparatorCount(int quantity) throws IOException
    {
        this.separatorCount = quantity;
    }

    /**
     * Metoda skanuje wszystkie wiersze bazy danych, a następnie jeśli w którymś z nich
     * jest mniej separatorów, niż w dowolnym innym wierszu, to "wybrakowany" wers zostanie
     * uzupełniony o brakujące separatory tak, aby ich liczba w każdej linijce była równa
     *
     * Metoda ta jest wykorzystywana głównie wtedy, gdy do bazy danych, w której zapisane
     * są już obiekty jakiejś klasy, dodamy nową klasę opisaną większą ilością kolumn danych,
     * a więc wymagającą większej ilości separatorów, aby stworzyć wiersz.
     */
    private void fillMissingSeparators() throws IOException
    {

        StringBuilder databaseModification = new StringBuilder();

        String currentLine;
        String missingSeparators;
        int missingSepQty;
        int separatorsInLineQty;
        int separatorCount = getMaxSeparators();

        Scanner myReader = new Scanner(new File(fileName));

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            missingSeparators = "";
            separatorsInLineQty = countSeparatorsInLine(currentLine);
            if (separatorsInLineQty < separatorCount)
            {
                missingSepQty = separatorCount - separatorsInLineQty;
                for (int i = 0; i < missingSepQty; i++)
                {
                    missingSeparators += separatorDefault;
                }
                databaseModification.append(currentLine).append(missingSeparators);
            }
            else
            {
                databaseModification.append(currentLine);
            }
            if (myReader.hasNextLine())
            {
                databaseModification.append("\n");
            }
        }

        clearCSV();
        insertString(databaseModification.toString());
    }

    /**
     * Metoda podstawowa do wpisywania/dodawania tekstu do pliku.
     * Dodaje po prostu tekst na koniec pliku, bez tworzenia nowej linii.
     *
     * @param appendix - tekst który chcemy dodać na koniec pliku
     */
    private void insertString(String appendix) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append(appendix);
        writer.close();
    }

    /**
     * Metoda dodająca nową pustą linię do konkretnego pliku (tak jakbyśmy w edytorze tekstu
     * wcisnęli 'enter'
     *
     * @param destinationFileName - nazwa pliku, do którego chcemy dodać nową linię
     */
    private void addEmptyLine(String destinationFileName) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFileName, true));
        writer.append("\n");
        writer.close();
    }

    /**
     * (przeciążenie)
     * j.w., tyle że doda nową linię do domyślnej bazy danych
     */
    private void addEmptyLine() throws IOException
    {
        addEmptyLine(fileName);
    }

    /**
     * Metoda
     *
     * @return - zwraca String, będący ciągiem tylu separatorów,
     * ile znajduje się w polu 'separatorCount' klasy,
     * np. dla 'separatorCount' równe 3 ===> ";;;"
     * W praktyce, gdybyśmy otworzyli plik bazy danych w Excelu,
     * to taka linia składająca się z samych średników byłaby po prostu
     * pustym wierszem arkusza kalkulacyjnego
     */
    private String getSeparatorsString()
    {
        String separators = "";
        for (int i = 0; i < this.separatorCount; i++)
        {
            separators += separatorDefault;
        }
        return separators;
    }

    /**
     * Metoda, która najpierw dodaje pustą linię do domyślnego pliku .csv,
     * a następnie uzupełnia ją separatorami, pomiędzy którymi nic nie ma (np. ;;;),
     * w ilości odpowiadającej wartości wpisanej w pole 'separatorCount' klasy.
     * W praktyce (w pliku .csv) jest to ekwiwalent pustego wiersza excelowskiego.
     */
    private void addSeparatorLine() throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        addEmptyLine(fileName);
        writer.append(getSeparatorsString());
        writer.close();
    }

    /**
     * Metoda dodająca nową LINIĘ KLASY do bazy danych (patrz LEGENDA).
     *
     * @param className - nazwa klasy obiektu dodawanego do bazy danych
     */
    private void addClassLine(String className) throws IOException
    {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String firstLineCheck = reader.readLine();

        if (!(firstLineCheck == null || firstLineCheck.equals("")))
        {
            addEmptyLine();
        }

        writer.append(getClassLineString(className));
        writer.close();

    }

    /**
     * Metoda
     *
     * @return - zwraca String, będący treścią bazy danych,
     * włącznie z podziałem na linie
     */
    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();
        String line;

        try
        {
            Scanner myReader = new Scanner(new File(fileName));

            while (myReader.hasNextLine())
            {
                line = myReader.nextLine();
                outputString.append(line);
                if (myReader.hasNextLine())
                {
                    outputString.append("\n");
                }
            }
            myReader.close();
        } catch (FileNotFoundException e)
        {
            System.out.println("An error occurred while reading the file.");
        }
        return outputString.toString();
    }

    /**
     * Metoda zwracająca treść bazy danych, ale tylko do pewnego wiersza włącznie.
     *
     * @param divisionLineNumber - numer wiersza bazy danych,
     *                           DO KTÓREGO WŁĄCZNIE będzie czytany ów plik
     * @return - zwraca String, będący treścią bazy danych aż po podaną linię włącznie.
     */
    private String getDatabaseContentUpToLine(int divisionLineNumber) throws FileNotFoundException
    {
        StringBuilder outputString = new StringBuilder();
        String currentLine;

        int currentLineNumber = 0;

        Scanner myReader = new Scanner(new File(fileName));

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            currentLineNumber++;

            if (currentLineNumber <= divisionLineNumber)
            {
                outputString.append(currentLine);
                if (myReader.hasNextLine() && currentLineNumber != divisionLineNumber)
                {
                    outputString.append("\n");
                }
            }
            else
            {
                break;
            }
        }

        return outputString.toString();
    }

    /**
     * Metoda zwracająca treść bazy danych, ale tylko po pewnym wierszu.
     *
     * @param divisionLineNumber - numer wiersza pliku .csv,
     *                           PO KTÓRYM będzie czytany ów plik
     * @return - zwraca String, będący treścią domyślnego pliku .csv po podanej linii,
     * z wyłączeniem treści tejże linii
     */
    private String getDatabaseContentAfterLine(int divisionLineNumber) throws FileNotFoundException
    {
        StringBuilder outputString = new StringBuilder();
        String currentLine;
        int currentLineNumber = 0;

        Scanner myReader = new Scanner(new File(fileName));

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            currentLineNumber++;

            if (currentLineNumber > divisionLineNumber)
            {
                outputString.append(currentLine);
                if (myReader.hasNextLine())
                {
                    outputString.append("\n");
                }
            }
        }

        return outputString.toString();
    }

    /**
     * Metoda zwracająca treść bazy danych, zawartą pomiędzy podanymi numerami wierszy
     * (wiersz początkowy włącznie)
     *
     * @param startLineNumber
     * @param endLineNumber
     * @return
     */
    private String getDatabaseContentBetweenLines(int startLineNumber, int endLineNumber) throws FileNotFoundException
    {
        StringBuilder outputString = new StringBuilder();
        String currentLine;
        int currentLineNumber = 0;

        Scanner myReader = new Scanner(new File(fileName));

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            currentLineNumber++;

            if (currentLineNumber >= startLineNumber)
            {
                if (currentLineNumber >= endLineNumber)
                {
                    break;
                }

                outputString.append(currentLine);
                if (myReader.hasNextLine() && currentLineNumber < endLineNumber - 1)
                {
                    outputString.append("\n");
                }
            }
        }

        return outputString.toString();
    }

    /**
     * Metoda dzieląca treść bazy danych na dwa człony do- (włącznie), i od konkretnego wiersza
     *
     * @param divisionLineNumber - numer wiersza, po którym dochodzi do podziału pliku
     * @return - zwraca dwuelementową tablicę (String[2]).
     * Pierwszy element tej tablicy (outputArr[0]) jest treścią pliku .csv od początku, aż do podanego wiersza włącznie.
     * Drugi element (outputArr[1]) zawiera treść pliku po podanym wierszu
     */
    public String[] splitToStringsOnLine(int divisionLineNumber) throws FileNotFoundException
    {
        String[] outputArr = new String[2];
        outputArr[0] = getDatabaseContentUpToLine(divisionLineNumber);
        outputArr[1] = getDatabaseContentAfterLine(divisionLineNumber);

        return outputArr;
    }

    /**
     * Metoda do uzyskania elementów danej klasy w postaći Stringa.
     *
     * @param className - nazwa klasy, której elementy chcemy pozyska
     * @return - String zawierający wiersze danej klasy, opisujące jej elementy (bez linii klasy,
     * bez wiersza pól opisowych i bez pustego wiersza końcowego).
     */
    public String getElementsOfClass(String className) throws IOException
    {
        int classContentBeginsLineNumber;
        int classDivisionLineNumber;

        classContentBeginsLineNumber = getClassDefinitionLineNumber(className) + 2;
        classDivisionLineNumber = getClassLastEntryLineNumber(className) + 1;

        return getDatabaseContentBetweenLines(classContentBeginsLineNumber, classDivisionLineNumber);
    }

    /**
     * (przeciążenie)
     * Metoda do uzyskania elementów danej klasy w postaći dwuwymiarowej tablicy Stringów.
     *
     * @param className - nazwa klasy, której elementy chcemy pozyska
     * @param asString - czy chcemy, żeby fukcja zwracała String? Jesli nie, zwróci tablicę.
     * @return - Dwuwymiarowa tablica, odwzorowująca strukturę kolumn i wierszy
     * arkusza kalkulacyjnego. Wiersze to kolejne elementy danej klasy,
     * a kolumny zawierają poszczególe informacje nt. tych elementów
     */
    public String[][] getElementsOfClass(String className, boolean asString) throws IOException
    {
        String classContent;
        String currentLine = "";
        int elementsCount = 0;

        if (asString)
        {
            getElementsOfClass(className);
            return null;
        }

        int classContentBeginsLineNumber;
        int classDivisionLineNumber;

        classContentBeginsLineNumber = getClassDefinitionLineNumber(className) + 2;
        classDivisionLineNumber = getClassLastEntryLineNumber(className) + 1;

        classContent = getDatabaseContentBetweenLines(classContentBeginsLineNumber, classDivisionLineNumber);

        Scanner myReader = new Scanner(classContent);
        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            elementsCount++;
        }

        String[][] outputArray = new String[elementsCount][arrayWithoutEmptyFields(getFieldArrayFromLine(currentLine)).length];

        myReader = new Scanner(classContent);
        elementsCount = 0;

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            outputArray[elementsCount] = arrayWithoutEmptyFields(getFieldArrayFromLine(currentLine));
            elementsCount++;

        }

        return outputArray;

    }


   /* public boolean areFieldDescriptionsCorrect(String className, String[] checkedDescriptions) throws IOException {
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
        String[] classDescriptions = getFieldArrayFromLine(line);
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
    }*/

    /**
     * Metoda parsująca wiersz bazy danych na tablicę stringów. W formacie .csv niektóre znaki
     * (np. średnik i cudzysłów) pełnią specjalną rolę, i zmieniają zachowanie się arkusza excela.
     * Zadaniem tej metody jest przeczytanie wiersza dosłownie, następnie "odarcie" go z separatorów i innych
     * znaków specjalnych, po czym zwrócenie samych treści kolejnych pól/kolumn arkusza.
     *
     * @param line - String, będący dosłowną treścią wiersza pliku .csv
     * @return - tablica Stringów, której kolejne elementy zawierają treść kolejnych kolumn arkusza excelowskiego
     * np. wiersz:
     * ---------------------
     * LOTR;Tolkien;Fantasy;
     * ---------------------
     * zostanie sparsowany na String[]{"LOTR", "Tolkien", "Fantasy", ""}
     * ostatni element tablicy to pusty String, gdyż nic nie jest wpisane po ostatnim separatorze wiersza.
     */
    private String[] getFieldArrayFromLine(String line)
    {
        int fieldNumbers = countSeparatorsInLine(line) + 1;
        String[] outputArray = new String[fieldNumbers];

        String left;
        String right;
        String quoted = "";
        boolean wasQuoted = false;
        int separatorIndex;
        int fieldCounter = 0;

        right = line;

        while (!right.equals(""))
        {
            separatorIndex = right.indexOf(separatorDefault);
            if (separatorIndex >= 0)
            {
                left = right.substring(0, separatorIndex);
                if (left.contains("\""))
                {   //ten separator musi być "cytowany"
                    right = right.substring(separatorIndex + 2); //+2, bo standardowo +1, ale omijamy jeszcze zamykający cudzysłów
                    quoted = left + separatorDefault + "\"";
                    wasQuoted = true;
                }
                else
                {    //prawdziwy separator
                    right = right.substring(separatorIndex + 1);
                    if (wasQuoted)
                    {
                        outputArray[fieldCounter] = quoted;
                        wasQuoted = false;
                        quoted = "";
                    }
                    else
                    {
                        outputArray[fieldCounter] = left;
                    }
                    fieldCounter++;
                }
            }
            else
            {
                outputArray[fieldCounter] = right;
                break;
            }
        }

        if (right.equals(""))
        {
            outputArray[fieldCounter] = "";
        }

        return outputArray;
    }

    /**
     * Metoda, która przyjmuje tablicę Stringów i zwraca na tej bazie bliźniaczą tablicę, tyle że z "odciętymi"
     * z końca elementami, jeśli były one puste (null, bądź ""), np.
     * {"111", "222", "", "444", "", ""} ===> {"111", "222", "", "444"}
     * Przydatne jeśli w pliku .csv mamy klasę opisaną przez np. tylko 2 pola, ale również inną klasę,
     * opisaną już przez 4 pola. W tym przypadku wszystkie wiersze pliku .csv będą posiadały aż 3 separatory,
     * więc wiersz elementu "mniejszej" klasy będzie składał się z jej dwóch pól, a potem dwóch
     * pustych nadmiarowych kolumn.
     * Opisywana metoda służy do ignorowania tych pustych kolumn z końca. Np z wiersza:
     * -----------------
     * Diuna;Herbert;;
     * -----------------
     * uzyskamy parsując na tablicę Stringów:
     * {"Diuna", "Herbert", "", ""}
     * Wtedy opisywana metoda zwróci z takiej "nadmiarowej" tablicy:
     * {"Diuna", "Herbert"}
     *
     * @param arr
     * @return
     */
    private String[] arrayWithoutEmptyFields(String[] arr)
    {
        String[] outputArray;
        int emptyFieldCounter = 0;
        for (int i = arr.length - 1; i >= 0; i--)
        {
            if (arr[i].equals("") || arr[i] == null)
            {
                emptyFieldCounter++;
            }
            else
            {
                break;
            }
        }

        outputArray = new String[arr.length - emptyFieldCounter];
        for (int i = 0; i < outputArray.length; i++)
        {
            outputArray[i] = arr[i];
        }

        return outputArray;
    }

    /**
     * Metoda, która sprawdza czy dwie tablice mają tą samą ilość elementów,
     * z pominięciem elementów pustych ("" lub null) jeśli znajdują się na końcu tablic.
     *
     * @param arr1 - pierwsza tablica Stringów
     * @param arr2 - druga tablica Stringów
     * @return - prawda, jeśli ta sama ilość elementów z pominięciem "pustych końcówek"
     * fałsz, jeśli różna ilość elementów z pominięciem "pustych końcówek"
     */
    private boolean areArrayContentsSameQuantity(String[] arr1, String[] arr2)
    {
        return arrayWithoutEmptyFields(arr1).length == arrayWithoutEmptyFields(arr2).length;
    }

    /**
     * Metoda która pobiera tablicę Stringów, a potem kolejne jej elementy umieszcza do pliku .csv
     * w formacie czytelnym dla tego rozszerzenia, tzn. uwzględniającym wymagane cudzysłowy i separatory.
     * Metoda nie tworzy nowej linii i domyślnie dodaje nową treść na końcu pliku.
     *
     * @param destinationFileName - nazwa pliku, do którego chcemy wpisać dane z tablicy
     * @param strArray            - tablica Stringów, której kolejne elementy będą wpisywane w następujące po sobie
     *                            kolumny arkusza kalkulacyjnego.
     *                            Jeśli podamy tablicę String[]{"Herbert","Diuna","Sci-Fi"}, to zostanie dodany tekst:
     *                            Herbert;Diuna;Sci-Fi
     */
    private void enterFromArray(String destinationFileName, String[] strArray) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFileName, true));

        for (int i = 0; i < strArray.length; i++)
        {

            if (strArray[i].contains(separatorDefault))
            {     //jeżeli pole = ;
                writer.append("\"").append(strArray[i]).append("\"");
            }
            else if (strArray[i].contains("\""))
            {  //jeżeli pole zawiera znak cudzysłowu "
                writer.append("\"").append(strArray[i].replace("\"", "\"\"")).append("\""); //znak cudzysłowu sie duplikuje: " -> ""
            }
            else
            {
                writer.append(strArray[i]);
            }

            if (i < strArray.length - 1)
            {
                writer.append(separatorDefault);
                //System.out.println(separatorDefault);
            }
        }
        writer.close();
    }

    /**
     * (przeciążenie)
     * Metoda j.w., z tym że dodaje treść tablicy do domyślnego pliku bazy danych
     *
     * @param strArray
     */
    private void enterFromArray(String[] strArray) throws IOException
    {
        enterFromArray(fileName, strArray);
    }


    /**
     * Metoda dodająca do bazy danych nową klasę, tzn. linię tej klasy, pod tą linią wiersz z polami opisowymi
     *
     * @param className
     * @param fieldDescriptions
     */
    public void addNewClassWithDescriptions(String className, String[] fieldDescriptions) throws IOException
    {

        if (doesClassExist(className))
        {
            System.out.println(String.format("This class >%s< already exists in database.", className));
            return;
        }

        addClassLine(className);

        addEmptyLine();
        enterFromArray(fieldDescriptions);
        if ((fieldDescriptions.length - 1) > separatorCount)
        {
            updateSeparatorCount(fieldDescriptions.length - 1);
        }
        addSeparatorLine();

        fillMissingSeparators();

        System.out.println(String.format("Added new class >%1$s<: (%2$s).", className, getClassDescriptionsConsoleText(className)));

    }

    /**
     * Metoda dodająca do bazy danych nowy element danej klasy z danymi polami opisowymi.
     * Jeśli ta klasa jeszcze nie istnieje w bazie, to zostanie utworzona.
     * Jeśli istnieje, to nowy element zostanie dodany na koniec listy elementów tej klasy.
     * Aktualnie metoda po prostu dodaje nowe elementy, nie zważając na ich treść, tzn.
     * elementy mogą być duplikowane, lub nie zawierać informacji (""), bądź informacje mogą
     * nie pasować kontekstem do pól opisowych. Ważne tylko, aby ilość pól opisowych zgadzała się
     * z ilością pól informacyjnych.
     *
     * @param className         - nazwa klasy, do ktorej ma należeć nowy element.
     * @param fieldDescriptions - tablica Stringów, będących nazwami pól opisowych klasy
     * @param fieldValues       - tablica Stringów, będących informacjami charakteryzującymi element klasy
     *                          wg.szablonu zawartego w polach opisowych tej klasy
     */
    public void addElementToDatabase(String className, String[] fieldDescriptions, String[] fieldValues) throws IOException
    {

        if (!areArrayContentsSameQuantity(fieldDescriptions, fieldValues))
        {

            String warning;
            warning = String.format("Class >%1$s< is defined by %2$d fields: %3$s \n" +
                            "Number of provided values (%4$d: %5$s) doesn't match these fields. Adding new element was interrupted.",
                    className, arrayWithoutEmptyFields(fieldDescriptions).length, fieldsToConsoleString(arrayWithoutEmptyFields(fieldDescriptions)),
                    arrayWithoutEmptyFields(fieldValues).length, fieldsToConsoleString(arrayWithoutEmptyFields(fieldValues)));
            System.out.println(warning);

            return;
        }

        if (doesClassExist(className))
        {    //jeśli klasa istnieje

            int divideOn = getClassLastEntryLineNumber(className);

            String beforeStr;
            String afterStr;
            String[] tempSplitArr;

            tempSplitArr = splitToStringsOnLine(divideOn);
            beforeStr = tempSplitArr[0];
            afterStr = tempSplitArr[1];

            clearCSV();
            //writer = new BufferedWriter(new FileWriter(fileName, false));
            //writer.flush();
            insertString(beforeStr);
            addEmptyLine();
            enterFromArray(fieldValues);
            addEmptyLine();
            insertString(afterStr);

            System.out.println(String.format("Added new row: (%1$s) to database of class >%2$s< (%3$s)", fieldsToConsoleString(fieldValues), className, getClassDescriptionsConsoleText(className)));

            //todo: może dodać sprawdzenie czy dany wpis już istnieje?

        }
        else
        {    //jeśli klasy nie ma w bazie

            addNewClassWithDescriptions(className, fieldDescriptions);
            addEmptyLine();
            addElementToDatabase(className, fieldValues);

        }

        fillMissingSeparators();
    }

    /**
     * (przeciążenie)
     * Metoda dodająca do bazy danych nowy element klasy, o której wiemy że istnieje już w bazie.
     * Ważne, aby ilość pól opisowych zgadzała się z ilością pól informacyjnych.
     *
     * @param className   - nazwa klasy, do ktorej ma należeć nowy element.
     * @param fieldValues - tablica Stringów, będących informacjami charakteryzującymi element klasy
     *                    wg.szablonu zawartego w polach opisowych tej klasy
     */
    public void addElementToDatabase(String className, String[] fieldValues) throws IOException
    {

        if (!doesClassExist(className))
        {
            System.out.println("The class: >" + className + "< doesn't exist in database. Element addition cannot be performed without defining Description-fields beforehand.");
            return;
        }

        String[] descriptionsArr = getClassDescriptionsArray(className);
        addElementToDatabase(className, descriptionsArr, fieldValues);

    }

    /**
     * (przeciążenie)
     *
     * j.w., z tym że jeśli argument @allowDuplicates jest ustawiony na false, to jeśli próbujemy
     * dodać do bazy danych element o tych samych danych, co jeden z elementów już istniejących,
     * to nie zostanie on dodany. Innymi słowy - metoda pozwala wybrać, czy chcemy dodawać do bazy danych duplikaty.
     *
     * @param className
     * @param fieldValues
     * @param allowDuplicates - jeśli true, to metoda doda element do bazy, nawet jeśli znajduje się w niej
     *                        już element o tych samych danych. Jeśli false, to w podobnym przypadku nie zostanie
     *                        dodany nowy element.
     */
    public void addElementToDatabase(String className, String[] fieldValues, boolean allowDuplicates) throws IOException
    {

        if (allowDuplicates)
        {
            addElementToDatabase(className, fieldValues);
            return;
        }


        Scanner myReader = new Scanner(new File(fileName));
        String currentLine = "";
        String[] currentElementFields;
        boolean foundDuplicate = false;
        boolean arrayContentsTheSame = true;

        if (!doesClassExist(className))
        {
            System.out.println("The class: >" + className + "< doesn't exist in database. Element addition cannot be performed without defining Description-fields beforehand.");
            return;
        }

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            if (currentLine.equals(getClassLineString(className)))
            {
                currentLine = myReader.nextLine(); //linia pól opisowych
                break;
            }
        }

        while (myReader.hasNextLine() && !currentLine.equals(getSeparatorsString()))
        {
            currentLine = myReader.nextLine();
            currentElementFields = getFieldArrayFromLine(currentLine);
            //currentElementFields = arrayWithoutEmptyFields(currentElementFields);

            arrayContentsTheSame = true;

            for (int i = 0; i < fieldValues.length; i++)
            {
                if (!currentElementFields[i].equals(fieldValues[i]))
                {
                    arrayContentsTheSame = false;
                    break;
                }
            }

            if (arrayContentsTheSame)
            {
                foundDuplicate = true;
                break;
            }

        }

        if (!foundDuplicate)
        {
            String[] descriptionsArr = getClassDescriptionsArray(className);
            addElementToDatabase(className, descriptionsArr, fieldValues);
        }
        else
        {
            System.out.println("The class: >" + className + "< " +
                    "already contains an element defined by these fields: " +
                    fieldsToConsoleString(fieldValues));
            System.out.println("No new elements were added");
        }

    }

    /**
     * Metoda do odczytania pól opisowych danej klasy. Skanuje bazę w poszukiwaniu danej klasy,
     * potem sprawdza linijkę bezpośrednio pod linią klasy (tam znajdują się pola opisowe), a
     * następnie parsuje ten wiersz (pozbywa się separatorów, etc.) i zwraca dane w formie tablicy Stringów.
     *
     * @param className - nazwa klasy, której pola opisowe chcemy poznać
     * @return - tablica Stringów, w której kolejne elementy to treść kolejnych pól opisowych
     */
    public String[] getClassDescriptionsArray(String className) throws IOException
    {
        Scanner myReader = new Scanner(new File(fileName));
        String currentLine;

        if (doesClassExist(className))
        {
            while (myReader.hasNextLine())
            {
                currentLine = myReader.nextLine();
                if (currentLine.equals(getClassLineString(className)))
                {
                    break;
                }
            }
        }
        else
        {
            return null;
        }

        currentLine = myReader.nextLine();   //nastepna linia po znalezieniu linii klasy, wiec tu są deskrypcje

        return getFieldArrayFromLine(currentLine);
    }

    /**
     * Metoda przygotowująca tekst (String) do wypisania później w konsoli, na podstawie
     * podanej tablicy Stringów, w formacie w którym kolejne elementy tej tablicy
     * znajdują się w nawiasach "dziubkowych" <xxx> i są oddzielone pionową linią, np.:
     * {"111", "222", "333"} ===> <111> | <222> | <333>
     *
     * @param fields - tablica Stringów, które mają być wypisane w konsoli
     * @return - zwraca String, jak w przykładzie powyżej, który można potem wykorzystać jako
     * input do "sout'a".
     */
    private String fieldsToConsoleString(String[] fields)
    {
        String outputStr = "";
        String[] fieldsPacked = new String[fields.length];

        for (int i = 0; i < fields.length; i++)
        {
            fieldsPacked[i] = "<" + fields[i] + ">";
        }

        for (int i = 0; i < fieldsPacked.length; i++)
        {
            outputStr += fieldsPacked[i];
            if (i < fieldsPacked.length - 1)
            {
                outputStr += " | ";
            }
        }

        return outputStr;
    }

    /**
     * Metoda, która przygotowuje tekst (String), nadający się do wypisania w konsoli, który obrazuje
     * wiersz opisowy danej klasy, np.:
     * --------------------
     * Book:;;
     * Tytuł;Autor;Gatunek
     * LOTR;Tolkien;Fantasy
     * ;;
     * --------------------
     * dostaniemy:
     * <Tytuł> | <Autor> | <Gatunek>
     *
     * @param className - nazwa klasy, której pola opisowe chcemy przedstawić w konsoli
     * @return - zwraca String, jak w przykładzie powyżej, który można potem wykorzystać jako
     * input do "sout'a".
     */
    public String getClassDescriptionsConsoleText(String className) throws IOException
    {
        return fieldsToConsoleString(getClassDescriptionsArray(className));
    }

    /**
     * Metoda do utworzenia tekstu LINII KLASY.
     *
     * @param className - nazwa klasy, której linię klasy chcemy poznać
     * @return - zwraca LINIĘ KLASY, np. dla bazy danych, w której maksymalna ilość kolumn arkusza excelowskiego
     * wynosi 4, linia klasy >Book< będzie miała postać (bez myślników; są dodane dla czytelności):
     * --------
     * Book:;;;
     * --------
     */
    private String getClassLineString(String className)
    {
        return className + ":" + getSeparatorsString();
    }

    /**
     * Metoda która sprawdza czy w bazie danych istnieje już klasa o podanej nazwie
     *
     * @param className - nazwa poszukiwanej klasy
     * @return - true jeśli klasa istnieje, false jeśli klasy nie odnaleziono
     */
    public boolean doesClassExist(String className) throws IOException
    {
        Scanner myReader = new Scanner(new File(fileName));
        String currentLine;

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            if (currentLine.equals(getClassLineString(className)))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Metoda do szukania wiersza w bazie danych / pliku .csv, w którym znajduje się linia
     * z ostatnim wpisanym elementem jakiejś konkretnej klasy. Na przykład, jeśli mamy w bazie tylko
     * klasę >Book< z dwoma wpisami, to plik .csv może wyglądać następująco:
     * --------------------
     * Book:;;                  (1)
     * Tytuł;Autor;Rok          (2)
     * LOTR;Tolkien;2000        (3)
     * Diuna;Herbert;2005       (4)
     * ;;                       (5)
     * -------------------
     * Metoda zwróci w tym przypadku liczbę 4
     *
     * @param className - nazwa klasy, której ostatniego elementu poszukujemy
     * @return - numer wiersza (w pliku .csv), w ktorym znaleziono ostatni wpis / element
     * przynależący do danej klasy
     */
    private int getClassLastEntryLineNumber(String className) throws IOException
    {
        int lineNumber = 0;
        String currentLine;

        Scanner myReader = new Scanner(new File(fileName));

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            lineNumber++;
            if (currentLine.equals(getClassLineString(className)))
            {
                break;
            }
        }

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            lineNumber++;
            if (currentLine.equals(getSeparatorsString()))
            {
                break;
            }
        }

        return lineNumber - 1;
    }

    /**
     * Metoda do szukania wiersza w bazie danych / pliku .csv, w którym znajduje się linia
     * z definicją konkretnej klasy
     *
     * @param className - nazwa poszukiwanej klasy
     * @return - numer wiersza (w pliku .csv), w ktorym znaleziono "linię klasy",
     * np. dla klasy >Book< z trzema polami opisowymi będzie to wiersz o treści "Book:;;"
     */
    private int getClassDefinitionLineNumber(String className) throws IOException
    {
        int lineNumber = 0;
        String currentLine;

        Scanner myReader = new Scanner(new File(fileName));

        while (myReader.hasNextLine())
        {
            currentLine = myReader.nextLine();
            lineNumber++;
            if (currentLine.equals(getClassLineString(className)))
            {
                break;
            }
        }

        return lineNumber;
    }

    /**
     * Metoda służąca do podmiany istniejących pól opisowych klasy.
     * Np. >Book< opisywane przez <Autor>;<Tytuł>;<Rok> można zamienić na <ISBN>;<Cena>;<Format>
     * Aktualnie metoda zadziała tylko jeśli podmieniamy pola opisowe - ilość starych i nowych
     * pól musi być ta sama. Nic nie dzieje się z już istniejącymi elementami tej klasy.
     *
     * @param className            - nazwa klasy, której pola opisowe chcemy zmienić
     * @param newFieldDescriptions - tablica, w której kolejne Stringi zostaną kolejno nowymi polami opisowymi
     */
    public void changeClassDescriptions(String className, String[] newFieldDescriptions) throws IOException
    {
        if (!doesClassExist(className))
        {
            System.out.println("The class: >" + className + "< doesn't exist in database. Descriptions modification failed.");
            return;
        }

        String[] currentFieldDescriptions = getClassDescriptionsArray(className);
        int descriptionLineNumber = getClassDefinitionLineNumber(className) + 1;

        String beforeStr;
        String afterStr;

        beforeStr = getDatabaseContentUpToLine(descriptionLineNumber - 1);
        afterStr = getDatabaseContentAfterLine(descriptionLineNumber);

        if (arrayWithoutEmptyFields(currentFieldDescriptions).length == newFieldDescriptions.length)
        {
            clearCSV();
            insertString(beforeStr);
            addEmptyLine();
            enterFromArray(newFieldDescriptions);
            addEmptyLine();
            insertString(afterStr);
        }
        else
        {

        }

    }

    /**
     * Metoda - czyści bazę danych, kasując jej treść. Pozostawia pusty plik bazy.
     */
    private void clearCSV() throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
        writer.flush();
        writer.close();

    }

}
