use d2090704;
Create Table UserLocations(
`User_ID` int(20) not null,
`Location_ID` varchar (255) not null,
`time` timestamp NOT NULL default current_timestamp,
foreign key(User_ID) references users(user_id) on delete cascade,
foreign key(Location_ID) references Location(Location_ID) on delete cascade
);