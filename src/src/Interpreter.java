import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

/**
 * This class receives three files: SOURCE, IF and OF, and then uses them
 * as if it were an interpreter for a fictitious machine.
 *
 * The SOURCE file contains the program to be run.
 * The IF file contains the user input values.
 * THE OF file will contain the outputs and the final values of the memory.
 *
 * @author            Diego Klein
 * @author            Lucas Gavirachi Cardoso
 * @author            Pedro Maia Rogoski
 * @author            Roberto Luís Rezende
 * @version           1.0
 * @since             2020-10-02
 */
public class Interpreter {
    private int dataPointer;
    private int programPointer;
    private int[] memory;
    private int memorySize = 1000;

    private String ofFile;
    private String ifFile;
    private String ofFileContent;
    private String ofFileContentReadableString;

    private String program;
    private int[] ifArray;

    private int[][] specialCharMap;
    private int IFPointer;

    /**
     * Interpreter class constructor
     *
     * @param sourceFile  the SOURCE file
     * @param ifFile      the IF file
     * @param ofFile      the OF file
     */
    public Interpreter(String sourceFile, String ifFile, String ofFile) {
        dataPointer = 0;
        programPointer = 0;
        IFPointer = 0;

        memory = new int[memorySize];

        // sets the files
        this.ifFile = ifFile;
        this.ofFile = ofFile;

        // turns the SOURCE file into a single String
        this.program = readSource(sourceFile);

        // turns the IF file into an array of int
        if (turnIFFileIntoArray(ifFile)==null) return;
        else this.ifArray = turnIFFileIntoArray(ifFile);

        // Initializes the String that will contain the output of the "." command and
        // later will be stored in the OF file
        ofFileContent = "";

        // Initializes the String that will contain the output of the "." command in
        // a readable format, i.e., converted from ASCII code to characters.
        ofFileContentReadableString="";
    }


    /**
     * Interpreter class constructor without IF file use
     *
     * @param sourceFile  the SOURCE file
     * @param ofFile      the OF file
     */
    public Interpreter(String sourceFile, String ofFile) {
        dataPointer = 0;
        programPointer = 0;

        memory = new int[memorySize];

        // sets the files
        this.ofFile = ofFile;

        // turns the SOURCE file into a single String
        this.program = readSource(sourceFile);

        // Initializes the String the will contain the output of the "." command and
        // later will be stored in the OF file
        ofFileContent = "";

        // Initializes the String that will contain the output of the "." command in
        // a readable format, i.e., converted from ASCII code to characters.
        ofFileContentReadableString="";
    }


