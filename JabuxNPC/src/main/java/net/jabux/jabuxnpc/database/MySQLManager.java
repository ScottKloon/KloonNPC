package net.jabux.jabuxnpc.database;

import net.jabux.jabuxnpc.JabuxNPC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLManager {
    private JabuxNPC plugin;
    private Connection connection;

    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    public MySQLManager(JabuxNPC plugin) {
        this.plugin = plugin;
        this.host = plugin.getConfig().getString("mysql.host");
        this.database = plugin.getConfig().getString("mysql.database");
        this.username = plugin.getConfig().getString("mysql.username");
        this.password = plugin.getConfig().getString("mysql.password");
        this.port = plugin.getConfig().getInt("mysql.port");

        connect();
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database,
                    username,
                    password
            );
            plugin.getLogger().info("Conectado ao banco de dados MySQL!");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Não foi possível conectar ao banco de dados MySQL!");
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
