package prueba;

import java.sql.*;
import java.util.Scanner;
import com.zaxxer.hikari.*;

public class ej4 {
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

        try (HikariDataSource dataSource = new HikariDataSource(ds);
             Connection conexion = dataSource.getConnection()) {

            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Lista de eventos:");
            String consultaEventos = "SELECT id_evento, nombre_evento FROM eventos";

            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(consultaEventos)) {

                while (rs.next()) {
                    int idEvento = rs.getInt("id_evento");
                    String nombreEvento = rs.getString("nombre_evento");

                    System.out.printf("%d. %s\n", idEvento, nombreEvento);
                }
            }

            // Solicitar al usuario el código del evento
            System.out.println("\nIngrese el ID del evento para consultar la cantidad de asistentes:");
            int codigoEvento = scanner.nextInt();

            // Llamar a la función almacenada obtener_numero_asistentes
            String llamarFuncion = "{ ? = CALL obtener_numero_asistentes(?) }";

            try (CallableStatement stmt = conexion.prepareCall(llamarFuncion)) {

                // Registrar el primer parámetro como salida (resultado de la función)
                stmt.registerOutParameter(1, Types.INTEGER);

                // Establecer el parámetro de entrada con el código del evento
                stmt.setInt(2, codigoEvento);

                // Ejecutar la función almacenada
                stmt.execute();

                // Obtener el resultado de la función
                int numeroAsistentes = stmt.getInt(1);

                // Mostrar el resultado al usuario
                System.out.println("El número de asistentes para el evento seleccionado es: " + numeroAsistentes);
            }
            scanner.close();
            
        } catch (Exception e) {
            System.out.println("Ocurrió un error al consultar los asistentes.");
            e.printStackTrace();
        }
    }
}
