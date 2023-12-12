package Utils;

import java.util.Scanner;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * A classe IOUtils é utilizada para implementar operações comuns de entrada e saída
 */
public class IOUtils {
    // Scanner utilizado para as operações de entrada e saída da classe
    private static Scanner scanner = new Scanner(System.in, "UTF-8").useDelimiter("\n");

    /**
     *  Método para obter uma string passando uma mensagem, que também é uma string
     * @param msg o texto que será mostrado ao pedir a mensagem
     * @return o input coletado do usuário
     * @throws InterruptedException
     */
    static public String getText(String msg) {
        System.out.print(ConsoleColors.YELLOW_UNDERLINED + msg + ConsoleColors.RESET);
        String input = scanner.next().trim();
        return input;
    }

    /**
     * Método para obter uma escolha do usuário (Sim ou não)
     * @param msg o texto que será mostrado antes de pedir a escolha
     * @return *true* se o texto for vazio ou contiver o caractere "S"
     */
    static public Boolean getChoice(String msg) {
        String result = getTextNormalized(msg + " [S/n] \n > ");
        return result == "" || result.contains("s");

    }

    /**
     * Usado para obter dados que precisam ser em caixa baixa e sem espaços
     * @param msg o texto que será mostrado antes de pedir o input do usuário
     * @return o texto do usuário, normalizado
     */
    static public String getTextNormalized(String msg) {
        return getText(msg).trim().toLowerCase();
    }

    /**  Método para obter um inteiro do input do usuário passando uma mensagem que é uma string
     * @param msg o texto que será mostrado antes de pedir o input do usuário
     * @return a conversão em inteiro do input, se possível
     * @throws NumberFormatException caso a conversão para inteiro não seja possível
     */
    static public Integer getInt(String msg) throws NumberFormatException {
        System.out.print(ConsoleColors.GREEN + msg + ConsoleColors.RESET);
        String input = scanner.next().trim();
        return Integer.parseInt(input);

    }

    /**
     * Exibe um texto em vermelho para representar um erro
     * @param msg a mensagem a ser exibida
     */
    static public void showErr(String msg){
        System.out.println(ConsoleColors.RED + msg + ConsoleColors.RESET);
    }

     /**
     * Exibe um texto em amarelo para representar um aviso
     * @param msg a mensagem a ser exibida
     */
    static public void showWarn(String msg){
        System.out.println(ConsoleColors.YELLOW + msg + ConsoleColors.RESET);
    }
    
    
    /**
     * Método que quando invocado cria a ilusão de apagar a tela exibindo vários caracteres de quebra de linha
     * @throws InterruptedException
     */
    static public void clearScreen(){
       System.out.println("<Enter....>");
        scanner.next();
        System.out.println("\n".repeat(30));
    }

    // Fecha o scanner para as operações de entrada e saída da classe
    // É obrigatório a sua invoação ao fim do programa, se não realizada, é
    // levantada a
    static public void closeScanner() {
        scanner.close();
    }

    // Método que encapsula a escrita em arquivo passando uma string com o conteúdo
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

    // Método que encapsula a leitura das linhas de um arquivo, retornando uma lista
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

   static public void animateText(String text) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.print("\r" + text);
            Thread.sleep(500);
            System.out.print("\r" + " ".repeat(text.length()));
            Thread.sleep(500);
        }
    }

}
