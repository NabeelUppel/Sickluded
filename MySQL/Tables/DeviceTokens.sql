Create table DeviceTokens(
`User_ID`int(20),  
foreign key (User_ID) references users(user_id) on delete cascade,
`Device_Token` varchar(255) unique 
);