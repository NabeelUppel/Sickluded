<?php
require_once 'jwt/php-jwt/src/BeforeValidException.php';
require_once 'jwt/php-jwt/src/ExpiredException.php';
require_once 'jwt/php-jwt/src/SignatureInvalidException.php';
require_once 'jwt/php-jwt/src/JWT.php';
require_once 'account.php';
require_once  'key.php';
$accountObject = new Account();

use \Firebase\JWT\JWT;
$key;
$username = "";
$password = "";
$email = "";
$function = "";
$newUsername = "";
$newPassword = "";
$resetPasswordToken="";
$newEmail="";
$jwtPost="";


// isset checks if not null.
if (isset($_POST['username'])) {
    $username = $_POST['username'];
}

if (isset($_POST['password'])) {
    $password = $_POST['password'];
}

if (isset($_POST['email'])) {
    $email = $_POST['email'];
}
if (isset($_POST['function'])) {
    $function = $_POST['function'];
}

if (isset($_POST['newUsername'])) {
    $newUsername = $_POST['newUsername'];
}

if (isset($_POST['newPassword'])) {
    $newPassword = $_POST['newPassword'];
}


if (isset($_POST['Token'])) {
    $resetPasswordToken = $_POST['Token'];
}

if (isset($_POST['jwtPost'])) {
    $jwtPost = $_POST['jwtPost'];
}

if (isset($_POST['newEmail'])) {
    $newEmail = $_POST['newEmail'];
}



// Login
if (! empty($email) && ! empty($password) && $function == "Login") {
    $json_login = array();
    if($accountObject->login( $email, $password)){
        $tokenId    = base64_encode($accountObject->generateSalt(32));
        $issuer_claim = "Lamp"; // this can be the servername
        $issuedat_claim = time(); // issued at
        $expire_claim = $issuedat_claim +7776000; 
        $token = [
           'jti'  => base64_encode($tokenId),
            "iss" => $issuer_claim,
            "iat" => $issuedat_claim,
            "exp" => $expire_claim,
             'username' => $accountObject->getUsername($accountObject->getID()),
             'email' => $email,
             'userID'=>$accountObject->getID()
             ];
             
                $jwt = JWT::encode($token, $key,'HS512');
              
                $json_login['jwt'] = $jwt;
     		$json_login['message'] = "Successful login.";
     		$json_login['response'] = 1;
    }else{
    	$json_login['response'] = 0;
     $json_login['message'] = "Failed to login";
    }
    echo json_encode($json_login);
            }

// Registration
if (! empty($username) && ! empty($password) && ! empty($email) && $function == "Register") {
    $json_registration = array();
    $json_registration = $accountObject->addAccount($username, $email, $password);
    if($json_registration['response'] == 1){
      
       $tokenId   = base64_encode($accountObject->generateSalt(32));
        $issuer_claim = "Lamp"; 
        $issuedat_claim = time(); // issued at
        $expire_claim = $issuedat_claim + 7776000; // expire time in seconds (90 days)
        $token = [
           'jti'  => base64_encode($tokenId),
            "iss" => $issuer_claim,
            "iat" => $issuedat_claim,
            "exp" => $expire_claim,
             'username' => $accountObject->getUsername($accountObject->getID()),
             'email' => $email,
              'userID'=>$accountObject->getID()
             ];
           
                $jwt = JWT::encode($token, $key,'HS512');
					$json['message'] = "Successfully Registered.";
               		$json['jwt'] = $jwt;
                         $json['response'] = 1;
 echo json_encode($json);
    }else{    
    echo json_encode($json_registration);
    }
}

//Logout
if ($function == "Logout") {
        	echo "Goodbye!";
}


//forgot password
if (!empty($newPassword) && $function == "editPassword" && !empty($resetPasswordToken)) {
    $jsonEditPassword = array();
    if($accountObject->validToken($resetPasswordToken)){
    $jsonEditPassword = $accountObject->editPassword($resetPasswordToken, $newPassword);
    echo json_encode($jsonEditPassword);
    }else{
    	$jsonEditPassword['response'] = 0;
    	$jsonEditPassword['message'] = "An error occured,please try again";
      echo json_encode($jsonEditPassword);
    }
}


//can only do if logged in
if ( !empty($jwtPost)) {
try{$decoded = JWT::decode($jwtPost, $key, ['HS512']);
$decoded_array = (array) $decoded;
if($decoded_array['exp']-$decoded_array['iat'] > 0){
$user_id = $decoded_array['userID'];

//change username
if (! empty($newUsername) && $function == "editUsername") {
    $jsonEditUsername = array();
    $jsonEditUsername = $accountObject->editUsername($user_id, $newUsername);
if($jsonEditUsername['response'] == 1){
$tokenId    = base64_encode($accountObject->generateSalt(32));
        $issuer_claim = "Lamp"; // this can be the servername
        $issuedat_claim = time(); // issued at
        $expire_claim = $issuedat_claim +7776000; 
        $token = [
           'jti'  => base64_encode($tokenId),
            "iss" => $issuer_claim,
            "iat" => $issuedat_claim,
            "exp" => $expire_claim,
             'message'=>"Successfully Logged In",
             'username' => $accountObject->getUsername($user_id),
             'email' => $email,
             'userID'=>$accountObject->getID()
             ];
             
                $jwt = JWT::encode($token, $key,'HS512');
                 $jsonEditUsername['jwt'] = $jwt;
	
   
}
 echo json_encode($jsonEditUsername);
}

if (! empty($newEmail) && $function == "editEmail") {
    $jsonEditEmail = array();
    $jsonEditEmail = $accountObject->editEmail($user_id, $newEmail);
if($jsonEditUsername['response'] == 1){
$tokenId    = base64_encode($accountObject->generateSalt(32));
        $issuer_claim = "Lamp"; // this can be the servername
        $issuedat_claim = time(); // issued at
        $expire_claim = $issuedat_claim +7776000; 
        $token = [
           'jti'  => base64_encode($tokenId),
            "iss" => $issuer_claim,
            "iat" => $issuedat_claim,
            "exp" => $expire_claim,
             'message'=>"Successfully Logged In",
             'username' => $accountObject->getUsername($user_id),
             'email' => $email,
             'userID'=>$accountObject->getID()
             ];
             
                $jwt = JWT::encode($token, $key,'HS512');
                 $jsonEditEmail['jwt'] = $jwt;
	
   
}
 echo json_encode($jsonEditUsername);
}


if ($function == "deleteAccount") {
$accountObject->deleteAccount($user_id);
}


}


}catch(exception $e){
	$jsonLoginAgain = array();
	$jsonLoginAgain['message'] = "Please Login Again";
     $jsonLoginAgain['response'] = 0;
     echo json_encode($jsonLoginAgain);
}


}


?>