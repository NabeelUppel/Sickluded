drop procedure if exists RequestInfected;
delimiter // 
Create procedure RequestInfected(ID int(20))
begin
Select count(*) from Diagnosis where User_ID IN( Select User_ID from Contact where Contact_With_ID = ID);

end//
delimiter ;