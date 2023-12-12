package SQLRepositories;

import java.sql.*;

import Exceptions.DBException.DBException;
import Exceptions.DBException.DriverException;

/**
 * Classe responsável por realizar a comunicação com o banco de dados via JDBC
 */
public class DBCom {
    public Connection con; // Onde a conexão será estabelecida
    private static String driver = "org.postgresql.Driver"; // Localização do driver
    private static String user = "postgres"; // Usuário do banco de dados
    private static String password = "admin"; // Senha do banco de dados (No meu caso admin)
    private static String url = "jdbc:postgresql://localhost:5432/livbook"; // Localização do banco de dados

    /**
     * Método responsável por estabelecer a conexão com o banco de dados
     * @throws DBException
     */
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

    /**
     * Método responsável por fechar a conexão com o banco de dados
     * @throws DBException
     */
    void close() throws DBException {
        try {
            con.close();
        } catch (SQLException e) {
            throw new DBException("Erro ao fechar conexão com o banco de dados: " + e.getMessage(), e);
        }

    }

}
