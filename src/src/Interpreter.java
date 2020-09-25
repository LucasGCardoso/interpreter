public class Interpreter {
    private int dataPointer;
    private int programPointer;
    private int[] memory;

    private int[][] specialCharMap;
    private int IFPointer;

    public Interpreter() {
        dataPointer = 0;
        programPointer = 0;
        memory = new int[3]; //The memory will be 1000, 3 is just for testing pourposes
        IFPointer = 0;
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
        System.out.println(memory[dataPointer]);
    }

    private void memoryDump(){
        System.out.println("Memory:");
        for(int i=0; i<memory.length; i++){
            System.out.println(memory[i]);
        }
    }

    private void readIF(){
        int [] file = new int[2];
        file[0] = 2;
        file[1] = 3;
        memory[dataPointer] = file[IFPointer];
        IFPointer++;
    }
}