<?php
require_once 'jwt/php-jwt/src/BeforeValidException.php';
require_once 'jwt/php-jwt/src/ExpiredException.php';
require_once 'jwt/php-jwt/src/SignatureInvalidException.php';
require_once 'jwt/php-jwt/src/JWT.php';
require_once 'diagnosis.php';
require_once 'notification.php';
require_once 'key.php';
require_once 'sendContactEmail.php';
$diagnosisObject = new Diagnosis();
$notificationObject = new Notification();
use Firebase\JWT\JWT;

$key;
$function = "";
$jwtPost = "";
$time = " ";

if (isset($_POST['function'])) {
    $function = $_POST['function'];
}

if (isset($_POST['jwtPost'])) {
    $jwtPost = $_POST['jwtPost'];
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

            if ($function == "Diagnose" && ! empty($time)) {
                if ($diagnosisObject->RecordDiagnosis($user_id, $time)) {
                    $diagnosisObject->TwoWeekCleanUp();
                    $diagnosisObject->AddInfectedCount($user_id);
                    $deviceArray = array();
                    $emailArray = array();
                    $emailArray = $diagnosisObject->GetContactEmails($user_id);
                    $deviceArray = $diagnosisObject->GetUserContact($user_id);
                    $notificationObject->sendContactNotification($deviceArray);
                    sendEmails($emailArray);
                    echo "Diagnosis Recorded";
                }
            }

            if ($function == "Request") {
                $infected = array();
                $diagnosisObject->TwoWeekCleanUp();
                $infected = $diagnosisObject->RequestInfected($user_id);
                echo json_encode($infected);
            }

            if ($function == "UserRecovery") {
                if ($diagnosisObject->UserRecovery($user_id)) {
                    echo "Diagnosis Recorded";
                } else {
                    echo "No Existing Diagnosis";
                }
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
