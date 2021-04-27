CREATE DATABASE
IF NOT EXISTS `eventsdb`;

CREATE TABLE
IF NOT EXISTS `eventsdb`.`user`
(
  `User_Id` INT NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR
(45) NULL,
  `Email` VARCHAR
(45) NULL,
  `Status` INT DEFAULT 0,
  PRIMARY KEY
(`User_Id`));

ALTER TABLE `eventsdb`.`user` 
ADD `Auth_Id` VARCHAR
(30) NOT NULL ;