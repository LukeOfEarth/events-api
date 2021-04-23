CREATE TABLE IF NOT EXISTS `eventsdb`.`event_user` (
	`Event_Id` INT NOT NULL,
	`User_Id` INT NOT NULL,
	`Status` INT NOT NULL,
	FOREIGN KEY (`Event_Id`) REFERENCES event(Event_Id)	,
	FOREIGN KEY (`User_Id`) REFERENCES user(User_Id),
	PRIMARY KEY (Event_Id,User_Id)
	);