CREATE TABLE Person (
 personID INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
   password VARCHAR(50) NOT NULL,
   email VARCHAR(100) NOT NULL,
  role ENUM('student', 'instructor', 'admin') NOT NULL
);


CREATE TABLE Course (
    courseID INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   credits INT NOT NULL,
  instructorID INT,
     FOREIGN KEY (instructorID) REFERENCES Person(personID)
);


 CREATE TABLE Enrollment (
   enrollmentID INT AUTO_INCREMENT PRIMARY KEY,
  studentID INT,
    courseID INT,
    FOREIGN KEY (studentID) REFERENCES Person(personID),
   FOREIGN KEY (courseID) REFERENCES Course(courseID)
 );

 CREATE TABLE Attendance (
    attendanceID INT AUTO_INCREMENT PRIMARY KEY,
    studentID INT,
    courseID INT,
    attendanceDate DATE,
    status ENUM('present', 'absent') NOT NULL,
     FOREIGN KEY (studentID) REFERENCES Person(personID),
     FOREIGN KEY (courseID) REFERENCES Course(courseID)
 );


CREATE TABLE Payment (
    paymentID INT AUTO_INCREMENT PRIMARY KEY,
  studentID INT,
    paymentDate DATE,
  amount DECIMAL(10, 2) NOT NULL,
   FOREIGN KEY (studentID) REFERENCES Person(personID)
 );

CREATE TABLE Grades (
     gradeID INT AUTO_INCREMENT PRIMARY KEY,
   enrollmentID INT,
    FOREIGN KEY (enrollmentID) REFERENCES Enrollment(enrollmentID)
);

ALTER TABLE Grades
ADD COLUMN grade int;

CREATE TABLE Student (
   studentID INT PRIMARY KEY,
     name VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
     email VARCHAR(100) NOT NULL,
    FOREIGN KEY (studentID) REFERENCES Person(personID)
 );

 CREATE TABLE Instructor (
    instructorID INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    FOREIGN KEY (instructorID) REFERENCES Person(personID)
 );

 CREATE TABLE Admin (
    adminID INT PRIMARY KEY,
   name VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
   email VARCHAR(100) NOT NULL,
   FOREIGN KEY (adminID) REFERENCES Person(personID)
 );


DELIMITER 

 CREATE TRIGGER add_student1 AFTER INSERT ON Person
 FOR EACH ROW
 BEGIN
    IF NEW.role = 'student' THEN
        INSERT INTO Student (studentID, name, password, email)
       VALUES (NEW.personID, NEW.username, NEW.password, NEW.email);
   END IF;
END;

CREATE TRIGGER add_instructor AFTER INSERT ON Person
FOR EACH ROW
BEGIN
    IF NEW.role = 'instructor' THEN
        INSERT INTO Instructor (instructorID, name, password, email)
       VALUES (NEW.personID, NEW.username, NEW.password, NEW.email);
    END IF;
 END;

 CREATE TRIGGER add_admin AFTER INSERT ON Person
 FOR EACH ROW
-BEGIN
   IF NEW.role = 'admin' THEN
     INSERT INTO Admin (adminID, name, password, email)
       VALUES (NEW.personID, NEW.username, NEW.password, NEW.email);
  END IF;
END;



DELIMITER ;

 DROP TRIGGER add_student_after_insert;
