import java.sql.*;
import java.util.Scanner;

public class Lab4 {
    static final String DB_URL = "jdbc:mysql://localhost:3306/Lab_4";
    static final String USER = "root";
    static final String PASSWORD = "night100";
    

    public static void main(String[] args) throws Exception {
        Connection conn;
        Statement stat;
        Scanner scan = new Scanner(System.in);
        String input = "";
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to the mySQL Server successfully.");

            stat = conn.createStatement();
            //while loop to make sure commands continue until quit
            while(true) {
                System.out.println();
                displayMenu();
                System.out.print("Command: ");
                scan = new Scanner(System.in);
                input = scan.nextLine();
                switch (input.trim()) { //switch statements for each function made
                    case "1":
                        displayTripSchedule(stat);
                        break;
                    case "2":
                        deleteTripOffering(stat);
                        break;
                    case "3":
                        addTripOffering(stat);
                        break;
                    case "4":
                        changeDriver(stat);
                        break;
                    case "5":
                        changeBus(stat);
                        break;
                    case "6":
                        displayTripStops(stat);
                        break;
                    case "7":
                        displayWeeklyDriverSchedule(stat);
                        break;
                    case "8":
                        addDriver(stat);
                        break;
                    case "9":
                        addBus(stat);
                        break;
                    case "10":
                        deleteBus(stat);
                        break;
                    case "11":
                        insertTripData(stat);
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    default:
                        displayMenu();
                        break;
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        } 
        scan.close();

    }

    //menu so people know what commands they can use and what they can do
    private static void displayMenu() {
        System.out.println("1:  Display Schedule from Starting City to Destination on Specified Date:");
        System.out.println("2:  Delete a Trip Offering");
        System.out.println("3:  Add a Trip Offering");
        System.out.println("4:  Change a Driver");
        System.out.println("5:  Change a Bus");
        System.out.println("6:  Display Trip Stops of a Provided Trip");
        System.out.println("7:  Display Weekly Schedule for Driver");
        System.out.println("8:  Add a Driver");
        System.out.println("9:  Add a Bus");
        System.out.println("10: Delete a Bus");
        System.out.println("11: Insert Actual Trip Info");

        System.out.println("q: Exit program");
    }

    //displays the Trip Schedule from a start location to an end location on a specified date
    public static void displayTripSchedule(Statement statement) {
        Scanner input = new Scanner(System.in);
        System.out.print("Please input the start location name: ");
        String startLocationName = input.nextLine();
        System.out.print("Please input the destination name: ");
        String destinationName = input.nextLine();
        System.out.print("Please input the date month/day/year: ex: YYYY-MM-DD: ");
        String date = input.nextLine();
       
        try {
            ResultSet result = statement
                    .executeQuery("SELECT S.ScheduledStartTime, S.ScheduledArrivalTime, S.DriverName, S.BusID " +
                            "FROM TripOffering S, Trip T " +
                            "WHERE T.StartLocationName LIKE '" + startLocationName + "' AND " +
                            "T.DestinationName LIKE '" + destinationName + "' AND " +
                            "S.Date = '" + date + "' AND " +
                            "T.TripNumber = S.TripNumber " +
                            "Order by ScheduledStartTime ");

            ResultSetMetaData data = result.getMetaData();
            int cols = data.getColumnCount();

            System.out.println();

            while (result.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.println(String.format("%-20s: %s", data.getColumnName(i), result.getString(i)));
                }
            }

            result.close();
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    //deletes a trip offering made
    public static void deleteTripOffering(Statement statement) {
        Scanner input = new Scanner(System.in);
        System.out.print("Please input the trip number: ");
        String tripNumber = input.nextLine();
        System.out.print("Please input the date: ");
        String date = input.nextLine();
        System.out.print("Please input the scheduled start time: ex: 13:00: ");
        String scheduledStartTime = input.nextLine();
        System.out.println(scheduledStartTime);
        try {
            if (statement.executeUpdate("DELETE FROM TripOffering " +
            "WHERE TripNumber = '" + tripNumber + "' AND " +
            "Date = '" + date + "' AND " +
            "ScheduledStartTime = '" + scheduledStartTime + "'") != 0) {
            System.out.println("Deleted Trip Offering with Trip Number: " + tripNumber + " on " + date
                    + " starting at " + scheduledStartTime);
        } 
        else {
            System.out.println(
                "Trip Offering with Trip Number: " + tripNumber 
                + " on " + date + " starting at " + scheduledStartTime
                + " does not exist.");
        }

        } catch(SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    //creates a new trip offering
    public static void addTripOffering(Statement statement) {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print("Enter Trip Number: ");
            String tripNumber = input.nextLine();
            System.out.print("Date: ");
            String date = input.nextLine();
            System.out.print("Scheduled Start Time: ");
            String scheduledStartTime = input.nextLine();
            System.out.print("Scheduled Arrival Time: ");
            String scheduledArrivalTime = input.nextLine();
            System.out.print("Driver Name: ");
            String driver = input.nextLine();
            System.out.print("Bus ID: ");
            String bus = input.nextLine();

            try {
                statement.execute(
                        "INSERT INTO TripOffering VALUES ('" + tripNumber + "', '" + date + "', '" 
                        + scheduledStartTime + "', '" + scheduledArrivalTime
                         + "', '" + driver + "', '" + bus + "')");
                System.out.println("Added new Trip Offering");
            } catch (SQLException e) {
                System.out.println("Input did not work, please check for any errors.");
            }

            System.out.print("Add another Trip Offering? (y/n): ");
            String input2 = input.nextLine();
            if (input2.charAt(0) == 'y') {
            } else {
                break;
            }
        }
    }

    //changes driver for a trip
    public static void changeDriver(Statement statement) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.print("New Driver Name: ");
        String driver = input.nextLine();
        System.out.print("Start Trip Number: ");
        String tripNumber = input.nextLine();
        System.out.print("Date: ");
        String date = input.nextLine();
        System.out.print("Scheduled Start Time: ");
        String scheduledStartTime = input.nextLine();

        try {
            if (statement.executeUpdate("UPDATE TripOffering " +
                    "SET DriverName = '" + driver + "' " +
                    "WHERE TripNumber = '" + tripNumber + "' AND " +
                    "Date = '" + date + "' AND " +
                    "ScheduledStartTime = '" + scheduledStartTime + "'") != 0) {
                System.out.println("Updated Driver");
            } else {
                System.out.println("Input did not work, please check for any errors.");
            }
        } catch (SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    //changes the bus used for a trip
    public static void changeBus(Statement statement) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.print("New Bus Number: ");
        String bus = input.nextLine().trim();
        System.out.print("Start Trip Number: ");
        String tripNumber = input.nextLine().trim();
        System.out.print("Date: ");
        String date = input.nextLine().trim();
        System.out.print("Scheduled Start Time: ");
        String scheduledStartTime = input.nextLine().trim();

        try {
            if (statement.executeUpdate("UPDATE TripOffering " +
                    "SET BusID = '" + bus + "' " +
                    "WHERE TripNumber = '" + tripNumber + "' AND " +
                    "Date = '" + date + "' AND " +
                    "ScheduledStartTime = '" + scheduledStartTime + "'") != 0) {
                System.out.println("Updated Bus");
            } else {
                System.out.println("Input did not work, please check for any errors.");
            }

        } catch (SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    //displays the stops on a trip done
    public static void displayTripStops(Statement statement) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.print("Trip Number: ");
        String tripNumber = input.nextLine();
        System.out.println();
        try {
            ResultSet result = statement.executeQuery("SELECT * " +
                    "FROM TripStopInfo " +
                    "WHERE TripNumber = '" + tripNumber + "' " +
                    "Order By SequenceNumber ");
            ResultSetMetaData data = result.getMetaData();
            int cols = data.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                System.out.print(data.getColumnName(i) + "\t");
            }
            System.out.println();

            while (result.next()) {
                for (int i = 1; i <= cols; i++)
                    System.out.print(result.getString(i) + "\t\t");
                System.out.println();
            }
            result.close();
            System.out.println();
        } catch (SQLException msg) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    //displays schedule of bus drivers in specified dates
    public static void displayWeeklyDriverSchedule(Statement statement) {
        Scanner input = new Scanner(System.in);
        System.out.print("Driver name: ");
        String driver = input.nextLine();
        System.out.print("Please input the date of the start of the week: ");
        String startDate = input.nextLine();
        System.out.print("Please input the date at the end of the week: ");
        String endDate = input.nextLine();
        System.out.println(startDate+" "+endDate);
        try {
            ResultSet result = statement.executeQuery("SELECT TripNumber, Date, ScheduledStartTime,"
            + " ScheduledArrivalTime, DriverName, BusID"+
            " FROM TripOffering"+
             " WHERE DriverName = '"+driver+
             "' AND Date BETWEEN '"+startDate+"' AND '"+endDate+"'");
             ResultSetMetaData data = result.getMetaData();
            int cols = data.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                System.out.print(data.getColumnName(i) + "\t");
            }
            System.out.println();

            while (result.next()) {
                for (int i = 1; i <= cols; i++)
                    System.out.print(result.getString(i) + "\t\t");
                System.out.println();
            }
            result.close();
            System.out.println();
        } catch(SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    public static void addDriver(Statement statement) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.print("Driver name: ");
        String driver = input.nextLine();
        System.out.print("Phone number: ");
        String phone = input.nextLine();

        try {
            statement.execute("INSERT INTO Driver VALUES ('" + driver + "', '" + phone + "')");
            System.out.println("Added new Driver");
        } catch (SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    public static void addBus(Statement statement) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.print("Bus ID: ");
        String bus = input.nextLine();
        System.out.print("Bus model: ");
        String model = input.nextLine();
        System.out.print("Bus year: ");
        String year = input.nextLine();

        // insert into bus
        try {
            statement.execute("INSERT INTO Bus VALUES ('" + bus + "', '" + model + "', '" + year + "')");
            System.out.println("Added new Bus");
        } catch (SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    public static void deleteBus(Statement statement) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.print("Bus ID: ");
        String bus = input.nextLine().trim();

        try {
            if (statement.executeUpdate("DELETE FROM Bus " +
                    "WHERE BusId = '" + bus + "'") != 0) {
                System.out.println("Deleted bus");
            } else {
                System.out.println("Input did not work, please check for any errors.");
            }
        } catch (SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
    }

    public static void insertTripData(Statement stmt) throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.print("Trip Number: ");
        String tripNumber = input.nextLine();
        System.out.print("Date: ");
        String date = input.nextLine();
        System.out.print("Scheduled Start Time: ");
        String scheduledStartTime = input.nextLine();
        System.out.print("Stop Number: ");
        String stop = input.nextLine();
        System.out.print("Scheduled Arrival Time: ");
        String scheduledArrivalTime = input.nextLine();
        System.out.print("Actual Start Time: ");
        String actualStartTime = input.nextLine();
        System.out.print("Actual Arrival Time: ");
        String actualArrivalTime = input.nextLine();
        System.out.print("Passengers in: ");
        String passengerIn = input.nextLine();
        System.out.print("Passengers out: ");
        String passengerOut = input.nextLine();

        try {
            stmt.execute("INSERT INTO ActualTripStopInfo VALUES ('" + tripNumber + "', '" + date + "', '" + scheduledStartTime
                    + "', '" + stop + "', '" + scheduledArrivalTime
                    + "', '" + actualStartTime + "', '" + actualArrivalTime + "', '" + passengerIn + "', '" + passengerOut
                    + "')");
        } catch (SQLException e) {
            System.out.println("Input did not work, please check for any errors.");
        }
        System.out.println("Inserted Actual Trip Info.");
    }
}

