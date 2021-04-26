<?php
require_once 'jwt/php-jwt/src/BeforeValidException.php';
require_once 'jwt/php-jwt/src/ExpiredException.php';
require_once 'jwt/php-jwt/src/SignatureInvalidException.php';
require_once 'jwt/php-jwt/src/JWT.php';
require_once 'location.php';
require_once 'key.php';
$locationObject = new Location();

use Firebase\JWT\JWT;
$key;
$Latitude = "";
$Longitude = "";
$function = "";
$jwtPost = "";
$placeID = "";
$place_title = "";
$time = "";

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
if (isset($_POST['placeID'])) {
    $placeID = $_POST['placeID'];
}
if (isset($_POST['place_title'])) {
    $place_title = $_POST['place_title'];
}

if (isset($_POST['time'])) {
    $time = $_POST['time'];
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

            if ($function == "AddLocation" && ! empty($placeID) && ! empty($Longitude) && ! empty($Latitude)) {
                if ($locationObject->AddLocation($placeID, $place_title, $Latitude, $Longitude)) {
                    echo "added";
                }
            }

            if ($function == "RecordLocation" && ! empty($time) && ! empty($placeID)) {
                if ($locationObject->RecordUserLocation($user_id, $placeID, $time)) {
                    echo "Successfully Recorded Location";
                } else {
                    echo "Failed to Record Location";
                }
            }

            if ($function == "ReturnUserLocations") {
                $arr = array();
                $arr = $locationObject->ReturnSingleUserLocations($user_id);
                echo json_encode($arr);
            }

            if ($function == "ReturnAllLocations") {
                $arr = array();
                $arr = $locationObject->ReturnLocations();
                echo json_encode($arr);
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
