package application;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;



public class Main extends Application {
    private Functions functions;
    
    @Override
    public void start(Stage primaryStage) {
        functions = new Functions(0, null);
        functions.connect();
        
       

        primaryStage.setTitle("SIS");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(150); 

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(150); 
        


        Button loginButton = new Button("Login");
        Label statusLabel = new Label();
        loginButton.setStyle("-fx-background-color: #0077cc; -fx-text-fill: white;");


        usernameLabel.setAlignment(Pos.CENTER);
        passwordLabel.setAlignment(Pos.CENTER);

        
        
     
        ImageView imageView = new ImageView();
        try {
            FileInputStream input = new FileInputStream("C:\\Users\\Fatmeh Kassab\\Downloads\\image.png");
            Image image = new Image(input);
            imageView.setImage(image);
            imageView.setFitWidth(250);
            imageView.setFitHeight(250);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            try {
                String role = functions.authenticate(username, password);

                if (role != null) {
                    
                    statusLabel.setText("Login successful!");
                    statusLabel.setTextFill(Color.GREEN);

                    switch (role) {
                        case "admin":
                            openAdminScene(primaryStage, username, role);
                            break;
                        case "instructor":
                            openInstructorScene(primaryStage, username, role);
                            break;
                        case "student":
                            openStudentScene(primaryStage, username, role);
                            break;
                        default:
                            statusLabel.setText("Role not recognized");
                            break;
                    }
                } else {
                    
                    statusLabel.setText("Invalid credentials. Please try again.");
                    statusLabel.setTextFill(Color.RED);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(imageView,usernameLabel, usernameField, passwordLabel, passwordField, loginButton, statusLabel);
        root.setPrefWidth(200);

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openAdminScene(Stage primaryStage, String username, String role) {
    	 VBox adminLayout = new VBox(20);
    	    adminLayout.setPadding(new Insets(20));
    	    adminLayout.setAlignment(Pos.CENTER);

    	    Label welcomeLabel = new Label("Welcome, " + username);
    	    Label roleLabel = new Label("Welcome to the Admin Dashboard");
    	    roleLabel.setStyle("-fx-font-weight: bold;");

    	    VBox optionsBox = new VBox(10);
    	    optionsBox.setAlignment(Pos.CENTER);
    	    optionsBox.setPadding(new Insets(10));
    	    Button addStudentButton = new Button("Add Student");
    	    Button deleteStudentButton = new Button("Delete Student");
    	    
    	    Button addInstructorButton = new Button("Add Instructor");
    	    Button addCourseButton = new Button("Add Course");
    	    Button addPaymentButton = new Button("Add Payment");
    	    Button enrollStudentButton = new Button("Enroll Student");
    	    Button editAttendanceButton = new Button("Edit Attendance");

    	   
    	    applyButtonStyle(addStudentButton);
    	    applyButtonStyle(deleteStudentButton);
    	    applyButtonStyle(addInstructorButton);
    	    applyButtonStyle(addCourseButton);
    	    applyButtonStyle(addPaymentButton);
    	    applyButtonStyle(enrollStudentButton);
    	    applyButtonStyle(editAttendanceButton);

    	    optionsBox.getChildren().addAll(
    	            addStudentButton,deleteStudentButton, addInstructorButton, addCourseButton,
    	            addPaymentButton, enrollStudentButton, editAttendanceButton);

    	    adminLayout.getChildren().addAll(welcomeLabel, roleLabel, optionsBox);

    	    Scene adminScene = new Scene(adminLayout, 800, 600);
    	    primaryStage.setScene(adminScene);

    	    addStudentButton.setOnAction(e -> addStudent(primaryStage, adminScene));
    	    deleteStudentButton.setOnAction(e -> deleteStudent(primaryStage, adminScene));

    	    addInstructorButton.setOnAction(e -> addInstructor(primaryStage, adminScene));
    	    addCourseButton.setOnAction(e -> addCourse(primaryStage, adminScene));
    	    addPaymentButton.setOnAction(e -> addPayment(primaryStage, adminScene));
    	    enrollStudentButton.setOnAction(e -> addEnrollment(primaryStage, adminScene));
    	    editAttendanceButton.setOnAction(e -> editAttendance(primaryStage, adminScene));

    	    primaryStage.setTitle("Admin Dashboard");
    	    primaryStage.show();
    	}


    
    
    private ObservableList<String> studentList = FXCollections.observableArrayList();
    public class Student {
        private String name;
        private String password;
        private String email;

        public Student(String name, String password, String email) {
            this.name = name;
            this.password = password;
            this.email = email;
        }

        @Override
        public String toString() {
            return "Name: " + name + ", Password: " + password + ", Email: " + email;
        }}
    
    private void addStudent(Stage primaryStage, Scene previousScene) {
    	VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Add Student");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(8);

        Label usernameLabel = new Label("Username:");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Enter username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter password");

        Label emailLabel = new Label("Email:");
        TextField emailInput = new TextField();
        emailInput.setPromptText("Enter email");

        Button submitButton = new Button("Add Student");
        submitButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            String email = emailInput.getText();
            functions.handleAddStudent(username, password, email);
            studentList.add(username);
            clearFields(usernameInput, passwordInput, emailInput);
        });

        ListView<String> listView = new ListView<>(studentList);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        VBox formBox = new VBox(8, usernameLabel, usernameInput, passwordLabel, passwordInput, emailLabel, emailInput);
        formBox.setMaxWidth(300);

        HBox buttonBox = new HBox(10, submitButton, backButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(titleLabel, formBox, buttonBox, new Separator(), listView);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Student");
        primaryStage.show();
        root.setStyle("-fx-background-color: #91D2F2;");
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    
    
    private void deleteStudent(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label idLabel = new Label("Student ID:");
        TextField idInput = new TextField();
        idInput.setPromptText("Enter Student ID");

        Button deleteButton = new Button("Delete Student");
        deleteButton.setOnAction(e -> {
            try {
                int studentID = Integer.parseInt(idInput.getText());

             
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Are you sure you want to delete this student?");
                alert.setContentText("This action cannot be undone.");

             
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Delete student from the database
                        boolean success = functions.deleteStudent(studentID);

                        // Show result message
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Student deleted successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete student.");
                        }
                    }
                });

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid Student ID.");
            }
        });

        Button backButton = createBackButton(primaryStage, previousScene);

        root.getChildren().addAll(idLabel, idInput, deleteButton, backButton);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Delete Student");
        primaryStage.show();
        root.setStyle("-fx-background-color: #91D2F2;");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

  
    
    
    
    
    
    
 
    
    private void addPayment(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label studentIdLabel = new Label("Student ID:");
        TextField studentIdInput = new TextField();
        studentIdInput.setPromptText("Enter Student ID");

        Label paymentDateLabel = new Label("Payment Date:");
        DatePicker paymentDatePicker = new DatePicker();
        
        
        ListView<String> enrolledCoursesListView = new ListView<>();
        enrolledCoursesListView.setPrefHeight(200);

        
        Label amountLabel = new Label("Amount 135*nbcredit:");
        TextField amountInput = new TextField();
        amountInput.setPromptText("Calculated Amount");
        amountInput.setDisable(true);

        Button calculateAmountButton = new Button("Calculate Amount");
        calculateAmountButton.setOnAction(e -> {
            try {
                int studentID = Integer.parseInt(studentIdInput.getText());
                
               
                List<String> enrolledCoursesList = functions.getEnrolledCourses(studentID);
                enrolledCoursesListView.getItems().setAll(enrolledCoursesList);


                
                double calculatedAmount = functions.calculatePaymentAmount(studentID);
                
                
                amountInput.setText(String.valueOf(calculatedAmount));
            } catch (NumberFormatException ex) {
               
                showAlert("Invalid input. Please enter a numeric value for Student ID.", Alert.AlertType.ERROR);
            }
        });

        Button addButton = new Button("Add Payment");
        addButton.setOnAction(e -> {
            try {
                int studentID = Integer.parseInt(studentIdInput.getText());
                String paymentDateStr = paymentDatePicker.getValue().toString();
                double amount = Double.parseDouble(amountInput.getText());

                functions.addPayment(studentID, paymentDateStr);

                
                showAlert("Payment added successfully", Alert.AlertType.INFORMATION);

                
                studentIdInput.clear();
                paymentDatePicker.setValue(null);
                amountInput.clear();
            } catch (NumberFormatException ex) {
                
                showAlert("Invalid input. Please enter numeric values for Student ID and Amount.", Alert.AlertType.ERROR);
            } catch (ParseException e1) {
				
				e1.printStackTrace();
			}
        });

        Button backButton = createBackButton(primaryStage, previousScene);

        root.getChildren().addAll(studentIdLabel, studentIdInput, paymentDateLabel, paymentDatePicker, amountLabel, amountInput, calculateAmountButton, 
        		addButton,enrolledCoursesListView, backButton);
        root.setStyle("-fx-background-color: #91D2F2;");
        Scene scene = new Scene(root,  800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Payment");
        primaryStage.show();
    }


  

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    
    private ObservableList<String> instructorList = FXCollections.observableArrayList();
    private void addInstructor(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label usernameLabel = new Label("Username:");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Enter username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter password");

        Label emailLabel = new Label("Email:");
        TextField emailInput = new TextField();
        emailInput.setPromptText("Enter email");

        Button submitButton = new Button("Add Instructor");
        submitButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            String email = emailInput.getText();
            functions.handleAddInstructor(username, password, email);
            
            String instructorDetails = "Username: " + username + ", Password: " + password + ", Email: " + email;

            instructorList.add(instructorDetails);

            
        });

        Button backButton = createBackButton(primaryStage, previousScene);
        ListView<String> listView = new ListView<>(instructorList);
        root.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, emailLabel,
                emailInput, submitButton, backButton, listView);
     
