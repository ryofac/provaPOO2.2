package Utils;

import java.util.Scanner;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class IOUtils {
    // Scanner utilizado para as operações de entrada e saída da classe
    private static Scanner scanner = new Scanner(System.in, "UTF-8").useDelimiter("\n");

    // Método para obter uma string passando uma mensagem, que também é uma string
    static public String getText(String msg) {
        System.out.print(ConsoleColors.YELLOW_UNDERLINED + msg + ConsoleColors.RESET);
        String input = scanner.next().trim();
        return input;
    }

    // Método para obter uma escolha (Sim ou não)
    static public Boolean getChoice(String msg) {
        String result = getTextNormalized(msg + " [S/n] \n > ");
        return result == "" || result.contains("s");

    }

    // Usado para obter dados que no "banco" estão em um formato específico como
    // username
    static public String getTextNormalized(String msg) {
        return getText(msg).trim().toLowerCase();
    }

    // Método para obter um inteiro passando uma mensagem que é uma string
    static public Integer getInt(String msg) throws NumberFormatException {
        System.out.print(ConsoleColors.GREEN + msg + ConsoleColors.RESET);
        String input = scanner.next().trim();
        return Integer.parseInt(input);

    }

    // Método que quando invocado cria a ilusão de apagar a tela:
    // exibe vários caracteres de quebra de linha
    static public void clearScreen() {
        System.out.print("<Enter....>");
        scanner.next();
        System.out.println("\n".repeat(20));
    }

    // Fecha o scanner para as operações de entrada e saída da classe
    // É obrigatório a sua invoação ao fim do programa, se não realizada, é
    // levantada a
    static public void closeScanner() {
        scanner.close();
    }

    // Classe que encapsula a escrita em arquivo passando uma string com o conteúdo
    public static File writeOnFile(String filePath, String content) {
        Charset utf8 = Charset.forName("UTF-8");
        try (FileWriter escritor = new FileWriter(filePath, utf8, false)) {
            escritor.write(content);
            return new File(filePath);

        } catch (IOException e) {
            System.out.println("An error ocurred while reading the file" + filePath);
        }
        return null;

    }

    // Classe que encapsula a leitura das linhas de um arquivo, retornando uma lista
    // de linhas
    public static List<String> readLinesOnFile(String filePath) {
        try {

            File toRead = new File(filePath);
            if (!toRead.exists()) {
                createFile(filePath);
            }
            Scanner reader = new Scanner(toRead);
            List<String> lines = new ArrayList<String>();
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());

            }
            reader.close();
            return lines;

        } catch (Exception e) {
            System.err.println("An error ocurred while reading the file" + filePath);
            e.printStackTrace();
            return null;
        }
    }

    // Método que encapsula a criação de um arquivo passando um caminho
    public static void createFile(String filePath) {
        try {
            File toBeCreated = new File(filePath);
            if (!toBeCreated.exists()) {
                toBeCreated.createNewFile();
                System.out.println("Non existent file " + filePath + " created!");
            }
        } catch (IOException e) {
            System.out.println("An error ocurred while tried to read file " + filePath);
            e.printStackTrace();

        }

    }

}