    /**
     * This method runs the program.
     *
     * The commands are:
     * >    moves the data pointer to the next cell.
     * <    moves the data pointer to the previous cell.
     * +    adds 1 to the value of the cell referenced by the data pointer.
     * -    subtracts 1 to the value of the cell referenced by the data pointer.
     * [    if the position of the memory referenced by the data pointer is 0, then sends the program pointer
     *      to the next command following the corresponding ]. Otherwise, advances the program pointer.
     * ]    if the position of the memory referenced by the data pointer is not 0,
     *      then sends the program pointer to the previous corresponding [.
     * ,    reads an entry from the IF file and stores it at the memory position referenced by the data pointer.
     * .    writes in the OF file the byte referenced by the data pointer.
     * $    ends the program and dumps the memory in the OF file.
     *
     * #    We added this optional command. It will ignore a whole line in the SOURCE file.
     *      It is used to create comment lines.
     *
     *      Any other command is ignored and the program pointer is incremented.
     *
     * @return             0 if the program run successfully
     *                    -1 if it has unpaired [ and ]
     *                    -2 if it doesn't end with $
     *                    -3 if used the command "," without providing an IF file - Since many BF programmers use comments with "," we decided to remove this error message from our interpreter. The code for this only commented, so you can use if you want.
     *                    -4 if the program tries to access a memory index higher than the allocated memory
     *                    -5 if the program tries to access a negative memory
     *                    -6 if the program tries to read from the IF file but it has no more values to be read.  - Since many BF programmers use comments with "," we decided to remove this error message from our interpreter. The code for this only commented, so you can use if you want.
     *
     */
    public int run() {
        if(!checkSpecialChars(program)) return -1;
        if(!checkForEndOfProgram()) return -2;
        mapSquareBrackets(program);
        while (true) {
            char command = program.charAt(programPointer);
            switch (command){
                case '>':
                    dataPointer++;

                    // If the program has a logic error
                    if (dataPointer>=memorySize) return -4;

                    programPointer++;
                    break;
                case '<':
                    dataPointer--;

                    // If the program has a logic error
                    if (dataPointer<0) return -5;

                    programPointer++;
                    break;
                case '+':
                    memory[dataPointer]++;
                    programPointer++;
                    break;
                case '-':
                    memory[dataPointer]--;
                    programPointer++;
                    break;
                case '[':
                    if(memory[dataPointer] == 0){
                        for(int i=0; i<specialCharMap.length; i++){
                            if(specialCharMap[i][0] == programPointer){
                                programPointer = specialCharMap[i][1] + 1;
                            }
                        }
                    }else{
                        programPointer++;
                    }
                    break;
                case ']':
                    if(memory[dataPointer] != 0){
                        for(int i=0; i<specialCharMap.length; i++){
                            if(specialCharMap[i][1] == programPointer){
                                programPointer = specialCharMap[i][0];
                            }
                        }
                    }else{
                        programPointer++;
                    }
                    break;
                case ',':
                    /*
                    * At the end of our coding we decided to comment out this error message, since many programmers use "," in their comments.
                    *
                    // In case you reach this command but there was no IF file provided by the user
                    if (ifFile==null) {
                        return -3;
                    }
                    */

                    /*
                    * At the end of our coding we decided to comment out this error message, since many programmers use "," in their comments.
                    *
                    // In case the program tries to read a value from the IF array but the array is smaller than expected
                    if (IFPointer >= ifArray.length) {
                        return -6;
                    }
                    */

                    // In case the programmer used "," simply as a comment
                    if (ifFile == null || ifArray.length==0) {
                        programPointer++;
                        break;
                    }

                    readIF();
                    programPointer++;
                    break;
                case '.':
                    ofFileContent = ofFileContent + memory[dataPointer] + "\n";
                    ofFileContentReadableString = ofFileContentReadableString + Character.toString((char)memory[dataPointer]);

                    // System.out.println(ofFileContentReadableString);

                    programPointer++;
                    break;
                case '$':
                    memoryDump();
                    writeInOF();
                    return 0;
                default:
                    programPointer++;
            }
        }
    }

    /**
     * This function checks if the program has an equal amount of [ and ].
     * If the program doesn't has an equal amount of [ and ] it cannot run properly.
     *
     * @param program     the program that was read from the SOURCE file
     * @return            true if the number of [ and ] is equal, otherwise it returns false
     */
    private boolean checkSpecialChars(String program){
        int count = 0;

        for(int i=0; i<program.length(); i++){
            if(program.charAt(i) == '[')
                count++;

            if(program.charAt(i) == ']')
                count--;
        }
        return count == 0;
    }


    /**
     * This function will map which '[' belongs to each ']'
     *
     * @param program     the program that was read from the SOURCE file
     */
    private void mapSquareBrackets(String program){
        Stack<Integer> openChars = new Stack<Integer>();
        int count = 0;

        for(int i=0; i<program.length(); i++){
            if(program.charAt(i) == '[')
                count++;
        }

        specialCharMap = new int[count][2];
        int matrixRow = 0;

        for(int i=0; i<program.length(); i++){
            if(program.charAt(i) == '['){
                openChars.push(i);
            }
            if(program.charAt(i) == ']'){
                specialCharMap[matrixRow][0] = openChars.pop();
                specialCharMap[matrixRow][1] = i;
                matrixRow++;
            }
        }
    }

