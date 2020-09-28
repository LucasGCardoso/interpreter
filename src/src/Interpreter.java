import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * This class receives three files: SOURCE, IF and OF, and then uses them
 * The SOURCE file contains the program to be run.
 * The IF file cntains the values in the memory.
 * THE OF file will be used to write the final values in the memory.
 * as if it were an interpreter for a fictitious machine.
 * At the end of the running the content of the memory is displayed on the screen.
 *
 * @author            Diego Klein
 * @author            Lucas Gavirachi Cardoso
 * @author            Pedro Maia Rogoski
 * @author            Roberto Luís Rezende
 * @version           1.0
 * @since             2020-09-27
 */
public class Interpreter {
    private int dataPointer;
    private int programPointer;
    private int[] memory;

    private String sourceFile;
    private String ofFile;
    private String ifFile;

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
        memory = new int[3]; //The memory will be 1000, 3 is just for testing pourposes
        IFPointer = 0;

        // sets the files
        this.sourceFile = sourceFile;
        this.ifFile = ifFile;
        this.ofFile = ofFile;

        this.program = readSource(sourceFile);
        this.ifArray = turnIfFileIntoArray (ifFile);

    }

    /**
     * This method runs the program.
     *
     * The commands are:
     * >    increments the data pointer in one unit (one unit to the right).
     * <    decrements the data pointer in one unit (one unit to the left).
     * +    increments in one unit the position of the memory referenced by the data pointer.
     * -    decrements in one unit the position of the memory referenced by the data pointer.
     * [    if the position of the memory referenced by the data pointer is 0, then sends the program pointer
     *      to the next command following the correspondig ]. Otherwise, advances the program pointer.
     * ]    if the position of the memory referenced by the data pointer is not 0,
     *      then sends the program pointer to the previous correspondig [.
     * ,    reads an entry from the IF file and stores it at the memory position referenced by the data pointer.
     * .    writes in the OF file the byte referenced by the data pointer.
     * $    ends the program and prints its content in the OF file.
     *
     *      Any other command is ignored and the program pointer is incremented.
     *
     * @param program     the program from the SOURCE file
     * @return            true if... or false if...
     */
    public boolean run(String program) {
        if(!checkSpecialChars(program)) return false;
        mapSpecialChars(program);
        while (true) {
            char command = program.charAt(programPointer);
            switch (command){
                case '>':
                    dataPointer++;
                    programPointer++;
                    break;
                case '<':
                    dataPointer--;
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
                    readIF();
                    programPointer++;
                    break;
                case '.':
                    //escreve no arquivo OF o byte apontado pelo ponteiro de dados.
                    writeInOF();
                    programPointer++;
                    break;

                case '$':
                    //termina o programa e imprime o conteúdo da memória no arquivo OF.
                    memoryDump();
                    return true;
            }
        }
    }

    /**
     * This function checks
     *
     * @param program     the program that was read from the SOURCE file
     * @return            true if... or false if...
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
     * This function will map which [ belongs to each ], keeping the position in the string
     *
     * @param program     the program that was read from the SOURCE file
     */
    private void mapSpecialChars(String program){
        int count = 0;

        for(int i=0; i<program.length(); i++){
            if(program.charAt(i) == '[')
                count++;
        }
        specialCharMap = new int[count][2];
        int matrixRow = 0;
        for(int i=0; i<program.length(); i++){
            if(program.charAt(i) == '['){
                specialCharMap[matrixRow][0] = i;
                matrixRow++;
            }
        }

        matrixRow = 0;
        for(int i=program.length()-1; i==0; i--){
            if(program.charAt(i) == ']'){
                specialCharMap[matrixRow][1] = i;
                matrixRow++;
            }

        }
    }

    /**
     * Writes the current position of the memory int the OF file
     *
     * @exception IOException     On file not found error
     */
    private void writeInOF(){
        Path pathTexto = Paths.get(ofFile);

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(pathTexto.getFileName(), Charset.forName("utf8")))) {
            writer.println(memory[dataPointer]);
        } catch (IOException x) {
            System.err.format("Erro de E/S: %s%n", x);
        }

    }

    /**
     * Reads the SOURCE file and turns it into a single String for easier manuipulation
     *
     * @param source                the SOURCE file
     * @exception IOException       On file not found error
     * @return                      a String with the content of the SOURCE file
     */
    public String readSource(String source) {
        Path path1 = Paths.get(source);
        String sourceStringyfied ="";

        try (BufferedReader reader = Files.newBufferedReader(path1.getFileName(), Charset.forName("utf8"))) {
            String line = null;

            while ((line = reader.readLine()) != null) {

                if(!line.isEmpty()){
                    line = line.trim();
                    sourceStringyfied = sourceStringyfied + line;
                }
            }

            return sourceStringyfied;

        } catch (IOException x) {
            System.err.format("Erro de E/S: %s%n", x);
        }
        return null;
    }

    /**
     * Reads the file IF and turns it into an array of int for easier manipulation
     *
     * @param ifFile                the IF file
     * @exception IOException       on file not found error
     * @return                      an array with the IF file values
     */
    public int [] turnIfFileIntoArray (String ifFile) {
        Path path1 = Paths.get(ifFile);
        int [] ifArray;

        try (BufferedReader reader = Files.newBufferedReader(path1.getFileName(), Charset.forName("utf8"))) {
            String line = null;

            // checks the size of the future array for the IF file
            int size = getSizeForTheIfFileArray(ifFile);

            ifArray = new int[size];

            int index=0;
            int IFvalue;

            while ((line = reader.readLine()) != null) {
                if(!line.isEmpty()){
                    line = line.trim();
                    IFvalue = Integer.parseInt(line);
                    ifArray[index]=IFvalue;
                    index++;
                }
            }

            return ifArray;

        } catch (IOException x) {
            System.err.format("Erro de E/S: %s%n", x);
        }
        return null;
    }

    /**
     * reads the IF file to check how many values it has
     *
     * @param ifFile                the IF file
     * @exception IOException       On file not found error
     * @return                      the number os values in the IF file
     */
    public int getSizeForTheIfFileArray (String ifFile) {
        Path path1 = Paths.get(ifFile);
        int size=0;

        try (BufferedReader reader = Files.newBufferedReader(path1.getFileName(), Charset.forName("utf8"))) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                if(!line.isEmpty()){
                    size++;
                }
            }

            return size;

        } catch (IOException x) {
            System.err.format("Erro de E/S: %s%n", x);
        }
        return 0;
    }

    /**
     * Prints in the screen the values stored in the memory
     *
     */
    private void memoryDump(){
        System.out.println("\nMemory:");
        for(int i=0; i<memory.length; i++){
            System.out.print(memory[i] + "; ");
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
}