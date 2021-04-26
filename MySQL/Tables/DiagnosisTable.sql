use d2090704;
create table Diagnosis(
`User_ID` int(20) unique, 
`Date` Date not null,
foreign key(User_ID) references users(user_id) on delete cascade
);