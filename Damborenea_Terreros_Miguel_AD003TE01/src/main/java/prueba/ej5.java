package prueba;

import org.vibur.dbcp.ViburDBCPDataSource;
import java.sql.Connection;

public class ej5 {
    public static void main(String[] args) {
        // Crear configuración del DataSource
        ViburDBCPDataSource dataSource = new ViburDBCPDataSource();
        dataSource.setJdbcUrl("jdbc:hsqldb:file:db/miBaseDeDatos");
        dataSource.setUsername("SA"); // Usuario por defecto de HSQLDB
        dataSource.setPassword("");  // Contraseña vacía por defecto
        dataSource.start();

        try (Connection connection = dataSource.getConnection()) {
            // Verificar conexión
        	System.out.println("connection.isValid(0) = " + connection.isValid(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataSource.close();
    }
}