    /**
     * Writes the current position of the memory in the OF file
     *
     * @exception IOException           on any I/O error
     */
    private void writeInOF(){
        Path pathTexto = Paths.get(ofFile);

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(pathTexto.getFileName(), Charset.forName("utf8")))) {
            writer.println(ofFileContent);
        } catch (IOException x) {
            System.err.format("I/O Error: %s%n\n", x);
        }
    }

    /**
     * Reads the SOURCE file and turns it into a single String for easier manipulation
     *
     * @param source                    the SOURCE file
     * @exception NoSuchFileException   on file not found error
     * @exception IOException           on any other error
     * @return                          a String with the content of the SOURCE file
     */
    public String readSource(String source) {
        Path path1 = Paths.get(source);
        String sourceStringyfied ="";

        try (BufferedReader reader = Files.newBufferedReader(path1.getFileName(), Charset.forName("utf8"))) {
            String line = null;

            while ((line = reader.readLine()) != null) {

                if (!line.isEmpty() && line.charAt(0) !='#') {
                    line = line.trim();
                    sourceStringyfied = sourceStringyfied + line;
                }
            }

            return sourceStringyfied;

        } catch (NoSuchFileException x) {
            System.err.format("SOURCE File not found.\n", x);
        } catch (IOException x) {
            System.err.format("I/O Error %s%n\n", x);
        }
        return null;
    }

    /**
     * Reads the IF file and turns it into an array of int for easier manipulation
     *
     * @param ifFile                    the IF file
     * @exception NoSuchFileException   on file not found error
     * @exception NumberFormatException if found anything else other than integer values in the IF file
     * @exception IOException           on any other error
     * @return                          an array with the IF file values
     */
    public int [] turnIFFileIntoArray(String ifFile) {
        Path path1 = Paths.get(ifFile);
        int [] IFArray;

        try (BufferedReader reader = Files.newBufferedReader(path1.getFileName(), Charset.forName("utf8"))) {
            String line = null;

            // checks the size of the future array for the IF file
            int size = getSizeForTheIfFileArray(ifFile);

            IFArray = new int[size];

            int index = 0;
            int IFValue;

            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    line = line.trim();

                    // if the value in the IF file is a number it is entered as a single value in the array
                    if (isNumeric(line)) {
                        IFValue = Integer.parseInt(line);
                        IFArray[index] = IFValue;
                        index++;
                    }

                    // if the value is a String it is entered as a sequence of bytes
                    else {
                        for (int i=0; i<line.length(); i++) {
                            IFValue = (byte) line.charAt(i);
                            IFArray[index] = IFValue;
                            index++;
                        }
                    }

                }
            }

            return IFArray;

        } catch (NoSuchFileException x) {
            System.err.format("IF File not found.\n", x);
        } catch (NumberFormatException x) {
            System.err.print("IF file must contain only integer numbers.\n");
        } catch (IOException x) {
            System.err.format("I/O error: %s%n\n", x);
        }
        return null;
    }


    /**
     * Checks if the line read in the IF file is a number
     *
     * @param line  the line in the IF file
     * @return      true if it is a int or false otherwise
     */
    public static boolean isNumeric(String line) {
        try {
            Integer.parseInt(line);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


    /**
     * Reads the IF file to check how many values it has
     *
     * @param ifFile                the IF file
     * @exception IOException       on any I/O error
     * @return                      the number os values in the IF file
     */
    public int getSizeForTheIfFileArray (String ifFile) {
        Path path1 = Paths.get(ifFile);
        int size=0;

        try (BufferedReader reader = Files.newBufferedReader(path1.getFileName(), Charset.forName("utf8"))) {
            String line = null;

            while ((line = reader.readLine()) != null) {

                if(!line.isEmpty()){

                    // if the value in the IF file is a number it is counted as a single entry in the array
                    if (isNumeric(line)) {
                        size++;
                    }

                    // if the value is a String it creates as many spaces as there are characters
                    else {
                        for (int i=0; i<line.length(); i++) {
                            size++;
                        }
                    }
                }
            }

            return size+1;

        } catch (IOException x) {
            System.err.format("I/O Error: %s%n\n", x);
        }
        return 0;
    }


    /**
     * Dumps the memory in the OF file String.
     *
     */
    private void memoryDump(){
        dataPointer = 0;

        ofFileContent = ofFileContent + "\n\nMemory Dump:\n";

        for(int i=0; i<memory.length; i++){
            ofFileContent = ofFileContent + memory[dataPointer] + " ";
            dataPointer++;
        }
    }


    /**
     * Reads the IF array and puts its current value in the current position of the memory,
     * then increases the IF pointer in one position
     */
    private void readIF(){
        memory[dataPointer] = ifArray[IFPointer];
        IFPointer++;
    }


    /**
     * Returns the content of the OF file in a readable format
     *
     * @return     the content of the OF file in String format
     */
    public String convertOfFileFromAsciiToText (){
        return ofFileContentReadableString;
    }


    /**
     * Check if the program has any $
     *
     * @return  true if the program has any ending command or false otherwise
     */
    public boolean checkForEndOfProgram(){
        for (int i=0; i<program.length(); i++){
            if (program.charAt(i)=='$') return true;
        }

        return false;
    }



}