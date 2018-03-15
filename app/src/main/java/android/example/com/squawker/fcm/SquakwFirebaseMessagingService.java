package android.example.com.squawker.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by federico.creti on 15/03/2018.
 */

public class SquakwFirebaseMessagingService extends FirebaseMessagingService {
    private static final int SQUAWK_MESSAGE_NOTIFICATION_ID = 541;
    private static final int SQUAWK_PENDING_INTENT_ID = 148;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        if (data != null) {
            String author = data.get("author");
            String message = data.get("message");
            String date = data.get("date");
            String autorKey = data.get("authorKey");

            createNotification(author);

            if (date == null || autorKey == null || author == null || message == null) return;

            ContentValues cv = new ContentValues();
            cv.put("author", author);
            cv.put("message", message);
            cv.put("date", Long.parseLong(date));
            cv.put("authorKey", autorKey);
            saveIntoDb(cv);
        }
    }

    private Uri saveIntoDb(ContentValues cv){
        return getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, cv);
    }

    private void createNotification(String author){
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.) { }*/
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_message, author))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.notification_message, author)))
                .setContentIntent(contentIntent(context))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(SQUAWK_MESSAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
        return largeIcon;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                SQUAWK_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
