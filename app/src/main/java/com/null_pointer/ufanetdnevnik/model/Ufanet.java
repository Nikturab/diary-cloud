package com.null_pointer.diarycloud.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.null_pointer.diarycloud.db.records.WeekRecord;
import com.null_pointer.diarycloud.exception.DiaryException;
import com.null_pointer.diarycloud.exception.GradesException;
import com.null_pointer.diarycloud.exception.InboxException;
import com.null_pointer.diarycloud.exception.InternetConnectionRequiredException;
import com.null_pointer.diarycloud.exception.MessageReadException;
import com.null_pointer.diarycloud.exception.MessageReplyException;
import com.null_pointer.diarycloud.exception.MessageWriteException;
import com.null_pointer.diarycloud.exception.DiaryCloudLoginRequiredException;
import com.null_pointer.diarycloud.model.response.Day;
import com.null_pointer.diarycloud.model.response.DiaryResponse;
import com.null_pointer.diarycloud.model.response.GradesResponse;
import com.null_pointer.diarycloud.model.response.MessageReadResponse;
import com.null_pointer.diarycloud.model.response.MessageReplyResponse;
import com.null_pointer.diarycloud.model.response.MessageWriteResponse;
import com.null_pointer.diarycloud.model.response.MessagesResponse;
import com.null_pointer.diarycloud.model.response.NotificationsResponse;
import com.null_pointer.diarycloud.model.response.SignInResponse;
import com.null_pointer.diarycloud.model.response.TotalsResponse;
import com.null_pointer.diarycloud.model.response.Week;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;


public class DiaryCloud {

    private DiaryCloudRequest URequest = new DiaryCloudRequest();
    public  static String DATE_FORMAT = "dd.MM.yy";
    private static final String SHARED_PREFERENCES_NAME = "DiaryCloudDnevnik";
    private static final String API_URL = null;
    private Gson gson = new GsonBuilder().create();
    private String token;
    private String name;


    private static ArrayList<Integer> cached_diary_weeks = new ArrayList<>();
    private static long last_diary_week_cache=0;
    public static ArrayList<Integer> cached_inbox_pages = new ArrayList<>();
    public static long last_inbox_pages_cache=0;
    private boolean connectionStatus=true;

    public static DiaryCloud getIdentity(Context context){
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String token_s = settings.getString("token", null);
        String name_s = settings.getString("name", null);
        if(token_s == null || token_s.equals("")){
            return null;
        }
        DiaryCloud c =  new DiaryCloud(token_s, context);
        c.name = name_s;
        return c;
    }


    public DiaryCloud(){}

    public DiaryCloud(String token){
        this.token = token;
    }

    public DiaryCloud(String token, Context ctx){
        this.token = token;
    }

