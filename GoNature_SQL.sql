-- Create the schema
DROP SCHEMA IF EXISTS gonature ; -- Drop schema if exists
CREATE SCHEMA gonature;


-- GUIDE TABLE
CREATE TABLE gonature.users(
    id varchar(25) PRIMARY KEY,
    Guide varchar(3),
    isLogged varchar(3)
);

INSERT INTO gonature.users (id, Guide, isLogged)
VALUES
    ('9001',  '1', '0'),
    ('9002',  '0', '0'),
    ('9003',  '0', '0'),
    ('8001',  '1', '0'),
    ('8002',  '0', '0'),
    ('8003',  '0', '0');


-- SMS notficiation :
CREATE TABLE gonature.SMS(
	Userid varchar(25),
    reservationNumber int,
    message varchar(200)
);

-- Reservaions
CREATE TABLE gonature.Reservation (
  reservationId  INT AUTO_INCREMENT PRIMARY KEY,
  userID VARCHAR(25) ,
  visitDate VARCHAR(25) ,     
  visitHour VARCHAR(25) ,
  numberOfVisitors INT, 
  park_name VARCHAR(25) ,
  email VARCHAR(25),
  phoneNumber VARCHAR(25),
  isGroup VARCHAR(25),
  Status VARCHAR(25),
  isPaid varchar(25)
);

 INSERT INTO gonature.Reservation (userID, visitDate, visitHour, numberOfVisitors, park_name, email, phoneNumber, isGroup, Status,isPaid)
 VALUES 
	-- Tests For Entry/Exit  From parks (check in entry Worker For Gan HaShlosha)
	('8001', '04/15/2024','13', 14, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'User_Approved','0'), -- Failed : Not For This Park
    ('8002', '04/15/2024','13', 14, 'Gan HaShlosha', 'jane.smith@mail.com', '1234567890','0', 'Confirmed','0'), -- Failed Status Must Be User_Approved(mean thats he approve day before) : Output Reservation Not Available
	('8003', '03/30/2024','11', 14, 'Gan HaShlosha', 'jane.smith@mail.com', '1234567890','0', 'Canceled','0'), -- Failed Status Must Be User_Approved(Canceled) :  Output Reservation Not Available
    ('8004', '04/15/2024','13', 14, 'Gan HaShlosha', 'jane.smith@mail.com', '1234567890','1', 'User_Approved','0'), -- Failed (Date is Not now) 
	('8005', '03/30/2024','14', 14, 'Gan HaShlosha', 'jane.smith@mail.com', '1234567890','1', 'User_Approved','1'),-- Sucess test (first Set Date AND HOur to now)
-- Test for total visitors report
    ('8004', '03/15/2024','13', 12, 'Banias Nature', 'jane.smith@mail.com', '1234567890','1', 'Visited','0'), 
	('8005', '03/30/2024','14', 15, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'Visited','1'),
	('8004', '02/15/2024','13', 11, 'Banias Nature', 'jane.smith@mail.com', '1234567890','1', 'Visited','0'), 
	('8005', '03/30/2024','14', 8, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'Canceled','1'),
-- Test for Cancel Report

	('8010', '05/01/2024','20', 14, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'Canceled','0'),
	('8010', '05/01/2024','20', 14, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'Canceled','0'),
	('8010', '05/01/2024','20', 14, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'Canceled','0'),
	('8010', '05/01/2024','20', 14, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'Canceled','0'),
	('8010', '05/01/2024','20', 14, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'NotVisited','0'),
	('8010', '05/01/2024','20', 14, 'Banias Nature', 'jane.smith@mail.com', '1234567890','0', 'NotVisited','0');

-- Employee
CREATE TABLE gonature.Employee (
    fullname VARCHAR(25) not null,
    employeeNumber VARCHAR(3) PRIMARY KEY not null,
    email VARCHAR(25) not null,
    role VARCHAR(25)not null,
    username VARCHAR(25) UNIQUE not null,
    password VARCHAR(25) not null,
    park VARCHAR(25) not null,
    isLogged VARCHAR(25) not null
);

-- INSERT INTO gonature.Employee (fullname, employeeNumber, email, role, username, password, park,isLogged)
-- VALUES 
  --  ('Emma Watson', '1', 'emma.watson@mail.com', 'ParkManager', 'parkm', 'password1', 'Banias Nature','0'),
  --   ('Tom Hanks', '2', 'tom.hanks@mail.com', 'ParkManager', 'tom_hanks', 'password2', 'Gan HaShlosha','1'),
   --  ('Jennifer Lopez', '3', 'jennifer.lopez@mail.com', 'ParkManager', 'jennifer_lopez', 'password3', 'Ein Gedi','0'),
   -- ('Julia Roberts', '4', 'julia.roberts@mail.com', 'DepartmentManager', 'depm', 'password1', 'ALL','0'),
  --  ('Margot Robbie', '5', 'margot.robbie@mail.com', 'EntryWorker', 'margot_robbie', 'password11', 'Banias Nature','0'),
  --  ('Chris Hemsworth', '6', 'chris.hemsworth@mail.com', 'EntryWorker', 'entry1', 'password1', 'Gan HaShlosha','0'),
   -- ('Gal Gadot', '7', 'gal.gadot@mail.com', 'EntryWorker', 'gal_gadot', 'password13', 'Ein Gedi','0'),
	-- ('maher salman', '0', 'service@mail.com', 'ServiceRepresenitive', 'service', 'password0', 'Ein Gedi','0');

