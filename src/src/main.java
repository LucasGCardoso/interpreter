public class main {
    public static void main(String args[]){

        // You should manually set the files paths here.
        // Ideally they should be in the root directory of the project.
        // The OF file will be created, so you can choose any name you want.
        String sourceFile = "source";
        String ofFile = "of";
        String ifFile = "if.txt";

        // If you want to use an IF file, please use this constructor
        //Interpreter machine = new Interpreter(sourceFile, ifFile, ofFile);

        // If you don´t want to use an IF file, please use this constructor
        Interpreter machine = new Interpreter(sourceFile, ofFile);

        // Checks if the program has any errors and runs it
        int errorCode = machine.run();

        // Checks if the program ended successfully (errorCode=0) and then
        // converts the OF file from ASCII to String.
        // This will only be useful if the intent of the program is to output any kind of text.
        if (errorCode==0) {
            String outputInTextFormat = machine.convertOfFileFromAsciiToText();
            System.out.println(outputInTextFormat);
        }

        // Treats the error code received from running the program
        if (errorCode == -1)
            System.out.println("Error. The program is badly written. It has at least one unpaired [ or ].");
        else if (errorCode == -2)
            System.out.println("Error. The program is badly written. It doesn´t end with $.");
        else if (errorCode == -3)
            System.out.println("Error. There is a command to read from IF file in the source code, but you have not provided an IF file.");
        else
            System.out.println("\nProgram succesfully ended.");
    }
}
