package application;



import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class Functions<Student> {
	public int cId;
	public int pId;
	 static String jdbcUrl = "jdbc:mysql://localhost:3306/projectoop";
     static String username = "root";
     static String password = "oracle";;
     private  java.sql.Connection connection;
     private int authenticatedStudentID;
    

     public Functions(int authenticatedStudentID, java.sql.Connection connection) {
         this.authenticatedStudentID = authenticatedStudentID;
         this.connection = connection;
     }

     public Functions() {
         
        
     }
     
	 public  void connect() {
    	 try {
			connection = DriverManager.getConnection(jdbcUrl, username, password);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		 if(connection!=null) {
		 
		 
		 } 
     }
     
     public  void disconnect() {
         try {
             if (connection != null && ! connection.isClosed()) {
                  connection.close();
                 
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
     } 
     
     
     public String authenticate(String username, String password) throws SQLException {
    	    Statement stmt = connection.createStatement();
    	    String sql = "SELECT role,PersonID FROM person WHERE Username = '" + username + "' AND Password = '" + password + "'";
    	    ResultSet res = stmt.executeQuery(sql);

    	    String role = null;
    	    

    	    if (res.next()) {
    	        role = res.getString("role");
    	        pId=res.getInt("personId");
    	        System.out.println(pId);
    	    }

    	    return role; 
    	}
     
     public void searchCourse(String courseName, ObservableList<String> data) {
    	    
    	    String query = "SELECT Course.courseID, Course.name AS courseName, Course.credits, Course.instructorID, Person.username AS instructorName " +
    	            "FROM Course " +
    	            "INNER JOIN Person ON Course.instructorID = Person.personID " +
    	            "WHERE Course.name LIKE ?";

    	   
    	        PreparedStatement statement;
				try {
					statement = connection.prepareStatement(query);
				
    	        statement.setString(1, "%" + courseName + "%");
    	        ResultSet resultSet = statement.executeQuery();

    	        while (resultSet.next()) {
    	            cId = resultSet.getInt("courseID");
    	            String name = resultSet.getString("courseName");
    	            int credits = resultSet.getInt("credits");
    	            int instructorId = resultSet.getInt("instructorID");
    	            String instructorName = resultSet.getString("instructorName");

    	            String courseInfo = "Course ID: " + cId + "\n Course Name: " + name +
    	                    "\n Credits: " + credits + "\n Instructor ID: " + instructorId +
    	                    "\n Instructor Name: " + instructorName;
    	            data.add(courseInfo);
    	        } 
    	        
				}catch (SQLException e) {
					e.printStackTrace();
				}
    	    
     }
     

     public List<String> getInstructorNamesFromDatabase() {
         List<String> instructorNames = new ArrayList<>();
        
         String query="SELECT name FROM instructor";

         try (
              PreparedStatement preparedStatement = connection.prepareStatement(query);
              ResultSet resultSet = preparedStatement.executeQuery()) {

             while (resultSet.next()) {
                 String instructorName = resultSet.getString("name");
                 instructorNames.add(instructorName);
             }

         } catch (SQLException e) {
             e.printStackTrace();  
         }

         return instructorNames;
     }
     
     public boolean addCourse(int pId, int courseID) {
    	  
    	    if (isCourseEnrolled(pId, courseID)) {
    	        System.out.println("Course already enrolled for the student.");
    	        return false; 
    	    }

    	    String query = "INSERT INTO Enrollment (studentID, courseID) VALUES (?, ?)";

    	    try {
    	        PreparedStatement statement = connection.prepareStatement(query);
    	        statement.setInt(1, pId);
    	        statement.setInt(2, courseID);

    	        int rowsAffected = statement.executeUpdate();

    	        if (rowsAffected > 0) {
    	            System.out.println("Course added successfully!");
    	            return true; 
    	        } else {
    	            System.out.println("Failed to add course.");
    	            return false; 
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	        return false; 
    	    }
    	}

    	
    	private boolean isCourseEnrolled(int pId, int courseID) {
    	    String query = "SELECT COUNT(*) AS count FROM Enrollment WHERE studentID = ? AND courseID = ?";

    	    try {
    	        PreparedStatement statement = connection.prepareStatement(query);
    	        statement.setInt(1, pId);
    	        statement.setInt(2, courseID);

    	        ResultSet resultSet = statement.executeQuery();
    	        if (resultSet.next()) {
    	            int count = resultSet.getInt("count");
    	            return count > 0; 
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
    	    return false; 
    	}

     
     
     public List<String> viewEnrolledCourses(int pId) {
    	    List<String> enrolledCourses = new ArrayList<>();
    	    String query = "SELECT Course.courseID, Course.name AS courseName, Course.credits, " +
    	            "Person.username AS instructorName " +
    	            "FROM Enrollment " +
    	            "INNER JOIN Course ON Enrollment.courseID = Course.courseID " +
    	            "INNER JOIN Person ON Course.instructorID = Person.personID " +
    	            "WHERE Enrollment.studentID = ?";

    	    try {
    	        PreparedStatement statement = connection.prepareStatement(query);
    	        statement.setInt(1, pId);
    	        ResultSet resultSet = statement.executeQuery();

    	        while (resultSet.next()) {
    	            int courseId = resultSet.getInt("courseID");
    	            String name = resultSet.getString("courseName");
    	            int credits = resultSet.getInt("credits");
    	            String instructorName = resultSet.getString("instructorName");

    	          
    	            String courseDetails = "Course ID: " + courseId +
    	                    ", Course Name: " + name +
    	                    ", Credits: " + credits +
    	                    ", Instructor Name: " + instructorName;

    	            enrolledCourses.add(courseDetails);
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }

    	    return enrolledCourses;
    	}

     public boolean dropCourse(int pId,int courseID) {
         String query = "DELETE FROM Enrollment WHERE studentID = ? AND courseID = ?";

         try {
             PreparedStatement statement = connection.prepareStatement(query);
             statement.setInt(1, pId);
             statement.setInt(2, courseID);

             int rowsAffected = statement.executeUpdate();

             if (rowsAffected > 0) {
                 System.out.println("Course dropped successfully!");
                 return true;
             } else {
                 System.out.println("Failed to drop course.");
                 return false;
             }
         } catch (SQLException e) {
             e.printStackTrace();
             return false; 
         }
     }
     
     public List<String> viewGrades(int pID) {
    	    List<String> enrolledCoursesWithGrades = new ArrayList<>();
    	    String query = "SELECT Course.courseID, Course.name AS courseName, Grades.grade " +
    	                   "FROM Enrollment " +
    	                   "INNER JOIN Course ON Enrollment.courseID = Course.courseID " +
    	                   "LEFT JOIN Grades ON Enrollment.enrollmentID = Grades.enrollmentID " +
    	                   "WHERE Enrollment.studentID = ?";

    	    try {
    	        PreparedStatement statement = connection.prepareStatement(query);
    	        statement.setInt(1, pID);
    	        ResultSet resultSet = statement.executeQuery();

    	        while (resultSet.next()) {
    	            int courseId = resultSet.getInt("courseID");
    	            String name = resultSet.getString("courseName");
    	            int grade = resultSet.getInt("grade");

    	            String courseDetails;
    	            if (resultSet.wasNull()) {
    	                courseDetails = "Course ID: " + courseId +
    	                                ", Course Name: " + name +
    	                                ", Grade: N/A";
    	            } else {
    	                courseDetails = "Course ID: " + courseId +
    	                                ", Course Name: " + name +
    	                                ", Grade: " + grade;
    	            }

    	            enrolledCoursesWithGrades.add(courseDetails);
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }

    	    return enrolledCoursesWithGrades;
    	}

     
     public List<String> viewAttendance(int pId) {
    	    List<String> attendanceDetails = new ArrayList<>();
    	    String query = "SELECT Course.courseID, Course.name AS courseName, Attendance.attendanceDate, Attendance.status " +
    	                   "FROM Enrollment " +
    	                   "INNER JOIN Course ON Enrollment.courseID = Course.courseID " +
    	                   "INNER JOIN Attendance ON Enrollment.studentID = Attendance.studentID AND Enrollment.courseID = Attendance.courseID " +
    	                   "WHERE Enrollment.studentID = ?";

    	    try {
    	        PreparedStatement statement = connection.prepareStatement(query);
    	        statement.setInt(1, pId);
    	        ResultSet resultSet = statement.executeQuery();

    	        while (resultSet.next()) {
    	            int courseId = resultSet.getInt("courseID");
    	            String courseName = resultSet.getString("courseName");
    	            String attendanceDate = resultSet.getString("attendanceDate");
    	            String status = resultSet.getString("status");

    	        
    	            String attendanceInfo = "Course ID: " + courseId +
    	                                    ", Course Name: " + courseName +
    	                                    ", Attendance Date: " + attendanceDate +
    	                                    ", Status: " + status;

    	            attendanceDetails.add(attendanceInfo);
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }

    	    return attendanceDetails;
    	}

     public List<String> viewPayments(int pId) {
    	    List<String> paymentDetails = new ArrayList<>();
    	    String query = "SELECT paymentID, studentID, paymentDate, amount " +
    	                   "FROM Payment " +
    	                   "WHERE studentID = ?";

    	    try {
    	        PreparedStatement statement = connection.prepareStatement(query);
    	        statement.setInt(1, pId);
    	        ResultSet resultSet = statement.executeQuery();

    	        while (resultSet.next()) {
    	            int paymentID = resultSet.getInt("paymentID");
    	            int studentID = resultSet.getInt("studentID");
    	            String paymentDate = resultSet.getString("paymentDate");
    	            double amount = resultSet.getDouble("amount");

    	          
    	            String paymentInfo = "Payment ID: " + paymentID +
    	                                 ", Student ID: " + studentID +
    	                                 ", Payment Date: " + paymentDate +
    	                                 ", Amount: " + amount;

    	            paymentDetails.add(paymentInfo);
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }

    	    return paymentDetails;
    	}

     
     public boolean updateStudentPassword(int pId,String newPassword) {
    	    String query = "UPDATE Person SET password = ? WHERE personID = ? AND role = 'student'";

    	    try {
    	        PreparedStatement statement = connection.prepareStatement(query);
    	        statement.setString(1, newPassword);
    	        statement.setInt(2, pId);

    	        int rowsAffected = statement.executeUpdate();

    	        if (rowsAffected > 0) {
    	            System.out.println("Password updated successfully!");
    	            return true; 
    	        } else {
    	            System.out.println("Failed to update password.");
    	            return false; 
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	        return false; 
    	    }
    	}

     public void handleAddStudent(String username, String password, String email) {
    	    try {
    	       
    	        String insertPersonQuery = "INSERT INTO Person (username, password, email, role) VALUES (?, ?, ?, 'student')";
    	        try (PreparedStatement personStatement = connection.prepareStatement(insertPersonQuery, Statement.RETURN_GENERATED_KEYS)) {
    	            personStatement.setString(1, username);
    	            personStatement.setString(2, password);
    	            personStatement.setString(3, email);

    	            int rowsAffected = personStatement.executeUpdate();

    	            if (rowsAffected > 0) {
    	            
    	                try (ResultSet generatedKeys = personStatement.getGeneratedKeys()) {
    	                    if (generatedKeys.next()) {
    	                        int personID = generatedKeys.getInt(1);

    	                       
    	                        String insertStudentQuery = "INSERT INTO Student (studentID, name, password, email) VALUES (?, ?, ?, ?)";
    	                        try (PreparedStatement studentStatement = connection.prepareStatement(insertStudentQuery)) {
    	                            studentStatement.setInt(1, personID);
    	                          
    	                            studentStatement.setString(2, username);
    	                            studentStatement.setString(3, password);
    	                            studentStatement.setString(4, email);

    	                            int studentRowsAffected = studentStatement.executeUpdate();

    	                            if (studentRowsAffected > 0) {
    	                                System.out.println("Student added successfully.");
    	                            } else {
    	                                System.out.println("Failed to add student.");
    	                            }
    	                        }
    	                    }
    	                }
    	            } else {
    	                System.out.println("Failed to add student.");
    	            }
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
     }
    	    
    	    
    	    
    	   


     
     public void addPayment(int studentID, String paymentDateStr) throws ParseException {
    	    try {
    	      
    	        int totalCredits = getTotalEnrolledCredits(studentID);

    	        if (totalCredits > 0) {
    	           
    	            double paymentAmount = 135 * totalCredits;
    	            
    	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	             Date parsedDate = dateFormat.parse(paymentDateStr);
    	             java.sql.Date paymentDate = new java.sql.Date(parsedDate.getTime());


    	           
    	            String sql = "INSERT INTO Payment (studentID, paymentDate, amount) VALUES (?, ?, ?)";
    	            try (PreparedStatement statement = connection.prepareStatement(sql)) {
    	                statement.setInt(1, studentID);

    	                statement.setDate(2, paymentDate);

    	                statement.setDouble(3, paymentAmount);

    	                int rowsAffected = statement.executeUpdate();

    	                if (rowsAffected > 0) {
    	                    System.out.println("Payment added successfully. Amount: $" + paymentAmount);
    	                } else {
    	                    System.out.println("Failed to add payment.");
    	                }
    	            }
    	        } else {
    	            System.out.println("No enrolled courses found for the student.");
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
    	}

    	private int getTotalEnrolledCredits(int studentID) throws SQLException {
    	   
    	    String query = "SELECT SUM(credits) AS totalCredits FROM Enrollment JOIN Course ON Enrollment.courseID = Course.courseID WHERE studentID = ?";
    	    try (PreparedStatement statement = connection.prepareStatement(query)) {
    	        statement.setInt(1, studentID);
    	        try (ResultSet resultSet = statement.executeQuery()) {
    	            if (resultSet.next()) {
    	                return resultSet.getInt("totalCredits");
    	            }
    	        }
    	    }
    	    return 0; 
    	}

     
    	 public double calculatePaymentAmount(int studentID) {
    	        try {
    	          
    	            int totalCredits = getTotalEnrolledCredits(studentID);

    	            
    	            double costPerCredit = 135.0;

    	           
    	            return totalCredits * costPerCredit;
    	        } catch (SQLException e) {
    	            e.printStackTrace();
    	            return 0.0;
    	        }
    	    }
     
     
     
    	 public List<String> getEnrolledCourses(int studentID) {
    	        List<String> enrolledCoursesList = new ArrayList<>();

    	        try {
    	          
    	            String query = "SELECT Course.name, Course.credits FROM Enrollment "
    	                    + "JOIN Course ON Enrollment.courseID = Course.courseID "
    	                    + "WHERE Enrollment.studentID = ?";
    	            
    	            try (PreparedStatement statement = connection.prepareStatement(query)) {
    	                statement.setInt(1, studentID);
    	                
    	                try (ResultSet resultSet = statement.executeQuery()) {
    	                    while (resultSet.next()) {
    	                        String courseName = resultSet.getString("name");
    	                        int credits = resultSet.getInt("credits");
    	                        
    	                      
    	                        enrolledCoursesList.add(courseName + " (Credits: " + credits + ")");
    	                    }
    	                }
    	            }
    	        } catch (SQLException e) {
    	            e.printStackTrace();
    	        }

    	        return enrolledCoursesList;
    	    }
    	 
    	 
    	 
    	 
    	 
    	 
    	 
    	 
    	 
     
     
     void handleAddCourse(String name, int credits, String instructorName) {
    	    try {
    	      
    	        int instructorID = getInstructorIDByName(instructorName);

    	        if (instructorID != -1) { 
    	            String sql = "INSERT INTO Course (name, credits, instructorID) VALUES (?, ?, ?)";
    	            try (PreparedStatement statement = connection.prepareStatement(sql)) {
    	                statement.setString(1, name);
    	                statement.setInt(2, credits);
    	                statement.setInt(3, instructorID);
    	                statement.executeUpdate();
    	            }
    	            System.out.println("Course added successfully!");
    	        } else {
    	            System.out.println("Instructor not found. Course not added.");
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
    	}

    
    	private int getInstructorIDByName(String name) throws SQLException {
    	    String query = "SELECT instructorID FROM Instructor WHERE name = ?";
    	    try (PreparedStatement statement = connection.prepareStatement(query)) {
    	        statement.setString(1, name);
    	        try (ResultSet resultSet = statement.executeQuery()) {
    	            if (resultSet.next()) {
    	                return resultSet.getInt("instructorID");
    	            }
    	        }
    	    }
    	    return -1; 
    	}
     
     
     
     
     
     
     
     
     
public void handleAddInstructor(String username, String password, String email) {
    try {
      
        String insertPersonQuery = "INSERT INTO Person (username, password, email, role) VALUES (?, ?, ?, 'instructor')";
        try (PreparedStatement personStatement = connection.prepareStatement(insertPersonQuery, Statement.RETURN_GENERATED_KEYS)) {
            personStatement.setString(1, username);
            personStatement.setString(2, password);
            personStatement.setString(3, email);

            int rowsAffected = personStatement.executeUpdate();

            if (rowsAffected > 0) {
              
                try (ResultSet generatedKeys = personStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int personID = generatedKeys.getInt(1);

                       
                        String insertStudentQuery = "INSERT INTO Instructor (instructorID, name, password, email) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement studentStatement = connection.prepareStatement(insertStudentQuery)) {
                            studentStatement.setInt(1, personID);
                          
                            studentStatement.setString(2, username);
                            studentStatement.setString(3, password);
                            studentStatement.setString(4, email);

                            int studentRowsAffected = studentStatement.executeUpdate();

                            if (studentRowsAffected > 0) {
                                System.out.println("Instructor added successfully.");
                            } else {
                                System.out.println("Failed to add instructor.");
                            }
                        }
                    }
                }
            } else {
                System.out.println("Failed to add instructor.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public void enrollStudentInCourse(int studentId, int courseId) {
    try {
        String sql = "INSERT INTO enrollment (studentID, courseID) VALUES (?, ?)";
        try (
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

           
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);

            preparedStatement.executeUpdate();
        }
        
       
        String studentName = getStudentNameById(studentId);
        String courseName = getCourseNameById(courseId);


        showEnrollmentPopup(studentName, courseName);
        
       
        
    } catch (SQLException e) {
        e.printStackTrace();  
    }
}
private void showEnrollmentPopup(String studentName, String courseName) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Enrollment Confirmation");
    alert.setHeaderText(null);
    alert.setContentText("Student  " + studentName + " has been enrolled in Course " + courseName);

    alert.showAndWait();
}


public String getStudentNameById(int studentId) {
    String studentName = null;
    String sql = "SELECT name FROM student WHERE studentID = ?";
    
    try (
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        
        preparedStatement.setInt(1, studentId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            studentName = resultSet.getString("name");
        }
    } catch (SQLException e) {
        e.printStackTrace();  
    }

    return studentName;
}

public String getCourseNameById(int courseId) {
    String courseName = null;
    String sql = "SELECT name FROM course WHERE courseID = ?";
    
    try (
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        
        preparedStatement.setInt(1, courseId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            courseName = resultSet.getString("name");
        }
    } catch (SQLException e) {
        e.printStackTrace();  
    }

    return courseName;
}
	   

public void handleeditAttendance(int studentID, int courseID, String newStatus) {
    try {
        String sql = "UPDATE attendance SET status = ? WHERE studentID = ? AND courseID = ? ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setInt(2, studentID);
            statement.setInt(3, courseID);
           

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Attendance edited successfully.");
            } else {
                System.out.println("No matching attendance record found.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public String getAttendanceStatus(int studentID, int courseID) {
    String status = null;
    String sql = "SELECT status FROM attendance WHERE studentID = ? AND courseID = ?";

    try (
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setInt(1, studentID);
        preparedStatement.setInt(2, courseID);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            status = resultSet.getString("status");
        }
    } catch (SQLException e) {
        e.printStackTrace();  
    }

    return status;
}




    

public void handleaddGrade(int enrollmentID, int grade) {
    try {
        String sql = "INSERT INTO grades (enrollmentID, grade) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollmentID);
            statement.setInt(2, grade);
            statement.executeUpdate();
        }
        System.out.println("Grade added successfully!");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public String getStudentNameByEnrollmentId(int enrollmentId) {
    String studentName = null;
    String sql = "SELECT student.name FROM student " +
                 "JOIN enrollment ON student.studentID = enrollment.studentID " +
                 "WHERE enrollment.enrollmentID = ?";

    try (
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setInt(1, enrollmentId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            studentName = resultSet.getString("name");
        }
    } catch (SQLException e) {
        e.printStackTrace();  
    }

    return studentName;
}

public String getCourseNameByEnrollmentId(int enrollmentId) {
    String courseName = null;
    String sql = "SELECT course.name FROM course " +
                 "JOIN enrollment ON course.courseID = enrollment.courseID " +
                 "WHERE enrollment.enrollmentID = ?";

    try (
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setInt(1, enrollmentId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            courseName = resultSet.getString("name");
        }
    } catch (SQLException e) {
        e.printStackTrace();  
    }

    return courseName;
}

public int getGradeByEnrollmentId(int enrollmentId) {
    int grade = -1;  
    String sql = "SELECT grade FROM grades WHERE enrollmentID = ?";

    try (
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setInt(1, enrollmentId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            grade = resultSet.getInt("grade");
        }
    } catch (SQLException e) {
        e.printStackTrace(); 
    }

    return grade;
}



public void handleaddAttendance(int studentID, int courseID, String attendanceDate, String status) {
    try {
      
        String sql = "INSERT INTO attendance (studentID, courseID, attendanceDate, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentID);
            statement.setInt(2, courseID);
            statement.setString(3, attendanceDate);
            statement.setString(4, status);
            statement.executeUpdate();
        }

        System.out.println("Attendance added successfully!");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}





public boolean deleteStudent(int studentID) {
    String sql = "DELETE FROM student WHERE studentID = ?";

    try (
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setInt(1, studentID);
        int rowsAffected = preparedStatement.executeUpdate();

        return rowsAffected > 0;

    } catch (SQLException e) {
        e.printStackTrace();  
        return false;
    }
}














}









