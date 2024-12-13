package prueba;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import com.zaxxer.hikari.*;

public class ej3 {
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

               // Solicitar DNI
               String dni;
               while (true) {
                   System.out.println("Introduce el DNI del asistente:");
                   dni = scanner.nextLine();

                   // Verificar formato de DNI
                   if (Pattern.matches("^[0-9]{8}[A-Za-z]$", dni)) {
                       break;
                   } else {
                       System.out.println("Formato de DNI inválido.");
                   }
               }

               // Verificar si el asistente ya existe en la base de datos
               String consultaAsistente = "SELECT nombre FROM asistentes WHERE dni = ?";
               boolean asistenteExiste = false;
               String nombreAsistente = null;

               try (PreparedStatement stmtAsistente = conexion.prepareStatement(consultaAsistente)) {
                   stmtAsistente.setString(1, dni);
                   ResultSet rsAsistente = stmtAsistente.executeQuery();

                   if (rsAsistente.next()) {
                       asistenteExiste = true;
                       nombreAsistente = rsAsistente.getString("nombre");
                   }
               }

               // Si el asistente no existe, pedir el nombre y crearlo
               if (!asistenteExiste) {
                   System.out.println("No se encontró un asistente con el DNI proporcionado.");
                   System.out.println("Introduce el nombre del asistente:");
                   nombreAsistente = scanner.nextLine();

                   String insertarAsistente = "INSERT INTO asistentes (dni, nombre) VALUES (?, ?)";
                   try (PreparedStatement stmtInsertar = conexion.prepareStatement(insertarAsistente)) {
                       stmtInsertar.setString(1, dni);
                       stmtInsertar.setString(2, nombreAsistente);
                       stmtInsertar.executeUpdate();
                   }
               }

               // Mostrar mensaje personalizado con el nombre del asistente
               System.out.println("Estás realizando la reserva para: " + nombreAsistente);
               System.out.println("Lista de eventos:");
               String consultaEventos = "SELECT e.id_evento, e.nombre_evento, u.capacidad, COUNT(ae.dni) AS asistentes_actuales " +
                                        "FROM eventos e " +
                                        "INNER JOIN ubicaciones u ON e.id_ubicacion = u.id_ubicacion " +
                                        "LEFT JOIN asistentes_eventos ae ON e.id_evento = ae.id_evento " +
                                        "GROUP BY e.id_evento, e.nombre_evento, u.capacidad";

               try (Statement stmt = conexion.createStatement();
                    ResultSet rs = stmt.executeQuery(consultaEventos)) {

                   while (rs.next()) {
                       int idEvento = rs.getInt("id_evento");
                       String nombreEvento = rs.getString("nombre_evento");
                       int capacidadMaxima = rs.getInt("capacidad");
                       int asistentesActuales = rs.getInt("asistentes_actuales");

                       System.out.printf("%d. %s - Espacios disponibles: %d\n", idEvento, nombreEvento, capacidadMaxima - asistentesActuales);
                   }
               }

               // Solicitar al usuario seleccionar un evento
               System.out.println("Elige el número del evento al que quiere asistir: ");
               int idEventoSeleccionado = scanner.nextInt();

               // Verificar si el evento tiene capacidad
               String consultaCapacidad = "SELECT u.capacidad, COUNT(ae.dni) AS asistentes_actuales " +
                                           "FROM eventos e " +
                                           "INNER JOIN ubicaciones u ON e.id_ubicacion = u.id_ubicacion " +
                                           "LEFT JOIN asistentes_eventos ae ON e.id_evento = ae.id_evento " +
                                           "WHERE e.id_evento = ? " +
                                           "GROUP BY u.capacidad";

               try (PreparedStatement stmtCapacidad = conexion.prepareStatement(consultaCapacidad)) {
                   stmtCapacidad.setInt(1, idEventoSeleccionado);
                   ResultSet rsCapacidad = stmtCapacidad.executeQuery();

                   if (rsCapacidad.next()) {
                       int capacidadMaxima = rsCapacidad.getInt("capacidad");
                       int asistentesActuales = rsCapacidad.getInt("asistentes_actuales");

                       if (asistentesActuales >= capacidadMaxima) {
                           System.out.println("Este evento ya alcanzó su capacidad máxima. No se puede registrar.");
                           return;
                       }
                   } else {
                       System.out.println("El evento seleccionado no existe.");
                       return;
                   }
               }

               // Registrar al asistente en el evento
               String registrarEvento = "INSERT INTO asistentes_eventos (dni, id_evento) VALUES (?, ?)";
               try (PreparedStatement stmtRegistrar = conexion.prepareStatement(registrarEvento)) {
                   stmtRegistrar.setString(1, dni);
                   stmtRegistrar.setInt(2, idEventoSeleccionado);
                   stmtRegistrar.executeUpdate();

                   System.out.println(nombreAsistente + " ha sido registrado para el evento seleccionado.");
               }
               scanner.close();
           } catch (Exception e) {
               System.out.println("Ocurrió un error al registrar al asistente.");
               e.printStackTrace();
           }
    }
}
