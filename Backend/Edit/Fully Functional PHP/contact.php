<?php
include_once 'db-connect.php';

class Contact
{
    private $db;

    private $contact_table = "Contact";

 public function __construct()
    {
        $this->db = new DbConnect();
    }

public function RecordContact($user_id, $latitude, $longitude, $contactWithID){
        $json = array();
                    $query = "INSERT INTO " . $this->contact_table . " (User_ID, Contact_Time, Latitude, Longitude, Contact_With_ID) values (? ,NOW() ,?, ?, ?)";
                    $stmt = $this->db->getDb()->prepare($query);
                    $stmt->bind_param('iddi', $user_id, $latitude, $longitude, $contactWithID);
                    if ($stmt->execute()) {
                    	$json['response'] =1;
                    	$json['message'] = "You've been in contact with".$contactWithID;
                    	echo "here";
                    }else{
                    	$json['response'] =0;
                    	
                    }
        return $json;
    
}

}
?>