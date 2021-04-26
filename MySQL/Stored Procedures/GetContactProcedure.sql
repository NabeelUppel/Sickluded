drop procedure if exists UserContact;

delimiter //
create procedure  UserContact()
begin

DECLARE anyVariableName1 INT DEFAULT 0;
DECLARE anyVariableName2 INT DEFAULT 0;

create temporary table if not exists UserContact(
select UserLocations.User_ID, 
Location.Latitude as Latitude, 
Location.Longitude as Longitude,
UserLocations.time as time1, 
temp.User_ID as Contact_With_ID,
temp.Location_ID, 
temp.time as time2
from UserLocations 
left join(
Select distinct * from UserLocations group by 1,2
) 
temp using (Location_ID)
natural join (Location)
where UserLocations.User_ID!=temp.User_ID 
and (datediff(UserLocations.time,temp.time)=0)
and (Hour(timediff(UserLocations.time,temp.time))<=1));


SELECT 
    COUNT(User_ID)
FROM
    UserContact INTO anyVariableName1;
SET anyVariableName2 =0;

 WHILE anyVariableName2 < anyVariableName1 DO
   Replace INTO Contact(User_ID, Contact_Time, Latitude,Longitude, Contact_With_ID) 
   SELECT User_ID, time1, Latitude,Longitude,Contact_With_ID
	FROM UserContact LIMIT anyVariableName2, 1;
   SET anyVariableName2 = anyVariableName2+1;
END WHILE;
end//
delimiter ;