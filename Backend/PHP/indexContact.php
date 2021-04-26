<?php
require_once 'jwt/php-jwt/src/BeforeValidException.php';
require_once 'jwt/php-jwt/src/ExpiredException.php';
require_once 'jwt/php-jwt/src/SignatureInvalidException.php';
require_once 'jwt/php-jwt/src/JWT.php';
require_once 'contact.php';
require_once 'key.php';
$contactObject = new Contact();

use Firebase\JWT\JWT;
$key;
$Latitude = "";
$Longitude = "";
$function = "";
$jwtPost = "";
$ContactWithID = "";

if (isset($_POST['function'])) {
    $function = $_POST['function'];
}

if (isset($_POST['jwtPost'])) {
    $jwtPost = $_POST['jwtPost'];
}
if (isset($_POST['Latitude'])) {
    $Latitude = $_POST['Latitude'];
}

if (isset($_POST['Longitude'])) {
    $Longitude = $_POST['Longitude'];
}
if (isset($_POST['ContactWithID'])) {
    $ContactWithID = $_POST['ContactWithID'];
}

// can only do if logged in
if (! empty($jwtPost)) {
    try {
        $decoded = JWT::decode($jwtPost, $key, [
            'HS512'
        ]);
        $decoded_array = (array) $decoded;
        if ($decoded_array['exp'] - $decoded_array['iat'] > 0) {
            $user_id = $decoded_array['userID'];

            if ($function == "Contact" && ! empty($ContactWithID) && ! empty($Longitude) && ! empty($Latitude)) {
                $json = array();
                $json = $contactObject->RecordContact($user_id, $Latitude, $Longitude, $ContactWithID);
                echo json_encode($json);
            }
        }
    } catch (exception $e) {
        $jsonLoginAgain = array();
        $jsonLoginAgain['message'] = "Please Login Again";
        $jsonLoginAgain['response'] = 0;
        echo json_encode($jsonLoginAgain);
    }
}

?>