        root.setStyle("-fx-background-color: #91D2F2;");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Instructor");
        primaryStage.show();
    }

    

    private ObservableList<String> courseList = FXCollections.observableArrayList();

    

    private void addCourse(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label nameLabel = new Label("Course Name:");
        TextField nameInput = new TextField();

        Label creditsLabel = new Label("Credits:");
        TextField creditsInput = new TextField();

        Label InameLabel = new Label("Instructor Name:");
        ComboBox<String> instructorComboBox = new ComboBox<>();

     
        Platform.runLater(() -> {
            List<String> instructorNamesFromDB = functions.getInstructorNamesFromDatabase();
            instructorComboBox.setItems(FXCollections.observableArrayList(instructorNamesFromDB));
        });

        Button addButton = new Button("Add Course");
        addButton.setOnAction(e -> {
            String name = nameInput.getText();
            int credits = Integer.parseInt(creditsInput.getText());
            String instructorName = instructorComboBox.getValue();

            
            if (instructorName != null && !instructorName.isEmpty()) {
                String courseDetails = "Course: " + name + ", Credits: " + credits + ", Instructor: " + instructorName;

                
                courseList.add(courseDetails);

                
                functions.handleAddCourse(name, credits, instructorName);
            } else {
               
            }
        });

        ListView<String> listView = new ListView<>(courseList);

        Button backButton = createBackButton(primaryStage, previousScene);
        root.getChildren().addAll(nameLabel, nameInput, creditsLabel, creditsInput, InameLabel, instructorComboBox, addButton, backButton, listView);
        root.setStyle("-fx-background-color: #91D2F2;");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Course");
        primaryStage.show();
    }

    
    private void addEnrollment(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label idLabel = new Label("Student ID:");
        TextField sidInput = new TextField();
      

        Label cidLabel = new Label("Course ID:");
	    TextField cidInput = new TextField();

        Button submitButton = new Button("Add Enroll Student");
        submitButton.setOnAction(e -> {
        	 int Studentid = Integer.parseInt(sidInput.getText());
        	 int Courseid = Integer.parseInt(cidInput.getText());
            
        
            functions.enrollStudentInCourse(Studentid, Courseid);
            
        });

        Button backButton = createBackButton(primaryStage, previousScene);

       

        root.getChildren().addAll( idLabel,sidInput,cidLabel ,cidInput, submitButton, backButton);

     
        root.setStyle("-fx-background-color: #91D2F2;");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Enroll student");
        primaryStage.show();
    }
    
    
 
    
    
    private ObservableList<String> attendanceList = FXCollections.observableArrayList();
    
    private void editAttendance(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label studentLabel = new Label("Student ID:");
        TextField studentInput = new TextField();
        studentInput.setPromptText("Enter Student ID");

        Label courseLabel = new Label("Course ID:");
        TextField courseInput = new TextField();
        courseInput.setPromptText("Enter Course ID");

        Label statusLabel = new Label("New Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Present", "Absent");
        statusComboBox.setPromptText("Select Status");
        
  

        Button submitButton = new Button("Edit Attendance");
        submitButton.setOnAction(e -> {
            try {
                int studentID = Integer.parseInt(studentInput.getText());
                int courseID = Integer.parseInt(courseInput.getText());
                String newStatus = statusComboBox.getValue();
                
                
                String previousStatus = functions.getAttendanceStatus(studentID, courseID);

                
                functions.handleeditAttendance(studentID, courseID, newStatus);

                
                updateAttendanceListView(studentID, courseID, previousStatus, newStatus);
                  

            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter valid IDs.");
            }
        });

        Button backButton = createBackButton(primaryStage, previousScene);
        ListView<String> attendanceListView = new ListView<>(attendanceList);
        attendanceListView.setPrefHeight(200);  // Adjust the height as needed

        root.getChildren().addAll(studentLabel, studentInput, courseLabel, courseInput, statusLabel, statusComboBox,
                submitButton, backButton, attendanceListView);
        root.setStyle("-fx-background-color: #91D2F2;");
        Scene scene = new Scene(root,800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Edit Attendance");
        primaryStage.show();
    }

    private void updateAttendanceListView(int studentID, int courseID, String previousStatus, String newStatus) {
        String entry = "Student ID: " + studentID + ", Course ID: " + courseID +
                ", Previous Status: " + previousStatus + ", New Status: " + newStatus;
        attendanceList.add(entry);
    }
    
    
    
    private void openInstructorScene(Stage primaryStage, String username, String role) {
    	
    	  VBox instructorLayout = new VBox(20);
    	    instructorLayout.setPadding(new Insets(20));
    	    instructorLayout.setAlignment(Pos.CENTER);

    	    Label welcomeLabel = new Label("Welcome, " + username);
    	    Label roleLabel = new Label("Welcome to the Instructor Dashboard");
    	    roleLabel.setStyle("-fx-font-weight: bold;");

    	    VBox optionsBox = new VBox(10);
    	    optionsBox.setAlignment(Pos.CENTER);
    	    optionsBox.setPadding(new Insets(10));
    	    Button addGradesButton = new Button("Add Grades");
    	    Button addAttendanceButton = new Button("Add Attendance");
    	    Button manageClassesButton = new Button("Manage Classes");

    	    
    	    applyButtonStyle(addGradesButton);
    	    applyButtonStyle(addAttendanceButton);
    	    applyButtonStyle(manageClassesButton);

    	    optionsBox.getChildren().addAll(addGradesButton, addAttendanceButton, manageClassesButton);

    	    instructorLayout.getChildren().addAll(welcomeLabel, roleLabel, optionsBox);

    	    Scene instructorScene = new Scene(instructorLayout, 400, 300);
    	    primaryStage.setScene(instructorScene);

    	    addGradesButton.setOnAction(e -> addGrade(primaryStage, instructorScene));
    	    addAttendanceButton.setOnAction(e -> addAttendance(primaryStage, instructorScene));


    	    primaryStage.setTitle("Instructor Dashboard");
    	    primaryStage.show();
    	}
    
    private ObservableList<String> gradeList = FXCollections.observableArrayList();
    private void addGrade(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label enrollLabel = new Label("Enrollment ID");
        TextField enrollInput = new TextField();
      

        Label gradeLabel = new Label("Grade");
	    TextField gradeInput = new TextField();

        Button submitButton = new Button("Add Grade");
        
        submitButton.setOnAction(e -> {
          
            int enroll = Integer.parseInt(enrollInput.getText());
            int grade = Integer.parseInt(gradeInput.getText());
            
            
           

            
            updateGradeListView(enroll);
        
            functions. handleaddGrade(enroll, grade);

            
        });

        Button backButton = createBackButton(primaryStage, previousScene);

        ListView<String> gradeListView = new ListView<>(gradeList);
        gradeListView.setPrefHeight(200);  

        root.getChildren().addAll(enrollLabel, enrollInput, gradeLabel, gradeInput, submitButton, backButton, gradeListView);

     
        root.setStyle("-fx-background-color: #91D2F2;");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Grade");
        primaryStage.show();
    }
    
    private void updateGradeListView(int enrollmentId) {
      
        String studentName = functions.getStudentNameByEnrollmentId(enrollmentId);
        String courseName = functions.getCourseNameByEnrollmentId(enrollmentId);
        int grade = functions.getGradeByEnrollmentId(enrollmentId);

      
        String entry = "Student: " + studentName + ", Course: " + courseName + ", Grade: " + grade;
        gradeList.add(entry);
    }
    
    
    private void addAttendance(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label studentLabel = new Label("Student ID:");
        TextField studentInput = new TextField();
        studentInput.setPromptText("Enter Student ID");

        Label courseLabel = new Label("Course ID:");
        TextField courseInput = new TextField();
        courseInput.setPromptText("Enter Course ID");

        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Present", "Absent");
        statusComboBox.setPromptText("Select Status");

        Label dateLabel = new Label("Attendance Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        Button submitButton = new Button("Add Attendance");
        submitButton.setOnAction(e -> {
            try {
                int studentID = Integer.parseInt(studentInput.getText());
                int courseID = Integer.parseInt(courseInput.getText());
                String status = statusComboBox.getValue();
                LocalDate date = datePicker.getValue();

                if (date != null) {
                    String attendanceDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    functions.handleaddAttendance(studentID, courseID, attendanceDate, status);

                  
                } else {
                    System.out.println("Please select a valid date.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter valid IDs.");
            }
        });

        Button backButton = createBackButton(primaryStage, previousScene);

        root.getChildren().addAll(studentLabel, studentInput, courseLabel, courseInput, statusLabel, statusComboBox, dateLabel, datePicker, submitButton, backButton);
        root.setStyle("-fx-background-color: #91D2F2;");
        Scene scene = new Scene(root,  800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Attendance");
        primaryStage.show();
    }
    

    
    
    
    
    
    
    
    
    
    
    
    private Button createBackButton(Stage stage, Scene previousScene) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(previousScene));
        return backButton;
    }

    private void openStudentScene(Stage primaryStage, String username, String role) {
    	  VBox studentLayout = new VBox(20);
          studentLayout.setPadding(new Insets(20));
          studentLayout.setAlignment(Pos.CENTER);

          Label welcomeLabel = new Label("Welcome,"+username);
          Label roleLabel = new Label("Welcome to the Student Dashboard");
          roleLabel.setStyle("-fx-font-weight: bold;");

          VBox optionsBox = new VBox(10);
          optionsBox.setAlignment(Pos.CENTER);
          optionsBox.setPadding(new Insets(10));
          Button manageCoursesButton = new Button("Manage Courses");
          Button viewGradesButton = new Button("View Grades");
          Button attendanceTrackingButton = new Button("Attendance Tracking");
          Button checkPaymentsButton = new Button("Check Payments");
          Button changePasswordButton = new Button("Change Password");

          
          applyButtonStyle(manageCoursesButton);
          applyButtonStyle(viewGradesButton);
          applyButtonStyle(attendanceTrackingButton);
          applyButtonStyle(checkPaymentsButton);
          applyButtonStyle(changePasswordButton);

          optionsBox.getChildren().addAll(
                  manageCoursesButton, viewGradesButton, attendanceTrackingButton,
                  checkPaymentsButton, changePasswordButton);

          studentLayout.getChildren().addAll(welcomeLabel, roleLabel, optionsBox);

          Scene studentScene = new Scene(studentLayout, 600, 400);
          primaryStage.setScene(studentScene);

          manageCoursesButton.setOnAction(e -> manageCourses(primaryStage, studentScene));
          viewGradesButton.setOnAction(e -> openViewGradesScene(primaryStage, studentScene));
          attendanceTrackingButton.setOnAction(e -> openAttendanceTrackingScene(primaryStage, studentScene));
          checkPaymentsButton.setOnAction(e -> openCheckPaymentsScene(primaryStage, studentScene));
          changePasswordButton.setOnAction(e -> openChangePasswordScene(primaryStage, studentScene));

          primaryStage.setTitle("Student Dashboard");
          primaryStage.show();
      }

      
      private void applyButtonStyle(Button button) {
          button.setPrefWidth(200);
          button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
      }

    private void manageCourses(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root,  800, 600);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter course name");

        ListView<String> displayList = new ListView<>();
        displayList.setPrefHeight(200);
        displayList.setEditable(false);

        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);

        Button viewEnrolled = new Button("View Classes");
        viewEnrolled.setOnAction(e -> viewEnrolledCourses(primaryStage,scene));

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String courseName = searchField.getText();
            ObservableList<String> data = FXCollections.observableArrayList();
            functions.searchCourse(courseName, data);
            displayList.setItems(data);
        });

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

        addButton.setOnAction(e -> {
            int courseId = functions.cId;
            int studentId = functions.pId;

            if (courseId != 0 && studentId != 0) {
                boolean courseAdded = functions.addCourse(studentId, courseId);

                if (courseAdded) {
                	messageArea.setText("Course added to the student!");
                } else {
                    messageArea.setText("Failed to add course for the student.");
                }
            } else {
                messageArea.setText("Please select a course and a student to add.");
            }
        });

        root.getChildren().addAll(searchField, searchButton, displayList, addButton, viewEnrolled, messageArea, createBackButton(primaryStage, previousScene));

        root.setStyle("-fx-background-color: #91D2F2;");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Course Search App");
        primaryStage.show();
    }
    

    private void viewEnrolledCourses(Stage primaryStage, Scene previousScene) {
        VBox enrolledCoursesLayout = new VBox(10);
        enrolledCoursesLayout.setPadding(new Insets(20));

        VBox enrolledCoursesList = new VBox(10);

        List<String> enrolledCourses = functions.viewEnrolledCourses(functions.pId);

        for (String course : enrolledCourses) {
            HBox courseInfo = new HBox(10);

            Label courseLabel = new Label(course);
            courseInfo.getChildren().add(courseLabel);

            Button dropButton = new Button("Drop");
            dropButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

            String[] courseDetails = course.split(",");
            int courseId = Integer.parseInt(courseDetails[0].split(":")[1].trim());
            dropButton.setOnAction(e -> dropCourse(courseId));

            courseInfo.getChildren().add(dropButton);
            enrolledCoursesList.getChildren().add(courseInfo);
        }

        Button backButton = createBackButton(primaryStage, previousScene); 

        enrolledCoursesLayout.getChildren().addAll(enrolledCoursesList, backButton);
        enrolledCoursesLayout.setStyle("-fx-background-color: #91D2F2;");
        Scene enrolledCoursesScene = new Scene(enrolledCoursesLayout,  800, 600);
        primaryStage.setScene(enrolledCoursesScene);
        primaryStage.show();
    }


    
    private void dropCourse(int courseId) {
        boolean dropped = functions.dropCourse(functions.pId, courseId);
        if (dropped) {
            
            System.out.println("Course with ID " + courseId + " dropped successfully!");
        } else {
           
            System.out.println("Failed to drop course with ID " + courseId);
        }

        
    }



    private void openViewGradesScene(Stage primaryStage, Scene previousScene) {
        VBox viewGradesLayout = new VBox(10);
        viewGradesLayout.setPadding(new Insets(20));

        Label viewGradesLabel = new Label("View Grades");
        viewGradesLayout.getChildren().add(viewGradesLabel);

        List<String> enrolledCoursesWithGrades = functions.viewGrades(functions.pId);

        VBox gradesList = new VBox(10);

        for (String course : enrolledCoursesWithGrades) {
            HBox courseInfo = new HBox(10);

            Label courseLabel = new Label(course);
            courseInfo.getChildren().add(courseLabel);

            gradesList.getChildren().add(courseInfo);
        }

        Button backButton = createBackButton(primaryStage, previousScene); 

        viewGradesLayout.getChildren().addAll(gradesList, backButton);
        viewGradesLayout.setStyle("-fx-background-color: #91D2F2;");
        Scene viewGradesScene = new Scene(viewGradesLayout,  800, 600);
        primaryStage.setScene(viewGradesScene);
        primaryStage.show();
    }



    private void openAttendanceTrackingScene(Stage primaryStage, Scene previousScene) {
        VBox attendanceTrackingLayout = new VBox(10);
        attendanceTrackingLayout.setPadding(new Insets(20));

        Label attendanceTrackingLabel = new Label("Attendance Tracking");
        attendanceTrackingLayout.getChildren().add(attendanceTrackingLabel);

        List<String> attendanceDetails = functions.viewAttendance(functions.pId);

        VBox attendanceList = new VBox(10);

        for (String attendance : attendanceDetails) {
            HBox attendanceInfo = new HBox(10);

            Label attendanceLabel = new Label(attendance);
            attendanceInfo.getChildren().add(attendanceLabel);

            attendanceList.getChildren().add(attendanceInfo);
        }

        Button backButton = createBackButton(primaryStage, previousScene);

        attendanceTrackingLayout.getChildren().addAll(attendanceList, backButton);
        attendanceTrackingLayout.setStyle("-fx-background-color: #91D2F2;");
        Scene attendanceTrackingScene = new Scene(attendanceTrackingLayout,  800, 600);
        primaryStage.setScene(attendanceTrackingScene);
        primaryStage.show();
    }



    private void openCheckPaymentsScene(Stage primaryStage, Scene previousScene) {
        VBox paymentsLayout = new VBox(10);
        paymentsLayout.setPadding(new Insets(20));

        Label paymentsLabel = new Label("Payments");
        paymentsLayout.getChildren().add(paymentsLabel);

        List<String> paymentDetails = functions.viewPayments(functions.pId);

        VBox paymentList = new VBox(10);

        for (String payment : paymentDetails) {
            HBox paymentInfo = new HBox(10);

            Label paymentLabel = new Label(payment);
            paymentInfo.getChildren().add(paymentLabel);

            paymentList.getChildren().add(paymentInfo);
        }

        Button backButton = createBackButton(primaryStage, previousScene); 

        paymentsLayout.getChildren().addAll(paymentList, backButton);
        paymentsLayout.setStyle("-fx-background-color: #91D2F2;");
        Scene paymentsScene = new Scene(paymentsLayout,  800, 600);
        primaryStage.setScene(paymentsScene);
        primaryStage.show();
    }



    private void openChangePasswordScene(Stage primaryStage, Scene previousScene) {
        VBox changePasswordLayout = new VBox(10);
        changePasswordLayout.setPadding(new Insets(20));

        Label newPasswordLabel = new Label("Enter New Password:");
        PasswordField newPasswordField = new PasswordField(); 

        Button changePasswordButton = new Button("Change Password");
        TextArea messageArea = new TextArea(); 
        messageArea.setEditable(false); 

        changePasswordButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            boolean passwordChanged = functions.updateStudentPassword(functions.pId, newPassword);

            if (passwordChanged) {
                messageArea.setText("Password changed successfully!"); 
            } else {
                messageArea.setText("Failed to change password."); 
            }
        });

        Button backButton = createBackButton(primaryStage, previousScene); 

        changePasswordLayout.getChildren().addAll(newPasswordLabel, newPasswordField, changePasswordButton, messageArea, backButton);
        changePasswordLayout.setStyle("-fx-background-color: #91D2F2;");
        Scene changePasswordScene = new Scene(changePasswordLayout,  800, 600);
        primaryStage.setScene(changePasswordScene);
        primaryStage.show();
    }


    
   
    
    


    @Override
    public void stop() {
        functions.disconnect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}