CREATE DATABASE `eventsdb`;

CREATE TABLE `eventsdb`.`event` (
  `Event_Id` INT NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(45) NULL,
  `Description` VARCHAR(200) NULL,
  `Time` DATETIME NOT NULL,
  `Location` VARCHAR(100) NOT NULL,
  `Owner_Id` INT NULL,
  PRIMARY KEY (`Event_Id`));