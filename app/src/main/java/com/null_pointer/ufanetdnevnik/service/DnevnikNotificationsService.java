package com.null_pointer.diarycloud.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.null_pointer.diarycloud.MainActivity;
import com.null_pointer.diarycloud.model.DiaryCloud;
import com.null_pointer.diarycloud.model.response.NotificationsResponse;

public class DnevnikNotificationsService extends Service {
    private int NOTIFICATION_ID = 1040;
    private boolean started=false;
    private long was = 0;
    public DnevnikNotificationsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ((flags & START_FLAG_RETRY) == 0) {
            if(started){
                return Service.START_STICKY;
            }
            // Если повторный запуск, то ничего не делаем, если сервис уже запущен
        }

            // Альтернативные действия в фоновом режиме.
        started = true;
        startThread();

        return super.onStartCommand(intent, flags, startId);

    }

    private void startThread(){
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    boolean kk = checkMessages();
                    if(!kk){ // если юзер не совершил вход, то сервис будет остановлен
                        break;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                started = false;
            }
        });
        updateThread.start();
    }


    private boolean checkMessages(){
        DiaryCloud client = DiaryCloud.getIdentity(getApplicationContext());
        if(client == null){ // если юзер не совершил вход, то getIdentity() возвращает null
            return false;
        }
        try {
            NotificationsResponse response = client.getNotifications();
            NotificationsResponse.Notifications notify = response.getNotifications();
            if(notify.isSuccess() && notify.getMail() > 0){
                sendNotificationIfNeed(notify.getMail());
            }
        } catch (Exception e){
            e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "error;;;" + TextUtils.join(";",e.getStackTrace()), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void sendNotificationIfNeed(long count){
        if(was > count){
            was = count;
            return;
        }else if(was == count){
            return;
        }
        was += count;
        createNotify("Дневник",
                     "Новое сообщение (" + count + ")");
    }


    private void createNotify(String title, String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_email);

        // создаем Intent с действием на просмотр страницы сообщений
        // и явно указываем какую ссылку мы хотим открыть
        Intent intent = new Intent(DnevnikNotificationsService.this, MainActivity.class);

        intent.putExtra("fragment", MainActivity.FRAGMENT_MESSAGES);
        // указываем фрагмент для запуска(обработка идет в MainActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );

        // устанавливаем объект PendingIntent, большую иконку, заголовки и контент
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_email));
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setAutoCancel(true);
        try
        {

            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
            r.play();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // отображаем объект Notification в панели уведомлений
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
