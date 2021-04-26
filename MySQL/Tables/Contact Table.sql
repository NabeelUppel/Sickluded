use d2090704;
Create Table Contact(
`User_ID` int(20) NOT NULL,
`Contact_Time` timestamp NOT NULL default current_timestamp,
`Latitude` DECIMAL(10, 8) NOT NULL, 
`Longitude` DECIMAL(11, 8) NOT NULL,
`Contact_With_ID` int(20) NOT NULL, 
foreign key(User_ID) references users(user_id) on delete cascade,
foreign key(Contact_With_ID) references users(user_id) on delete cascade
);