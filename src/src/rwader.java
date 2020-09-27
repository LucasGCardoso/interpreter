/*
public static rwader(String[] args) {
        //Path path1 = Paths.get("/home/user2/statusReport");
        //Path path2 = Paths.get("/home","user2","statusReport");
        Path path1 = Paths.get("C:\\Dropbox sync\\Dropbox\\poo\\aeroporto.rar");
        // Caminho a partir da raiz do usuário (depende do S.O.)
        //Path path4 = Paths.get(System.getProperty("user.home"),"books", "java7.pdf");

        System.out.println("toString: " + path1.toString());     // /home/user2/statusReport
        System.out.println("getFileName: " + path1.getFileName());  // statusReport
        System.out.println("getName(0): " + path1.getName(0));     // home
        System.out.println("getNameCount: " + path1.getNameCount()); // 3
        System.out.println("subpath(0,2): " + path1.subpath(0, 2));   // /home/user2
        System.out.println("getParent: " + path1.getParent());    // /home/user2
        System.out.println("getRoot: " + path1.getRoot());      // / ou C:\

        //
        //escrevnedo em um arquivo
        //
        Path pathTexto = Paths.get("C:\\Dropbox sync\\Dropbox\\poo\\streams\\teste.txt");
        // defaultCharset retorna a codificação padrão de textos (usualmente UTF-8)
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(pathTexto.getFileName(), Charset.forName("utf8")))) {
        writer.println("Escrevendo linha em arquivo texto");
        writer.println("Outra linha...");
        } catch (IOException x) {
        System.err.format("Erro de E/S: %s%n", x);
        }

        //
        // escrevendo com CSV
        //
        ArrayList<Pessoa> lista = new ArrayList<Pessoa>();

        Pessoa p1 = new Pessoa("João", 23);
        Pessoa p2 = new Pessoa("Roberto", 43);
        Pessoa p3 = new Pessoa("Lucas", 18);

        lista.add(p1);
        lista.add(p2);
        lista.add(p3);

        Path pathPessoa = Paths.get("C:\\Dropbox sync\\Dropbox\\poo\\streams\\dados.txt");

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(pathPessoa.getFileName(), Charset.forName("utf8")))) {
        for(Pessoa p: lista)
        writer.format("%s;%s%n", p.getNome(), p.getIdade());
        }
        catch (IOException x) {
        System.err.format("Erro de E/S: %s%n", x);
        }

        //
        // Lendo de um arquivo
        //
        Path pathLendo = Paths.get("C:\\Dropbox sync\\Dropbox\\poo\\streams\\testeleitura.txt");
        try (BufferedReader reader = Files.newBufferedReader(pathLendo.getFileName(), Charset.forName("utf8"))) {
        String line = null;
        while ((line = reader.readLine()) != null) {
        System.out.println("Linha: "+line);
        }
        }
        catch (IOException x) {
        System.err.format("Erro de E/S: %s%n", x);
        }

        //
        //Lendo de um arquivo palavra por palavra
        //
        Path pathFormatado = Paths.get("C:\\Dropbox sync\\Dropbox\\poo\\streams\\testeleitura.txt");
        try (Scanner sc = new Scanner(Files.newBufferedReader(pathFormatado.getFileName(), Charset.forName("utf8")))) {
        String pal = null;
        while (sc.hasNext()) {
        pal = sc.next();
        System.out.println("Palavra: "+pal);
        }
        }
        catch (IOException x) {
        System.err.format("Erro de E/S: %s%n", x);
        }


        //
        // Lê de arquivo com separadores csv
        //
        Path path2 = Paths.get("C:\\Dropbox sync\\Dropbox\\poo\\streams\\dados.txt");
        try (Scanner sc = new Scanner(Files.newBufferedReader(path2.getFileName(), Charset.forName("utf8")))) {
        sc.useDelimiter("[;\n]"); // separadores: ; e nova linha
        String header = sc.nextLine(); // pula cabeçalho
        String nome, idade;
        while (sc.hasNext()) {
        nome = sc.next();
        idade = sc.next();
        System.out.format("%s - %s%n", nome, idade);
        }
        }
        catch (IOException x) {
        System.err.format("Erro de E/S: %s%n", x);
        }


        //
        // outra maneira de ler arquivo
        //
        Path path3 = Paths.get("C:\\Dropbox sync\\Dropbox\\poo\\streams\\dados.txt");
        try (BufferedReader br = Files.newBufferedReader(path3.getFileName(), Charset.defaultCharset()))
        {
        String linha = null;
        while((linha = br.readLine()) != null) {
        Scanner sc = new Scanner(linha).useDelimiter(";"); // separador é ;
        String nome, data, idade;
        nome = sc.next();
        idade = sc.next();
        System.out.format("%s - %s %n", nome, idade);
        }
        }
        catch (IOException x) {
        System.err.format("Erro de E/S: %s%n", x);
        }

        }
        }*/