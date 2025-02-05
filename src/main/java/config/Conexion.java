package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private Connection con;
    private static final String URL = "jdbc:mysql://localhost:3306/prestamos?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CONTRASEÑA = "root";

    public Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
            System.out.println("✅ Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.err.println("�?� Error: No se encontró el driver JDBC.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("�?� Error al conectar a la base de datos.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return con;
    }

    public void cerrarConexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("✅ Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("�?� Error al cerrar la conexión.");
            e.printStackTrace();
        }
    }
}
