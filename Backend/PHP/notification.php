<?php
$fcmAPI = "AAAAjxMma0Q:APA91bHZbmIYn1BdYSe34JPLkyRkewy0QLTPppin2a882ps0-6NV3uGf1eSZZO3yqTbJp-6KBk5F-ALcMMyhUEfMPuesBt8fUgnjBsHs91gPGLQk9uwvimSVMQaBxvZVRzsO94i0oaUj";
define('API_ACCESS_KEY', $fcmAPI);

class Notification
{

    public function __construct()
    {}

    public function sendContactNotification($DeviceTokens)
    {
        $fcmUrl = 'https://fcm.googleapis.com/fcm/send';

        $notification = [
            'title' => "Contact with Covid-19",
            'body' => "You've been in contact with an individual who has been diagnosed with Covid-19"
        ];

        $fcmNotification = [
            'registration_ids' => $DeviceTokens, // multple token array
            'notification' => $notification,
            'data' => $notification
        ];

        $headers = [
            'Authorization: key=' . API_ACCESS_KEY,
            'Content-Type: application/json'
        ];

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $fcmUrl);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fcmNotification));
        $result = curl_exec($ch);
        curl_close($ch);
    }
}
?>
        