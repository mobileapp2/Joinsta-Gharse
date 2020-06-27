package in.oriange.joinstagharse.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Random;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.EnquiriesActivity;
import in.oriange.joinstagharse.activities.NotificationActivity;
import in.oriange.joinstagharse.activities.SplashScreenActivity;
import in.oriange.joinstagharse.utilities.UserSessionManager;

public class FirebaseMessageService extends FirebaseMessagingService {

    private final static String TAG = FirebaseMessageService.class.getSimpleName();

    public static final int NOTIFICATION_REQUEST_CODE = 100;
    private static PendingIntent pendingIntent;
    private static NotificationCompat.Builder builder;
    private static NotificationManagerCompat notificationManager;
    private static Uri notificationSound;
    private static Bitmap iconBitmap;
    private static Random random;

    @Override
    public void onNewToken(String s) {
        android.util.Log.e("NEW_TOKEN", s);
        UserSessionManager session = new UserSessionManager(FirebaseMessageService.this);

        String token = FirebaseInstanceId.getInstance().getToken();
        android.util.Log.d("TokenID", "" + token);

        if (token != null) {
            session.createAndroidToken(token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        android.util.Log.wtf(TAG, remoteMessage.getData().get("title") + " " +
                remoteMessage.getData().get("message") + " " +
                remoteMessage.getData().get("image") + " " +
                remoteMessage.getData().get("icon") + " " +
                remoteMessage.getData().get("type") + " " +
                remoteMessage.getData().get("userId") + " " +
                remoteMessage.getData().get("taskId"));


        remoteMessage.getData();

        Intent notificationIntent = null;

        if (remoteMessage.getData().get("notification_type").equals("1")) {     // General Notifications
            notificationIntent = new Intent(getApplicationContext(), NotificationActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (remoteMessage.getData().get("notification_type").equals("3")) {     // Enquiry Notifications
            notificationIntent = new Intent(getApplicationContext(), EnquiriesActivity.class)
                    .putExtra("businessId", remoteMessage.getData().get("business_id"));
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (remoteMessage.getData().get("notification_type").equals("4")) {     // Chat Notifications
//            if (ChatActivity.isChatActivityRunning)
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("ChatActivity"));
//            else {
//                notificationIntent = new Intent(getApplicationContext(), ChatActivity.class)
//                        .putExtra("orderId", remoteMessage.getData().get("order_id"))
//                        .putExtra("name", remoteMessage.getData().get("title"))
//                        .putExtra("sendTo", remoteMessage.getData().get("send_by"));
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            }
        } else {
            notificationIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        if (remoteMessage.getData().get("notification_type").equals("1")) {
            if (remoteMessage.getData().get("image") != null && remoteMessage.getData().get("image").isEmpty()) {
                showNewNotification(
                        getApplicationContext(),
                        notificationIntent,
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("message"),
                        remoteMessage.getData().get("image"),
                        remoteMessage.getData().get("icon"),
                        remoteMessage.getData().get("type"),
                        remoteMessage.getData().get("userId"),
                        remoteMessage.getData().get("taskId"),
                        remoteMessage.getData().get("notification_type"),
                        remoteMessage.getData().get("group_id"),
                        remoteMessage.getData().get("msg_id"),
                        remoteMessage.getData().get("group_name"));
            } else {
                generatepicture(
                        getApplicationContext(),
                        null,
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("message"),
                        remoteMessage.getData().get("image"),
                        remoteMessage.getData().get("notification_type"),
                        remoteMessage.getData().get("group_id"),
                        remoteMessage.getData().get("msg_id"),
                        remoteMessage.getData().get("group_name"));
            }
        } else if (remoteMessage.getData().get("notification_type").equals("3")) {
            showEnquiryNotification(
                    getApplicationContext(),
                    notificationIntent,
                    remoteMessage.getData().get("enquiry_title"),
                    remoteMessage.getData().get("business_name") + "\n" + remoteMessage.getData().get("from_user") + "\n" + remoteMessage.getData().get("subject") + " - " + remoteMessage.getData().get("msg"),
                    remoteMessage.getData().get("communication_mode"),
                    remoteMessage.getData().get("mobile"),
                    remoteMessage.getData().get("email")
            );
        } else if (remoteMessage.getData().get("notification_type").equals("4")) {
//            if (!ChatActivity.isChatActivityRunning) {
//                if (remoteMessage.getData().get("chat_doc_type").equals("0") || remoteMessage.getData().get("chat_doc_type").equals("2")) {
//                    showNewNotification(
//                            getApplicationContext(),
//                            notificationIntent,
//                            remoteMessage.getData().get("title"),
//                            remoteMessage.getData().get("chat_text"),
//                            remoteMessage.getData().get("image"),
//                            remoteMessage.getData().get("icon"),
//                            remoteMessage.getData().get("type"),
//                            remoteMessage.getData().get("userId"),
//                            remoteMessage.getData().get("taskId"),
//                            remoteMessage.getData().get("notification_type"),
//                            remoteMessage.getData().get("group_id"),
//                            remoteMessage.getData().get("msg_id"),
//                            remoteMessage.getData().get("group_name"));
//                } else if (remoteMessage.getData().get("chat_doc_type").equals("1")) {
//                    generatepicture(
//                            getApplicationContext(),
//                            notificationIntent,
//                            remoteMessage.getData().get("title"),
//                            remoteMessage.getData().get("chat_text"),
//                            IMAGE_LINK + "chat/" + remoteMessage.getData().get("image"),
//                            remoteMessage.getData().get("notification_type"),
//                            remoteMessage.getData().get("group_id"),
//                            remoteMessage.getData().get("msg_id"),
//                            remoteMessage.getData().get("group_name"));
//                }
//            }
        }
    }

    public static void showNewNotification(Context context, Intent intent, String title, String msg, String image, String icon, String type, String userId,
                                           String taskId, String notification_type, String group_id, String msg_id, String group_name) {

        android.util.Log.wtf(TAG, "showNewNotification: ");

        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder = new NotificationCompat.Builder(context);
        notificationManager = NotificationManagerCompat.from(context);

        int m1 = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Intent notificationIntent = null;
        if (intent != null) {
            notificationIntent = intent;
            android.os.Bundle data = new android.os.Bundle();
            data.putString("action", "notificationfromfcm");
            data.putString("taskId", taskId);
            data.putString("type", type);
            notificationIntent.putExtras(data);
        } else {
            if (notification_type.equals("1")) {
                notificationIntent = new Intent(context, NotificationActivity.class);
            }

            android.os.Bundle data = new android.os.Bundle();
            data.putString("action", "notificationfromfcm");
            data.putString("taskId", taskId);
            data.putString("type", type);
            notificationIntent.putExtras(data);
        }


        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        pendingIntent = PendingIntent.getActivity((context), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;
        notification = builder
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.icon_notification_logo)
                .setSound(notificationSound)
                .setLights(Color.YELLOW, 1000, 1000)
                .setVibrate(new long[]{500, 500})
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(m1, notification);
    }

    public static void generatepicture(Context context, Intent notificationIntent, String title, String message, String imageUrl,
                                       String notification_type, String group_id, String msg_id, String group_name) {
        Intent intent = null;
        if (notificationIntent != null) {
            intent = notificationIntent;
        } else {
            if (notification_type.equals("1")) {
                intent = new Intent(context, NotificationActivity.class);
            }
        }
        new generatePictureStyleNotification(context, intent, title, message,
                imageUrl).execute();

    }

    public static class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, Intent notificationIntent, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;

            pendingIntent = PendingIntent.getActivity((context), 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            java.io.InputStream in;
            try {
                java.net.URL url = new java.net.URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            android.util.Log.wtf(TAG, "generatePictureStyleNotification: ");
            builder = new NotificationCompat.Builder(mContext);
            notificationManager = NotificationManagerCompat.from(mContext);

            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
                builder = new NotificationCompat.Builder(mContext, channelId);
            } else {
                builder = new NotificationCompat.Builder(mContext);
            }


            Notification notification;
            if (result == null || this.imageUrl == null || this.imageUrl.isEmpty()) {
                notification = builder
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSmallIcon(R.drawable.icon_notification_logo)
                        .setSound(notificationSound)
                        .setLights(Color.YELLOW, 1000, 1000)
                        .setVibrate(new long[]{500, 500})
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                        .setContentIntent(pendingIntent)
                        .build();
            } else {
                notification = builder
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSmallIcon(R.drawable.icon_notification_logo)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
                        .setSound(notificationSound)
                        .setLights(Color.YELLOW, 1000, 1000)
                        .setVibrate(new long[]{500, 500})
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                        .setContentIntent(pendingIntent)
                        .build();
            }
            random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;

            notificationManager.notify(m, notification);
        }
    }

    private void showEnquiryNotification(Context mContext, Intent notificationIntent, String title, String message,
                                         String communication_mode, String mobile, String email) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        PendingIntent pendingIntent = PendingIntent.getActivity((mContext), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(mContext, channelId);
        } else {
            builder = new NotificationCompat.Builder(mContext);
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
        PendingIntent callPendingIntent = PendingIntent.getActivity(mContext, 0, callIntent, 0);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        PendingIntent emailPendingIntent = PendingIntent.getActivity(mContext, 0, emailIntent, 0);

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.icon_notification_logo);
        builder.setSound(notificationSound);
        builder.setLights(Color.YELLOW, 1000, 1000);
        builder.setVibrate(new long[]{500, 500});
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setColor(getResources().getColor(R.color.colorPrimary));
        if (communication_mode.equals("mobile"))
            builder.addAction(R.drawable.icon_call, "CALL", callPendingIntent);
        else if (communication_mode.equals("email"))
            builder.addAction(R.drawable.icon_email, "EMAIL", emailPendingIntent);

        Notification notification = builder.build();

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        notificationManager.notify(m, notification);
    }
}