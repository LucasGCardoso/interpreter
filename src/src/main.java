public class main {
    public static void main(String args[]){

        // You should manually set the files paths here.
        // Ideally they should be in the root directory of the project.
        String sourceFile = "source";
        String ofFile = "of.txt";
        String ifFile = "if.txt";

        Interpreter machine = new Interpreter(sourceFile, ifFile, ofFile);
        machine.run(">,.$");
    }
}
