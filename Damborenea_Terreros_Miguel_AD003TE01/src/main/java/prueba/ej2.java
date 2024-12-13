package prueba;

import java.sql.*;
import java.util.Scanner;
import com.zaxxer.hikari.*;

public class ej2 {
	
	public static void main(String[] args) throws SQLException {

        // Configuración de la conexión a la base de datos
        HikariConfig ds = new HikariConfig();
        String basedatos = "dbeventos";
        String host = "localhost";
        String port = "3306";
        String parAdic = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDateTimeCode=false&serverTimezone=UTC";
        String urlConnection = "jdbc:mysql://" + host + ":" + port + "/" + basedatos + parAdic;
        String user = "root";
        String pwd = "mdt212001";

        ds.setJdbcUrl(urlConnection);
        ds.setUsername(user);
        ds.setPassword(pwd);

        try (HikariDataSource dataSource = new HikariDataSource(ds)) {

            // Solicitar al usuario el nombre de la ubicación
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introduce el nombre de la ubicación: ");
            String nombreUbicacion = scanner.nextLine();

            // Consulta para verificar si la ubicación existe
            String consultaUbicacion = "SELECT id_ubicacion, nombre, capacidad FROM ubicaciones WHERE nombre = ?";
            try (Connection conexion = dataSource.getConnection();
                 PreparedStatement stmt = conexion.prepareStatement(consultaUbicacion)) {

                stmt.setString(1, nombreUbicacion);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Mostrar la capacidad actual
                    int idUbicacion = rs.getInt("id_ubicacion");
                    String nombre = rs.getString("nombre");
                    int capacidadActual = rs.getInt("capacidad");

                    System.out.println("La capacidad actual de la ubicación '" + nombre + "' es: " + capacidadActual);

                    // Solicitar al usuario la nueva capacidad máxima
                    System.out.print("Introduce la nueva capacidad máxima: ");
                    int nuevaCapacidad = scanner.nextInt();

                    // Actualizar la capacidad máxima en la base de datos
                    String actualizarCapacidad = "UPDATE ubicaciones SET capacidad = ? WHERE id_ubicacion = ?";
                    try (PreparedStatement updateStmt = conexion.prepareStatement(actualizarCapacidad)) {
                        updateStmt.setInt(1, nuevaCapacidad);
                        updateStmt.setInt(2, idUbicacion);
                        int filasActualizadas = updateStmt.executeUpdate();

                        if (filasActualizadas > 0) {
                            System.out.println("La capacidad máxima ha sido actualizada exitosamente.");
                        } else {
                            System.out.println("Ocurrió un error al actualizar la capacidad.");
                        }
                    }
                } else {
                    // Si la ubicación no existe
                    System.out.println("La ubicación introducida no existe en la base de datos.");
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al conectarse a la base de datos.");
            e.printStackTrace();
        }
    }
}