-- Park
CREATE TABLE gonature.Park (
    name VARCHAR(25) PRIMARY KEY not null,
    maxVisitros VARCHAR(3) not null,
    maxReservations VARCHAR(25) not null,
    avgVisitingTime VARCHAR(25)not null,
    currentlyVisitorsInPark INT
);

INSERT INTO gonature.Park (name, maxVisitros, maxReservations, avgVisitingTime,currentlyVisitorsInPark)
VALUES 
    ('Banias Nature', '100', '80', '1',0),
    ('Gan HaShlosha', '100', '70', '1',0),
    ('Ein Gedi', '100', '20', '1',0);
    
    
-- ParkVacancy for each hour at a day if an hour not exits mean at full free
CREATE TABLE gonature.ParkVacancy(
ParkName VARCHAR(25) NOT NULL,
Date varchar(25) not null,
Hour varchar(25)not null,
visitors_withReservation  INT,
visitors_withoutResevation INT
);

INSERT INTO gonature.ParkVacancy(ParkName,Date,Hour,visitors_withReservation,visitors_withoutResevation)
VALUES
    ('Banias Nature', '04/15/2024', '14', 56,20),
	('Banias Nature', '04/01/2024', '14', 60,30),
	('Banias Nature', '02/01/2024', '9', 70,30),-- for Usage Report full
	('Banias Nature', '02/01/2024', '10', 60,30),-- for Usage Report
	('Banias Nature', '02/01/2024', '11', 70,30),-- for Usage Report full
	('Banias Nature', '02/01/2024', '12', 60,30),-- for Usage Report
	('Banias Nature', '02/01/2024', '13', 60,30),-- for Usage Report
	('Banias Nature', '02/01/2024', '14', 70,30),-- for Usage Report full
	('Banias Nature', '02/01/2024', '15', 60,30),-- for Usage Report
	('Banias Nature', '02/01/2024', '15', 60,30),-- for Usage Report
	('Banias Nature', '02/02/2024', '10', 60,30);-- for Usage Report


-- Update park Setting requests Table
CREATE TABLE gonature.UpdateSettingTable (
    request_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,  
	MaxVisitors varchar(5),
	MaxReservations varchar(5),
	AverageWaitingTime varchar(5),
	Status varchar(25),
	park_name varchar (25)
);


-- Info For Visitors without reservation
CREATE TABLE gonature.VisitorsWithoutReservation (
  park VARCHAR(25),
  id  VARCHAR(25),
  month VARCHAR(25) ,
  year VARCHAR(25) ,
  numberOfVisitors INT,
  isGroup varchar(25),
  status varchar(25)
);

-- genereteVisitingReport  table (data to  generete visitReport)
CREATE TABLE gonature.genereteVisitingReport(
	keyId varchar(25)  primary key not null,
    enterTime varchar(25) not null,
    exitTime varchar(25),
    year varchar(5),
	month varchar(5),
    isGroup varchar(5)
);

INSERT INTO gonature.genereteVisitingReport VALUES
('key1', '08:15', '14:30', '2024', '03', '1'),
('key2', '08:30', '14:45', '2024', '03', '0'),
('key3', '08:55', '15:20', '2024', '04', '0'),
('key4', '09:40', '15:55', '2024', '03', '1'),
('key5', '10:15', '15:59', '2024', '03', '0'),
('key6', '11:00', '14:15', '2024', '04', '1'),
('key7', '12:30', '15:05', '2024', '04', '0'),
('key8', '08:00', '13:45', '2024', '03', '1'),
('key9', '10:45', '15:30', '2024', '04', '0'),
('key10', '11:50', '12:15', '2024', '03', '1');

-- TotalVisitor
CREATE TABLE gonature.TotalVisitorsReport (
    month VARCHAR(25) NOT NULL,
    year VARCHAR(25) NOT NULL,
    ParkName VARCHAR(25) NOT NULL,
    GroupsNumber VARCHAR(25) ,
    IndividualsNumbers VARCHAR(25) 
);

-- Canceliiation Report :
CREATE TABLE gonature.CancellationReport (
    month VARCHAR(25) NOT NULL,
    year VARCHAR(25) NOT NULL,
    area VARCHAR(25) NOT NULL,
    CanceledOrders VARCHAR(25) ,
    NotVisitedOrders VARCHAR(25) 
    );


CREATE TABLE gonature.waitApprove(
	reservationId INT primary KEY,
    smsTime varchar(10)
);

