public class main {
    public static void main(String args[]){

        // You should manually set the files paths here.
        // Ideally they should be in the root directory of the project.
        String sourceFile = "source";
        String ofFile = "of.txt";
        String ifFile = "if.txt";

        //Não termina com $
        String programaTeste1 = ">";

        //Não tem pares de [ e ]
        String programaTeste2 = "]$";

        //Deve rodar e dar saída 0, 2, 0
        String programaTeste3 = ">,.$";

        Interpreter machine = new Interpreter(sourceFile, ifFile, ofFile);

        // Checks if the program has any errors and runs it
        int errorCode = machine.run(programaTeste3);
        if (errorCode==-1) System.out.println("The program is badly written. It has at least one unpaired [ or ].");
        else if (errorCode==-2) System.out.println("The program is badly written. It doesn´t end with $.");
        else System.out.println("\nProgram succesfully ended.");

    }
}
