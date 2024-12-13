package prueba;

import java.sql.*;
import com.zaxxer.hikari.*;

public class ej1 {

	public static void main(String[] args) throws SQLException {
		
		HikariConfig ds = new HikariDataSource();
		String basedatos = "dbeventos";
		String host = "localhost";
		String port = "3306";
		String parAdic = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDateTimeCode=false&serverTimezone=UTC";
		String urlConnection = "jdbc:mysql://" + host + ":" + port + "/" + basedatos + parAdic;
		String user = "root";
		String pwd = "admin";
		
		ds.setJdbcUrl(urlConnection);
		ds.setUsername(user);
		ds.setPassword(pwd);
		
		try (HikariDataSource dataSource = new HikariDataSource(ds)) {
            // Conectar y ejecutar consulta
			String consultaSQL = "SELECT e.nombre_evento AS Evento, COUNT(ae.dni) AS Asistentes, u.nombre AS Ubicación, u.direccion AS Dirección "
					+ "FROM eventos e INNER JOIN ubicaciones u ON e.id_ubicacion = u.id_ubicacion "
					+ "LEFT JOIN asistentes_eventos ae ON e.id_evento = ae.id_evento "
					+ "GROUP BY e.id_evento, e.nombre_evento, u.nombre, u.direccion "
					+ "ORDER BY e.nombre_evento DESC;";

	            // Conectar y ejecutar consulta
	            try (Connection conexion = dataSource.getConnection();
	                 Statement stmt = conexion.createStatement();
	                 ResultSet rs = stmt.executeQuery(consultaSQL)) {

	                // Mostrar los datos recuperados
	                System.out.printf("%-30s   %-12s   %-35s   %-30s\n", "| Evento", "| Asistentes", "| Ubicación", "| Dirección");
	                System.out.println("-----------------------------------------------------------------------------------------------------------------");

	                while (rs.next()) {
	                    String evento = rs.getString("Evento");
	                    int asistentes = rs.getInt("Asistentes");
	                    String ubicacion = rs.getString("Ubicación");
	                    String direccion = rs.getString("Dirección");


	                    // Imprimir cada fila
	                    System.out.printf("| %-30s | %-12d | %-35s | %-30s\n", evento, asistentes, ubicacion, direccion);
	                }
	            }
	        } catch (Exception e) {
	            System.out.println("Ocurrió un error al conectarse a la base de datos.");
	            e.printStackTrace();
	        }
	}
}
