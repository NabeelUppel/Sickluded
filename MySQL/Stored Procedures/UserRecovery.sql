drop procedure if exists UserRecovery;
delimiter // 
create procedure UserRecovery(id int(20))
Begin
Declare a varchar(255);
declare endcount int;
declare startcount int default 0;
DECLARE cur1 CURSOR FOR select Location_ID from UserLocations where User_ID = id;

open cur1;
SELECT 
    COUNT(*)
FROM
    UserLocations
WHERE
    User_ID = id INTO endcount;
while startcount < endcount do
FETCH cur1 INTO a;
UPDATE Location 
SET 
    `InfectedCount` = IF(InfectedCount > 0,
        InfectedCount - 1,
        0)
WHERE
    Location_ID = (SELECT a);
    set startcount=startcount+1;
  end while;
  CLOSE cur1;




DELETE FROM Diagnosis 
WHERE
    User_ID = id;
end//
delimiter ;

