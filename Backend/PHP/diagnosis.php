<?php
include_once 'db-connect.php';

class Diagnosis
{

    private $db;

    private $diagnosis_table = "Diagnosis";

    private $contact_table = "Contact";

    private $Userslocations_table = "UserLocations";

    private $device_table = "DeviceTokens";

    private $location_table = "Location";

    private $user_table = "users";

    public function __construct()
    {
        $this->db = new DbConnect();
    }

    public function getPlace_ID()
    {
        return $this->placeID;
    }

    public function RecordDiagnosis($user_id, $time)
    {
        $timestamp = date('Y-m-d', strtotime($time));
        $query = "Replace INTO " . $this->diagnosis_table . " (User_ID, Date) values (?,?)";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('is', $user_id, $timestamp);
        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
    }

    public function GetUserContact($user_id)
    {
        $this->UserContact();
        $return_arr = array();
        $return_arr1 = array();
        $row1 = array();
        $query = "SELECT Distinct Contact_With_ID from " . $this->contact_table . " where User_ID = ?";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('i', $user_id);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($Contact_With_ID);

        while ($stmt->fetch()) {

            array_push($return_arr, $Contact_With_ID);
        }

        if (! empty($return_arr)) {
            $query1 = "Select Device_Token from " . $this->device_table . " where User_ID IN (" . implode(',', $return_arr) . ")  ";
            $stmt1 = $this->db->getDb()->prepare($query1);
            $stmt1->execute();
            $stmt1->store_result();
            $stmt1->bind_result($Device_Token);

            while ($stmt1->fetch()) {
                array_push($row1, $Device_Token);
            }
            return $row1;
        }
    }

    public function GetContactEmails($user_id)
    {
        $this->UserContact();
        $return_arr = array();
        $return_arr1 = array();
        $row1 = array();
        $query = "SELECT Distinct Contact_With_ID from " . $this->contact_table . " where User_ID = ?";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('i', $user_id);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($Contact_With_ID);

        while ($stmt->fetch()) {

            array_push($return_arr, $Contact_With_ID);
        }

        if (! empty($return_arr)) {
            $query1 = "Select email from " . $this->user_table . " where user_id IN (" . implode(',', $return_arr) . ")  ";
            $stmt1 = $this->db->getDb()->prepare($query1);
            $stmt1->execute();
            $stmt1->store_result();
            $stmt1->bind_result($email);

            while ($stmt1->fetch()) {
                array_push($row1, $email);
            }
            return $row1;
        }
    }

    public function AddInfectedCount($user_id)
    {
        $return_arr = array();
        $return_arr1 = array();
        $row_array1 = array();
        $query = "SELECT  Location_ID from " . $this->Userslocations_table . " where User_ID = ?";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('i', $user_id);

        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($Location_ID);

        while ($stmt->fetch()) {

            array_push($return_arr, $Location_ID);
        }

        $query1 = 'Update ' . $this->location_table . ' set InfectedCount = InfectedCount + 1 where Location_ID IN ("' . implode('", "', $return_arr) . '")';
        $stmt1 = $this->db->getDb()->query($query1);
    }

    public function UserContact()
    {
        $query = "call UserContact()";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->execute();
    }

    public function RequestInfected($user_id)
    {
        $json = array();
        $query = "call RequestInfected(?)";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('i', $user_id);

        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($ContactCount);

        while ($stmt->fetch()) {
            if ($ContactCount > 0) {
                $json['message'] = "You've been in contact with $ContactCount individual(s) who has been diagnosed with Covid-19 in the last 2 weeks.";
                $json['response'] = true;
            } else {
                $json['message'] = "You've not been in contact with Covid-19.";
                $json['response'] = false;
            }
        }

        return $json;
    }

    public function TwoWeekCleanUp()
    {
        $query = "call TwoWeekCleanUp()";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->execute();
    }

    public function UserRecovery($user_id)
    {
        $json = array();
        $query = "call UserRecovery(?)";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('i', $user_id);

        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
    }
}
?>