    public boolean saveIdentity(Context context){
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        try {
            editor.putString( "token", token );
            editor.putString( "name", name );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return editor.commit();
    }

    private boolean removeIdentity(Context context){
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        try {
            editor.remove("token");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return editor.commit();
    }
    /**
     * @param username String
     * @param password String
     * @return boolean user status
     */
    public boolean signIn(String username, String password) throws IOException, DiaryCloudLoginRequiredException {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "sign-in", URequest.getQueryString(data));
        SignInResponse response = gson.fromJson(raw.getBody(), SignInResponse.class);
        if(response.hasError()){
            return false;
        }
        this.token =  response.getToken();
        this.name =  response.getName();
        return true;
    }

    public boolean signOut(Context context){
        return this.removeIdentity(context);
    }

    public void setConnectionStatus(boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    /**
     * @return boolean user status
     */
    public DiaryResponse getDiary(GregorianCalendar date) throws DiaryException, IOException, DiaryCloudLoginRequiredException, InternetConnectionRequiredException {
        String date_str = getFormattedDate(date);
        return _getDiary(date_str);
    }

    /**
     * @return boolean user status
     */
    public DiaryResponse getDiary(String date) throws DiaryException, IOException, DiaryCloudLoginRequiredException, InternetConnectionRequiredException {
        Log.d("getDiary string", "string used");
        return _getDiary(date);
    }
    /**
     * @return DiaryResponse diary object
     */
    private DiaryResponse _getDiary(String date) throws DiaryException, IOException, DiaryCloudLoginRequiredException, InternetConnectionRequiredException {
        if(!connectionStatus){
            DiaryResponse response = new DiaryResponse();
            /* Получаем данные из бд по timestamp понедельника */
            Week week = WeekRecord.getWeek(getMonday(getUnFormattedDate(date, "dd.MM.yy")).getTime());
            if(week == null)
               throw new InternetConnectionRequiredException(); // В базе этой недели нет, показываем необходимость интернета
            else
                response.fromDays( week.getDays() ); // устанавливаем кэшированные данные

            return response;
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("date", date);
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "diary/" + token, URequest.getQueryString(data));
        final DiaryResponse response = gson.fromJson(raw.getBody(), DiaryResponse.class);
        int hash = response.getPage().getWeek().hashCode();
        if(!cached_diary_weeks.contains(hash)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WeekRecord.addWeek(response.getPage().getWeek());
                }
            }).start();
            cached_diary_weeks.add(hash);
        }
        if(response.hasError()){
            throw new DiaryException(response.getMessage());
        }
        return response;
    }


    /**
     * @return MessagesResponse response
     */
    public MessagesResponse getInbox(int page) throws InboxException, IOException, DiaryCloudLoginRequiredException, InternetConnectionRequiredException {
        if(!connectionStatus){
            throw new InternetConnectionRequiredException();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "" + page);
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "inbox/" + token, URequest.getQueryString(data));
        MessagesResponse response = gson.fromJson(raw.getBody(), MessagesResponse.class);
        if(response.hasError()){
            throw new InboxException(response.getMessage());
        }

        return response;
    }

    /**
     * @return MessagesResponse response
     */
    public NotificationsResponse getNotifications() throws InboxException, IOException, DiaryCloudLoginRequiredException, InternetConnectionRequiredException {
        if(!connectionStatus){
            throw new InternetConnectionRequiredException();
        }
        HashMap<String, String> data = new HashMap<>();
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "notifications/" + token, URequest.getQueryString(data));
        NotificationsResponse response = gson.fromJson(raw.getBody(), NotificationsResponse.class);
        if(response.hasError()){
            throw new InboxException(response.getMessage());
        }

        return response;
    }

    /**
     * @return boolean user status
     */
    public MessageReplyResponse replyMessage(int id, String content, String subject) throws IOException, DiaryCloudLoginRequiredException, MessageReplyException, InternetConnectionRequiredException {
        if(!connectionStatus){
            throw new InternetConnectionRequiredException();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("content",  content);
        data.put("subject", subject);
        data.put("id", "" + id);
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "message-reply/" + token, URequest.getQueryString(data));
        MessageReplyResponse response = gson.fromJson(raw.getBody(), MessageReplyResponse.class);
        if(response.hasError()){
            throw new MessageReplyException(response.getMessage());
        }

        return response;
    }

    /**
     * @return boolean user status
     */
    public MessageWriteResponse writeMessage(String receiver, String content, String subject, boolean is_teacher) throws IOException, DiaryCloudLoginRequiredException, MessageWriteException, InternetConnectionRequiredException {
        HashMap<String, String> data = new HashMap<>();
        if(!connectionStatus){
            throw new InternetConnectionRequiredException();
        }
        data.put("content",  content);
        data.put("subject", subject);
        data.put("receiver", "" + receiver);
        data.put("is_teacher", "" + is_teacher);
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "message-write/" + token, URequest.getQueryString(data));
        MessageWriteResponse response = gson.fromJson(raw.getBody(), MessageWriteResponse.class);
        if(response.hasError()){
            throw new MessageWriteException(response.getMessage());
        }

        return response;
    }

    public MessageReadResponse setMessageRead(int mid) throws IOException, DiaryCloudLoginRequiredException, MessageReadException, InternetConnectionRequiredException {
        if(!connectionStatus){
            throw new InternetConnectionRequiredException();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("mid",  mid + "");
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "set-message-read/" + token, URequest.getQueryString(data));
        MessageReadResponse response = gson.fromJson(raw.getBody(), MessageReadResponse.class);
        if(response.hasError()){
            throw new MessageReadException(response.getMessage());
        }

        return response;
    }


    public GradesResponse getGrades(String begin, String end) throws IOException, DiaryCloudLoginRequiredException, GradesException, InternetConnectionRequiredException {
        if(!connectionStatus){
            throw new InternetConnectionRequiredException();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("begin", begin);
        data.put("end", end);
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "period-grades/" + token, URequest.getQueryString(data));
        GradesResponse response = gson.fromJson(raw.getBody(), GradesResponse.class);
        if(response.hasError()){
            throw new GradesException(response.getMessage());
        }
        return response;
    }


    public TotalsResponse getTotals() throws IOException, DiaryCloudLoginRequiredException, GradesException, InternetConnectionRequiredException {
        if(!connectionStatus){
            throw new InternetConnectionRequiredException();
        }
        HashMap<String, String> data = new HashMap<>();
        DiaryCloudResponse raw = URequest.sendPost(API_URL + "get-totals/" + token, URequest.getQueryString(data));
        TotalsResponse response = gson.fromJson(raw.getBody(), TotalsResponse.class);
        if(response.hasError()){
            throw new GradesException(response.getMessage());
        }
        return response;
    }

    /**
     * Возвращает дату в формате 02.02.17
     * @param date
     * @return дата
     */
    public static String getFormattedDate(GregorianCalendar date){
        /*
            Очень страшная вещь
            бу!11!!pac!1!!
         */
        return ( date.get(Calendar.DAY_OF_MONTH) < 10 ? ( "0"+date.get(Calendar.DAY_OF_MONTH) ) : (date.get(Calendar.DAY_OF_MONTH) )  ) + "." + ( date.get(Calendar.MONTH) + 1<10 ? "0"+ (date.get(Calendar.MONTH) + 1 ) :  date.get(Calendar.MONTH) + 1 ) + "." + String.valueOf(date.get(Calendar.YEAR)).substring(2,4);
    }

    public static Date getUnFormattedDate(String date, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date fdate;
        try {
            fdate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return fdate;
    }

    public static Date getMonday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        return calendar.getTime();
    }
    public String getName(){
        return name;
    }

    /**
     * Сохраняет логин и пароль.
     * Вызывается, когда юзер поставил галочку "сохранить пароль"
     * @param username username
     * @param password password
     * @param context context
     * @return editor.commit()
     */
    public static boolean saveData(String username, String password, Context context){
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        try {
            editor.putString( "username", username );
            editor.putString( "password", password );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return editor.commit();
    }

    public static HashMap<String, String> getData(Context context){
        HashMap<String, String> map = new HashMap<>();
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String username = settings.getString("username", null);
        String password = settings.getString("password", null);
        if(username == null || password == null){
            return null;
        }
        map.put("username", username);
        map.put("password", password);
        return map;
    }

    private class DiaryCloudRequest {
        private DiaryCloudResponse sendPost(String url, String params) throws IOException {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader in;
            if(responseCode>=200 && responseCode<300) {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            }else{
                //read error stream
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            }


            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return new DiaryCloudResponse(response.toString(), responseCode);
        }

        String getQueryString(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        String getQueryString(Map<?,?> map) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<?,?> entry : map.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s",
                        getQueryString(entry.getKey().toString()),
                        getQueryString(entry.getValue().toString())
                ));
            }
            return sb.toString();
        }

    }

}

