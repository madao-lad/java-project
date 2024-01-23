

import jdk.internal.icu.impl.CharacterIteratorWrapper;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "2581421@Ap";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver" );
//        } catch( ClassNotFoundException e ) {
//            System.out.println( e.getMessage() );
//        }

        try {
            Connection connection = DriverManager.getConnection( url, username, password );
            while( true ) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner in = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. UpdatHotelReservationSysteme Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                int choice = in.nextInt();
                switch( choice ) {
                    case 1 -> reserveRoom(connection, in);
                    case 2 -> viewReservation(connection);
                    case 3 -> getRoomNumber(connection, in);
                    case 4 -> updateReservation(connection, in);
                    case 5 -> deleteReservation(connection, in);
                    case 0 -> {
                        exit();
                        return;
                    }
                    default -> System.out.println("Invalid Choice! Try Again.");
                }
            }
        } catch ( SQLException e ) {
            System.out.println( e.getMessage() );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( e );
        }
    }

    private static void reserveRoom(Connection connection, Scanner in ) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = in.next();
            in.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = in.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = in.next();
            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number )" +
                         "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if( affectedRows > 0 ) {
                    System.out.println("Reservation Successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try (Statement statement = connection.createStatement();
             ResultSet resultset = statement.executeQuery(sql)) {

            System.out.println("Current Reservations: ");
            System.out.println("+----------------+----------------+----------------+----------------+-------------------------------+");
            System.out.println("| Reservation ID | Guest          | Room Number    | Contact NUmber | Reservation Date              |");
            System.out.println("+----------------+----------------+----------------+----------------+-------------------------------+");


            while(resultset.next()) {
                int reservationId = resultset.getInt("reservation_id");
                String guestName = resultset.getString("guest_name");
                int roomNumber = resultset.getInt("room_number");
                String contactNumber = resultset.getString("contact_number");
                String reservationDate = resultset.getTimestamp("reservation_date").toString();

                System.out.printf("| %-16d | %-16s | %-16d | %-16s | %-31s |\n", reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+----------------+----------------+----------------+-------------------------------+");
        }
    }

    private static void getRoomNumber( Connection connection, Scanner in ) {
        try {
            System.out.println("Enter reservation ID: ");
            int reservationId = in.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = in.next();

            String sql = "SELECT room_number FROM reservations " +
                         "WHERE reservation_id = " + reservationId +
                         " AND guest_name = '" + guestName + "'";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if(resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                                       " and Guest " + guestName + " is: " + roomNumber);
                } else {
                         System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner in ) {
        try {
            System.out.println("Enter reservation ID to update: ");
            int reservationId = in.nextInt();
            in.nextLine();

            if(!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = in.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = in.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = in.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                         "room_number = " +  newRoomNumber + ", " +
                         "contact_number = '" + newContactNumber + "' " +
                         "WHERE reservation_id = " + reservationId;
            try (Statement statements = connection.createStatement()) {
                int affectedRows = statements.executeUpdate(sql);

                if(affectedRows > 0) {
                    System.out.println("Reservation updated successfully");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner in) {
        try {
            System.out.println("Enter reservation_ID to delete: ");
            int reservationId = in.nextInt();

            if(!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try ( Statement statement = connection.createStatement() ) {
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0) { System.out.println("Reservation deleted successfully");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId ) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next();
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i != 0) {
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank You for using Hotel reservation system!");
    }
}