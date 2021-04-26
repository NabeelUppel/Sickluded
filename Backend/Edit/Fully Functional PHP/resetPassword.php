<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\SMTP;
require 'PHPMailer/src/Exception.php';
require 'PHPMailer/src/PHPMailer.php';
require 'PHPMailer/src/SMTP.php';
require_once 'account.php';
$accountObject = new Account();

$From = 'no-reply@adblikethatsometimes.com';
$email = "";

if (isset($_POST['email'])) {
    $email = $_POST['email'];
}

$mail = new PHPMailer(true);
if (! empty($email) && $accountObject->EmailExist($email)) {
    $token = $accountObject->addToken($email);
    if (! empty($token)) {
        try {
            // Server settings
            $mail->isSMTP(); // Send using SMTP
            $mail->SMTPSecure = 'ssl';
            $mail->Host = 'smtp.gmail.com'; // Set the SMTP server to send through
            $mail->SMTPAuth = true; // Enable SMTP authentication
            $mail->Username = 'adblikethatsometimes@gmail.com'; // SMTP username
            $mail->Password = 'android1studio'; // SMTP password
            $mail->Port = 465; // TCP port to connect to, use 465 for `PHPMailer::ENCRYPTION_SMTPS` above

            // Recipients
            $mail->setFrom($From, 'No-Reply');
            $mail->addAddress($email); // Add a recipient

            // Content
            $mail->isHTML(true); // Set email format to HTML
            $mail->Subject = 'Password Reset';
            $mail->Body = "https://lamp.ms.wits.ac.za/home/s2090704/ResetPasswordRedirect.php?token=" . $token;

            $mail->send();
            echo 'An email has been sent to reset your password';
        } catch (Exception $e) {
            echo "Please try again.";
        }
    }
} else {
    echo "Please enter your email address";
}

?>