package SQLRepositories;

import java.sql.*;

import Exceptions.DBException.DBException;
import Exceptions.DBException.DriverException;

/**
 * Classe responsável por realizar a comunicação com o banco de dados via JDBC
 */
public class DBCom {
    public Connection con; // Onde a conexão será estabelecida
    public Statement smt; // Onde as pesquisas vão ser passadas
    public ResultSet rs; // O resultado obtido pelo smt
    private static String driver = "org.postgresql.Driver"; // Localização do driver
    private static String user = "postgres"; // Usuário do banco de dados
    private static String password = "admin"; // Senha do banco de dados (No meu caso admin)
    private static String url = "jdbc:postgresql://localhost:5432/livbook"; // Localização do banco de dados

    public void connect() throws DBException {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new DriverException("Driver não encontrado!", e);
        } catch (SQLException e) {
            throw new DBException("Erro ao conectar com o banco de dados: " + e.getMessage(), e);
        }
    }

    void close() throws DBException{
        try {
            con.close();
        } catch (SQLException e) {
            throw new DBException("Erro ao fechar conexão com o banco de dados: " + e.getMessage(), e);
        }

    }

    public static void main(String[] args) {
        DBCom db = new DBCom();
        try {
            db.connect();
            System.out.println("Conectado!");
            db.close();
        } catch (DBException e) {
            System.out.println(e.getMessage());
        }
    }


}
