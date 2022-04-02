import java.sql.*;
public class initialiseSystem {
    static final String DB_URL = "jdbc:mysql://a.eren@dijkstra.ug.bcc.bilkent.edu.tr/a_eren";
    static final String USER = "a.eren";
    static final String PASS = "Pr7GvLN9";

    public static void main(String[] args) {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); Statement statement = conn.createStatement()) {
            System.out.println("Succesfull connection");

            //Remove previously created tables if any exists

            statement.executeUpdate("DROP TABLE IF EXISTS Apply, Student, Company");

            //Creating the Student table
            String createStudent = "CREATE TABLE Student " +
                    "(sid CHAR(12), " +
                    " sname VARCHAR(50), " +
                    " bdate DATE, " +
                    " address VARCHAR(50)," +
                    " scity VARCHAR(20)," +
                    " year CHAR(20)," +
                    " gpa FLOAT," +
                    " nationality VARCHAR(20)," +
                    " PRIMARY KEY ( sid ))" +
                    " ENGINE = InnoDB;";
            statement.executeUpdate(createStudent);
            System.out.println("Student table is created successfully");

            //Creating the Company table
            String createCompany = "CREATE TABLE Company " +
                    "(cid CHAR(8), "+
                    " cname VARCHAR(20), "+
                    " quota INT, "+
                    " gpathreshold FLOAT, "+
                    " PRIMARY KEY ( cid ))" +
                    " ENGINE = InnoDB;";
            statement.executeUpdate(createCompany);
            System.out.println("Company table is created successfully");

            //Creating the Apply table
            String createApply = "CREATE TABLE Apply " +
                    "(sid CHAR(12), "+
                    " cid CHAR(8), "+
                    " PRIMARY KEY ( sid, cid )," +
                    " FOREIGN KEY(sid) REFERENCES Student(sid)," +
                    " FOREIGN KEY(cid) REFERENCES Company(cid))" +
                    " ENGINE = InnoDB;";
            statement.executeUpdate(createApply);
            System.out.println("Apply table is created successfully");

            //Populating the Student table
            String insertStudents = "INSERT INTO Student VALUES " +
                    "(21000001, 'Marco', '1998-05-31', 'Strobelallee' , 'Dortmund', 'senior', 2.64, 'DE')," +
                    "(21000002, 'Arif', '2001-11-17', 'Nisantasi', 'Istanbul', 'junior', 3.86, 'TC')," +
                    "(21000003, 'Veli', '2003-02-19', 'Cayyolu', 'Ankara', 'freshman', 2.51, 'TC')," +
                    "(21000004, 'Ayse', '2003-05-01', 'Tunali', 'Ankara', 'freshman', 2.52, 'TC');";
            statement.executeUpdate(insertStudents);
            System.out.println("Student table is populated successfully");

            //Populating the Company table
            String insertCompanies = "INSERT INTO Company VALUES " +
                    "('C101', 'milsoft', 3, 2.50)," +
                    "('C102', 'merkez bankasi', 10, 2.45), " +
                    "('C103', 'tubitak', 2, 3.00), " +
                    "('C104', 'havelsan', 5, 2.00), " +
                    "('C105', 'aselsan', 4, 2.50), " +
                    "('C106', 'tai', 2, 2.20), " +
                    "('C107', 'amazon', 1, 3.85); ";
            statement.executeUpdate(insertCompanies);
            System.out.println("Company table is populated successfully");

            //Populating the Apply table
            String insertApplications = "INSERT INTO Apply VALUES " +
                    "(21000001, 'C101'), " +
                    "(21000001, 'C102'), " +
                    "(21000001, 'C104'), " +
                    "(21000002, 'C107'), " +
                    "(21000003, 'C104'), " +
                    "(21000003, 'C106'), " +
                    "(21000004, 'C102'), " +
                    "(21000004, 'C106'); ";
            statement.executeUpdate(insertApplications);
            System.out.println("Apply table is populated successfully");

            //Executing the queries asked in Part 1
            //1. Give the names of the students who applied 3 companies for internships.
            String query = "SELECT s.sname FROM Student AS s NATURAL JOIN Apply AS a GROUP BY s.sid HAVING COUNT(sid) = 3;";
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("1. Give the names of the students who applied 3 companies for internships: ");
            while (resultSet.next()){
                String sname = resultSet.getString("sname");
                System.out.println("Student Name = " + sname);
            }

            //2. Give the sum of the quotas of the companies which are applied by the student having the most applications.
            query = "SELECT SUM(y.quota) AS sum_quota FROM Company AS y NATURAL JOIN Apply as x WHERE x.sid IN " +
                    "(SELECT a.sid FROM Apply AS a GROUP BY a.sid " +
                    " HAVING COUNT(a.sid) = (SELECT MAX(c.cnt) FROM " +
                    "(SELECT b.sid, COUNT(b.sid) AS cnt FROM Apply AS b GROUP BY b.sid) AS c));";
            resultSet = statement.executeQuery(query);
            System.out.println("2. Give the sum of the quotas of the companies which are applied by the student having the most applications: ");
            while (resultSet.next()){
                int sum_quota = resultSet.getInt("sum_quota");
                System.out.println("Sum of quotas = "+sum_quota);
            }

            //3. Give the average number of applications of students by each nationality.
            query = "SELECT x.nationality, AVG(x.app_count) AS avg_app_count FROM " +
                    "(SELECT s.nationality, COUNT(a.sid) AS app_count FROM Student AS s NATURAL JOIN Apply AS a GROUP BY s.sid) " +
                    "AS x GROUP BY x.nationality;";
            resultSet = statement.executeQuery(query);
            System.out.println("3. Give the average number of applications of students by each nationality: ");
            while (resultSet.next()){
                String nationality = resultSet.getString("nationality");
                Float avg_app_count = resultSet.getFloat("avg_app_count");
                System.out.println("Nationality: " + nationality + "    Average application count: " + avg_app_count);
            }

            //4. Give the name of the companies which are applied by all students from the freshman year.
            query = "SELECT DISTINCT c.cname FROM Student AS s NATURAL JOIN Apply AS a NATURAL JOIN Company AS c WHERE s.year = 'freshman';";
            resultSet = statement.executeQuery(query);
            System.out.println("4. Give the name of the companies which are applied by all students from the freshman year: ");
            while (resultSet.next()){
                String cname = resultSet.getString("cname");
                System.out.println("Company Name: " + cname);
            }
            //5. For each company, give the average gpa of applied students.
            query = "SELECT c.cname, AVG(s.gpa) AS avg_app_gpa FROM Student AS s NATURAL JOIN Apply " +
                    "AS a NATURAL JOIN Company AS c GROUP BY c.cname;";
            resultSet = statement.executeQuery(query);
            System.out.println("5. For each company, give the average gpa of applied students: ");
            while (resultSet.next()){
                String cname = resultSet.getString("cname");
                Float avg_app_gpa = resultSet.getFloat("avg_app_gpa");
                System.out.println("Company Name: " + cname + "     Average applicant gpa: " + avg_app_gpa);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
