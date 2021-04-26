drop procedure if exists TwoWeekCleanUp;
delimiter // 
Create procedure TwoWeekCleanUp()
begin
Declare a varchar(255);
declare endcount int;
declare startcount int default 0;
DECLARE cur1 CURSOR FOR select Location_ID from UserLocations where(datediff(now(), time) >= 14);

open cur1;
SELECT 
    COUNT(*)
FROM
    UserLocations
WHERE
    (DATEDIFF(NOW(), time) >= 14) INTO endcount;
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
    
DELETE FROM Contact 
WHERE
    (DATEDIFF(NOW(), Contact_Time) >= 14);
DELETE FROM UserLocations 
WHERE
    (DATEDIFF(NOW(), time) >= 14);
DELETE FROM Diagnosis 
WHERE
    (DATEDIFF(NOW(), Date) >= 14);

end//
delimiter ;
