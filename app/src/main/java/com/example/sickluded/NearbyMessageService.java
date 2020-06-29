package com.example.sickluded;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class NearbyMessageService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static String TAG = "NearbyMessage";
    private MessageListener mMessageListener;
    private Message message;

    public NearbyMessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //find new place for this
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            MessagesClient mMessagesClient = Nearby.getMessagesClient(this, new MessagesOptions.Builder()
                    .setPermissions(NearbyPermissions.BLE)
                    .build());

            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);

        }

        final String userId = SharedPreferenceClass.getData(getApplicationContext(), "ID");
        final String JWT = SharedPreferenceClass.getData(getApplicationContext(), "jwt");
        message = new Message(userId.getBytes());


        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                final String userMetID = new String(message.getContent());
                System.out.println(userMetID);
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.getFusedLocationProviderClient(getApplicationContext()).getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();

                            RecordContact(userMetID, JWT, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        }
                    }
                });
                Log.d(TAG, "Found message: " + new String(message.getContent()));
            }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Starting tracker", Toast.LENGTH_LONG).show();
        Nearby.getMessagesClient(this).publish(message);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        createNotificationChannel();
        buildNotification();
        Intent local = new Intent();
        local.setAction("com.hello.action");
        this.sendBroadcast(local);
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void RecordContact(String UserMetID, String JWT, String Lat, String lng) {
        ContentValues params = new ContentValues();
        params.put("function", "Contact");
        params.put("ContactWithID", UserMetID);
        params.put("jwtPost", JWT);
        params.put("Latitude", Lat);
        params.put("Longitude", lng);
        String URL = " https://lamp.ms.wits.ac.za/home/s2090704/indexContact.php";
        new PhpHandler().ContactHttp(URL, params);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nearby";
            String description = "Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);
            //Stop the Service//
            stopSelf();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

// Create the persistent notification//

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.tracking_enabled_notification))
                .setContentText(getString(R.string.stopTracker))

//Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_tracker);
        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Nearby.getMessagesClient(this).unpublish(message);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);

        Intent local = new Intent();
        local.setAction("com.goodbye.action");
        this.sendBroadcast(local);
    }


}




