<?php
include_once 'db-connect.php';

class Location{
	
    private $db;
private $placeID;
    private $location_table = "Location";
private $Userslocations_table = "UserLocations";

    
    public function __construct()
    {
	$this->placeID = null;
        $this->db = new DbConnect();
    }
    
    public function getPlace_ID()
    {
        return $this->placeID;
    }

    
public function AddLocation($place_id, $place_title,$latitude, $longitude){
                    $query = "INSERT IGNORE INTO " . $this->location_table . " (Location_ID ,Location_Title, Latitude, Longitude) values (?,? ,?, ?)";
                    $stmt = $this->db->getDb()->prepare($query);
                    $stmt->bind_param('ssdd', $place_id, $place_title, $latitude, $longitude);
                    if ($stmt->execute()) {
                    	$this->placeID = $place_id;
					return true;
                    }else{
                   echo("Statement failed: ". $stmt->error . "<br>");
                   echo("Statement failed: ". $stmt->errno . "<br>");
                   
                    	return false;  
}  
}

public function RecordUserLocation($user_id, $place_ID, $time){
	
$timestamp =date('Y-m-d H:i:s',strtotime($time));
 $query = "INSERT INTO " . $this->Userslocations_table . " (User_ID ,Location_ID,time) values (?,?,?)";
 $stmt = $this->db->getDb()->prepare($query);
                    $stmt->bind_param('iss', $user_id, $place_ID ,$timestamp);
                    if ($stmt->execute()) {
					return true;
                    }else{
                   echo("Statement failed: ". $stmt->error . "<br>");
                   echo("Statement failed: ". $stmt->errno . "<br>");
                    
                    	return false;  
} 
}



public function ReturnLocations(){
	$return_arr = array();
	$row_array = array();
	$query = "SELECT Location_ID ,Location_Title, Latitude, Longitude FROM " . $this->location_table ."";
 $stmt = $this->db->getDb()->query($query);
 while ($row = mysqli_fetch_assoc($stmt)) {
    $row_array['Location_ID'] = $row['Location_ID'];
    $row_array['Location_Title'] = $row['Location_Title'];
    $row_array['Latitude'] = $row['Latitude'];
    $row_array['Longitude'] = $row['Longitude'];

    array_push($return_arr,$row_array);
}

                    
return $return_arr;
}


 
public function ReturnSingleUserLocations($user_id){
	$return_arr = array();
	$row_array = array();
	$query = "call getUserLocations(?)";
	
 $stmt = $this->db->getDb()->prepare($query);
 $stmt->bind_param('i', $user_id);
 
$stmt->execute();

$result=$stmt->get_result();
 while ($row = mysqli_fetch_assoc($result)) {
    $row_array['Location_ID'] = $row['Location_ID'];
    $row_array['Location_Title'] = $row['Location_Title'];
    $row_array['Latitude'] = $row['Latitude'];
    $row_array['Longitude'] = $row['Longitude'];

    array_push($return_arr,$row_array);
}

                    
return $return_arr;
}


}
?>
