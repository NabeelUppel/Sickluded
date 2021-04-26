use d2090704;


DROP PROCEDURE IF EXISTS getUserLocations;
delimiter //
create procedure getUserLocations(ID int(20))
Begin
create temporary table if not exists SingleUserLocations (select distinct Location_ID from UserLocations where User_ID = ID);
select * from Location natural Join SingleUserLocations;
end//
delimiter ;