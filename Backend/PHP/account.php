<?php
include_once 'db-connect.php';

class Account
{

    private $id;

    private $db;

    private $user_table = "users";

    private $device_table = "DeviceTokens";

    public function __construct()
    {
        $this->id = NULL;
        $this->db = new DbConnect();
    }

    public function getID()
    {
        return $this->id;
    }

    public function getUsername($id)
    {
        $query = "SELECT username FROM " . $this->user_table . " WHERE user_id= ? ";

        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('i', $id);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($username);

            while ($stmt->fetch()) {
                return $username;
            }
            $stmt->close();
        }
    }

    public function getEmail($id)
    {
        $query = "SELECT email FROM " . $this->user_table . " WHERE user_id= ? ";

        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('i', $id);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($email);
            while ($stmt->fetch()) {
                return $email;
            }
            $stmt->close();
        }
    }

    public function getUserID($email)
    {
        $query = "select user_id from " . $this->user_table . " where email = ?";

        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('s', $email);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($user_id);
            while ($stmt->fetch()) {
                return $user_id;
            }
            $stmt->close();
        }
    }

    public function getSalt($email)
    {
        $query = "SELECT salt FROM " . $this->user_table . " WHERE email = ? ";
        // changed from $email to $id
        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('s', $email);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($salt);
            while ($stmt->fetch()) {
                return $salt;
            }
            $stmt->close();
        } else {
            echo "Prepare failed: (" . $this->db->getDb()->errno . ") " . $this->db->getDb()->error;
        }
    }

    private function getPassword($email)
    {
        $query = "SELECT password FROM " . $this->user_table . " WHERE email= ? ";

        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('s', $email);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($password);

            while ($stmt->fetch()) {
                return $password;
            }
            $stmt->close();
        }
    }

    private function ValidPassword($password)
    {
        $len = strlen($password);
        if ($len >= 8) {
            return true;
        }
        return false;
    }

    public function EmailExist($email)
    {
        $query = "select * from " . $this->user_table . " where email = ?";
        if ($stmt = $this->db->getDb()->prepare($query)) {

            $stmt->bind_param('s', $email);

            $stmt->execute();

            $result = $stmt->get_result();

            $num_of_rows = $result->num_rows;

            if ($num_of_rows > 0) {

                return true;
            }
            return false;
        }
    }

    private function UsernameExist($username)
    {
        $query = "select * from " . $this->user_table . " where username = ?";
        if ($stmt = $this->db->getDb()->prepare($query)) {

            $stmt->bind_param('s', $username);

            $stmt->execute();

            $result = $stmt->get_result();

            $num_of_rows = $result->num_rows;

            if ($num_of_rows > 0) {
                return true;
            }
            return false;
        }
    }

    private function ValidEmail($email)
    {
        if (filter_var($email, FILTER_VALIDATE_EMAIL)) {
            return true;
        }
        return false;
    }

    public function generateSalt($randStringLen)
    {
        $charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()*+,;";
        $randString = "";
        for ($i = 0; $i < $randStringLen; $i ++) {
            $randString .= $charset[mt_rand(0, strlen($charset) - 1)];
        }

        return $randString;
    }

    private function LoginExists($email)
    {
        $password = $this->getPassword($email);
        $query = "select * from " . $this->user_table . " where email = ? AND password = ? Limit 1";
        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('ss', $email, $password);
            $stmt->execute();
            $result = $stmt->get_result();
            $num_of_rows = $result->num_rows;
            if ($num_of_rows > 0) {
                return true;
            }
            return false;
        }
    }

    public function addAccount($username, $email, $password)
    {
        $json = array();
        if ($this->EmailExist($email) || $this->UsernameExist($username)) {
            $json['response'] = 0;
            $json['message'] = "An account with that Email Address or Username already exits";
        } else {
            if ($this->ValidEmail($email)) {
                if ($this->ValidPassword($password)) {
                    $salt = $this->generateSalt(32);
                    $hash = password_hash($salt . $password, PASSWORD_DEFAULT);
                    $query = "INSERT INTO " . $this->user_table . " (user_id,username,password, salt ,email, created_at, updated_at) values (NULL ,? ,?, ?,?, NOW(), NOW())";

                    $stmt = $this->db->getDb()->prepare($query);
                    $stmt->bind_param('ssss', $username, $hash, $salt, $email);
                    if ($stmt->execute()) {
                        $id = $this->getUserID($email);
                        $this->id = $id;
                        $json['response'] = 1;
                    }
                } else {
                    $json['response'] = 0;
                    $json['message'] = "Password must be atleast 8 characters";
                }
            } else {
                $json['response'] = 0;
                $json['message'] = "please enter valid email address";
            }
        }

        return $json;
    }

    public function deleteAccount($id, $password)
    {
        $email = $this->getEmail($id);
        if ($this->login($email, $password)) {
            $json = array();
            $query = "DELETE FROM " . $this->user_table . " WHERE user_id = ?";
            if ($stmt = $this->db->getDb()->prepare($query)) {
                $stmt->bind_param('i', $id);
                if ($stmt->execute()) {
                    $json['response'] = 1;
                    $json['message'] = "Your account has been deleted.";
                } else {
                    $json['response'] = 0;
                    $json['message'] = "An Error Has Occured";
                }
            } else {
                $json['response'] = 0;
                $json['message'] = "An Error Has Occured";
            }
        } else {
            $json['response'] = 0;
            $json['message'] = "Incorrect Password";
        }
        return $json;
    }

    public function login($email, $password)
    {
        $json = array();
        if ($this->LoginExists($email)) {
            $salt = $this->getSalt($email);
            $hash = $this->getPassword($email);
            if (password_verify($salt . $password, $hash)) {
                $user_id = $this->getUserID($email);
                $this->id = $user_id;
                return true;
            }
        }
        return false;
    }

    public function editUsername($user_id, $NewUsername)
    {
        $json = array();
        if (! empty($NewUsername)) {
            if (! $this->UsernameExist($NewUsername)) {
                $query = "UPDATE " . $this->user_table . " SET username = ?, updated_at=NOW() WHERE user_id = ?";
                if ($stmt = $this->db->getDb()->prepare($query)) {
                    $stmt->bind_param('si', $NewUsername, $user_id);
                    $stmt->execute();
                    $this->id = $user_id;
                    $json['response'] = 1;
                    $json['message'] = "Successfully updated username";
                } else {
                    "Prepare failed: (" . $this->db->getDb()->errno . ") " . $this->db->getDb()->error;
                    $json['response'] = 0;
                    $json['message'] = "An error has occured";
                }
            } else {
                $json['response'] = 0;
                $json['message'] = "Username unavailable";
            }
        } else {
            $json['response'] = 0;
            $json['message'] = "login to change username";
        }
        return $json;
    }

    public function editEmail($user_id, $NewEmail)
    {
        $json = array();
        if (! empty($NewEmail)) {
            if (! $this->EmailExist($NewEmail) && $this->ValidEmail($NewEmail)) {
                $query = "UPDATE " . $this->user_table . " SET email = ?, updated_at=NOW() WHERE user_id = ?";
                if ($stmt = $this->db->getDb()->prepare($query)) {
                    $stmt->bind_param('si', $NewEmail, $user_id);
                    $this->id = $user_id;
                    $stmt->execute();
                    $json['response'] = 1;
                    $json['message'] = "Successfully updated email address";
                } else {
                    "Prepare failed: (" . $this->db->getDb()->errno . ") " . $this->db->getDb()->error;
                    $json['response'] = 0;
                    $json['message'] = "An error has occured";
                }
            } else {
                $json['response'] = 0;
                $json['message'] = "Invalid email address";
            }
        } else {
            $json['response'] = 0;
            $json['message'] = "Please enter email address";
        }
        return $json;
    }

    public function editPassword($token, $NewPassword)
    {
        $json = array();
        if (! empty($NewPassword)) {
            if (! empty($NewPassword) && $this->ValidPassword($NewPassword)) {
                $salt = $this->generateSalt(32);
                $hash = password_hash($salt . $NewPassword, PASSWORD_DEFAULT);
                $query = "UPDATE " . $this->user_table . " SET password = ?, salt=?, updated_at=NOW() WHERE password_reset_token= ?";
                if ($stmt = $this->db->getDb()->prepare($query)) {
                    $stmt->bind_param('sss', $hash, $salt, $token);
                    $stmt->execute();
                    if ($this->deleteToken($token)) {
                        $json['response'] = 1;
                        $json['message'] = "Successfully updated password";
                    }
                } else {
                    $json['response'] = 0;
                    $json['message'] = "An error has occured";
                }
            } else {
                $json['response'] = 0;
                $json['message'] = "Invalid password";
            }
        }
        return $json;
    }

    public function validToken($token)
    {
        $t = $this->getToken($token); // see if it exists.
        if (! empty($t)) {
            $e = $this->getExpTime($t); // get expiration time
            $now = time(); // time now
            $exp = strtotime($e); // convert expiration time to seconds.
            if ($exp - $now > 0) { // compare
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public function addToken($email)
    {
        $query = "update " . $this->user_table . " set password_reset_token=?, updated_at =NOW() ,token_expiration = Date_add(NOW(), INTERVAL 3 hour ) where email = ?";
        $token = bin2hex(openssl_random_pseudo_bytes(64));
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('ss', $token, $email);
        if ($stmt->execute()) {
            return $token;
        }
    }

    public function deleteToken($token)
    {
        $query = "update " . $this->user_table . " set password_reset_token=? ,token_expiration = ?  where password_reset_token = ?";
        $n1 = null;
        $n2 = null;
        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('sss', $n1, $n2, $token);
            $stmt->execute();
            if ($stmt->execute()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public function getToken($token)
    {
        $query = "select password_reset_token from " . $this->user_table . " where password_reset_token = ?";

        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('s', $token);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($Token);
            while ($stmt->fetch()) {
                return $Token;
            }
            $stmt->close();
        }
    }

    public function getExpTime($token)
    {
        $query = "select token_expiration from " . $this->user_table . " where password_reset_token = ?";

        if ($stmt = $this->db->getDb()->prepare($query)) {
            $stmt->bind_param('s', $token);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($time);
            while ($stmt->fetch()) {
                return $time;
            }
            $stmt->close();
        }
    }

    public function clearTokens()
    {
        $query = "CALL clearTokens();";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->execute();
    }

    public function addDeviceToken($user_id, $deviceToken)
    {
        $query = "Replace INTO " . $this->device_table . " (User_ID, Device_Token) values (?,?)";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('is', $user_id, $deviceToken);
        if ($stmt->execute()) {
            return true;
        } else {
            echo ("Statement failed: " . $stmt->error . "<br>");
            echo ("Statement failed: " . $stmt->errno . "<br>");
            return false;
        }
    }

    public function logout($DeviceToken)
    {
        $query = "DELETE FROM " . $this->device_table . " WHERE Device_Token = ?";
        $stmt = $this->db->getDb()->prepare($query);
        $stmt->bind_param('s', $DeviceToken);
        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
    }
}
?>