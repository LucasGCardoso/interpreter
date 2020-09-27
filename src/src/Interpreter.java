import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static jdk.nashorn.internal.runtime.JSType.isNumber;

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

    /*
        *
    >    incrementa o ponteiro de dados para a próxima posição (uma unidade à direita).
    <    decrementa o ponteiro de dados para a posição anterior (uma unidade à esquerda).
    +    incrementa em uma unidade a posição apontada pelo ponteiro de dados.
    -    decrementa em uma unidade a posição apontada pelo ponteiro de dados.
    [    se a posição apontada pelo ponteiro de dados é 0, então desloque o ponteiro de programa para o próximo comando em sequência ao ] correspondente. Caso contrário, avance o ponteiro de programa.
    ]    se a posição apontada pelo ponteiro de dados é diferente de 0, então retroceda o ponteiro de programa para o [ correspondente.
    ,    lê uma entrada do arquivo IF e o armazena na posição apontada pelo ponteiro de dados
    .    escreve no arquivo OF o byte apontado pelo ponteiro de dados.
    $    termina o programa e imprime o conteúdo da memória no arquivo OF.
        *
        * */
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

    // This function will map which [ belongs to each ], keeping the position in the string;
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

    private void writeInOF(){
        Path pathTexto = Paths.get(ofFile);

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(pathTexto.getFileName(), Charset.forName("utf8")))) {
            writer.println(memory[dataPointer]);
        } catch (IOException x) {
            System.err.format("Erro de E/S: %s%n", x);
        }

    }

    //lê o arquivo source e o transforma em uma String única
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

    //lê o arquivo if e o transforma em um array
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

    private void memoryDump(){
        System.out.println("\nMemory:");
        for(int i=0; i<memory.length; i++){
            System.out.print(memory[i] + "; ");
        }
    }

    private void readIF(){
        memory[dataPointer] = ifArray[IFPointer];
        IFPointer++;
    }
}