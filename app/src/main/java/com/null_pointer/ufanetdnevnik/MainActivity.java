package com.null_pointer.diarycloud;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.null_pointer.diarycloud.model.DiaryCloud;
import com.null_pointer.diarycloud.service.DnevnikNotificationsService;
import com.null_pointer.diarycloud.view.main.adapter.MenuAdapter;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    /*
        Индексы элементов меню
     */
    public static final int MENU_HEADER       = 0;
    public static final int FRAGMENT_MAIN     = 1;
    public static final int FRAGMENT_GRADES   = 2;
    public static final int FRAGMENT_MESSAGES = 3;
    public static final int FRAGMENT_EXIT     = 4;
    //////////////////////////////////////////
    public static final int FRAGMENT_ADD_MESSAGE     = 5;

    private String[] menuTitles;
    private ListView drawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private DiaryCloud client;

    private FrameLayout mContentFrame;

    /*
        Принимает сообщения, содержащие операции для выполнения в главном потоке
     */
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MainActivity.MainActivityMessage message = (MainActivity.MainActivityMessage) msg.obj;
            message.runOperation();
        }
    };


    static class MainActivityMessage{
        public void runOperation(){
            // Override this
        }
    }

    protected void sendMessage(MainActivity.MainActivityMessage operation){
        Message msg = new Message();
        msg.obj = operation;
        handler.sendMessage(msg);
    }

    public DiaryCloud getClient(){
        return client;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareActionBar();

        if(!prepareClient())
            return;

        startService(new Intent(MainActivity.this, DnevnikNotificationsService.class));
        // Сервис проверки входящих сообщений

        prepareMenu();
        prepareViews();

        if(getIntent() != null) // сервис отправляет интент на открытие сообщений
            executeIntent(getIntent());
        else
            changeFragment(FRAGMENT_MAIN);
    }

    private void prepareViews() {
        mContentFrame = (FrameLayout) findViewById(R.id.activity_main__content_frame);
    }

    /**
     * Создает меню, добавляет иконки и пр.
     */
    private void prepareMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.menu__drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        menuTitles = getResources().getStringArray(R.array.navigation);
        drawerList = (ListView) findViewById(R.id.menu__drawer_layout__list);
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.menu__drawer_layout__list__header, null, false);
        ((TextView)listHeaderView.findViewById(R.id.menu__drawer_layout__list__header__name)).setText(client.getName());
        drawerList.addHeaderView(listHeaderView);
        drawerList.setAdapter( new MenuAdapter(this, android.R.layout.simple_list_item_1, menuTitles, new int[]{
                R.drawable.menu_diary,
                R.drawable.checked,
                R.drawable.send,
                R.drawable.menu_exit
        }));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    /**
     * Обрабатывает клик, запуская changeFragment()
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            changeFragment(i);
            mDrawerLayout.closeDrawer(drawerList);
        }
    }

    /**
     * Получает токен из Shared Preferences
     * и проверяет наличие интернет соединения
     * @return void
     */
    private boolean prepareClient() {
        client = DiaryCloud.getIdentity(getApplicationContext());
        if(client == null) {
            showLoginRequiredWarning(null);
            return false;
        }
        Thread checkConnection = new Thread(new Runnable() {
            @Override
            public void run() {
                client.setConnectionStatus(checkInternet());
            }
        });
        try {checkConnection.start(); checkConnection.join(); } catch (InterruptedException ignored){}
        return true;
    }


    private void prepareActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.main__color_main)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void executeIntent(Intent intent){
        int fragment = intent.getIntExtra("fragment", FRAGMENT_MAIN);
        changeFragment(fragment);
    }

    /**
     * Меняет фрагмент или выходит
     * вызывается при нажатии на элемент меню
     */
    private void changeFragment(int i){
        Fragment fragment = null;
        switch(i) {
            case FRAGMENT_MAIN:{
                fragment = new MainFragment();
                break;
            }
            case FRAGMENT_GRADES: fragment = new StatisticsFragment(); break;
            case FRAGMENT_MESSAGES: fragment = new MessagesFragment(); break;
            case FRAGMENT_EXIT: {
                if(client != null){
                    client.signOut(getApplicationContext());
                }
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        }
        if (fragment != null) {
            /*
                null, если нажали выход.
             */
            getSupportFragmentManager().popBackStack();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.activity_main__content_frame, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

            setActionBarTitle(i);
        }
    }


    @Override
    public void onNewIntent(final Intent intent){
        sendMessage(new MainActivityMessage(){
            @Override
            public void runOperation() {
                executeIntent(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //If click on toggle icon
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    //Synchronization drawer toggle icon for work panel
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setActionBarTitle(int position) {
        this.addActionBarTitle("");
        String title;
        if (position == 1) {
            title = getResources().getString(R.string.app_name);
        } else {
            title =  menuTitles[position-1];
        }
        getSupportActionBar().setTitle(title);
        ( (TextView)getSupportActionBar().getCustomView().findViewById(R.id.actionbar_title) ).setText(title);
    }

    public void addActionBarTitle(String t) {
        TextView tw = (TextView)getSupportActionBar().getCustomView().findViewById(R.id.actionbar_subtitle);
        tw.setText(t);
    }

/*
Проверяет наличие интернета подключением к яндексу.
 */
    public boolean checkInternet(){
            Socket socket = new Socket();
            Exception exception = null;
            try {
                socket.connect(new InetSocketAddress("ya.ru", 80), 500);
            } catch (IOException e) {
                exception = e;
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
            return exception == null;
    }


    public void showSnackbar(final String s, final int lengthShort) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(mContentFrame, s, lengthShort).show();
            }
        });
    }

    /*
    Эти функции с перспективой на будущее
     */
    public void showLoginRequiredWarning(String _message){
        if(_message == null){
            _message = "Попробуйте перезайти в аккаунт";
        }
        final String message = _message;
        runOnUiThread(new Runnable() {
            public void run() {
                Snackbar.make(mContentFrame, message, Snackbar.LENGTH_LONG).show();
            }
        });
    }
    public void showConnectionWarning(String _message){
        if(_message == null){
            _message = "Проверьте соединение с интернетом";
        }
        final String message = _message;
        runOnUiThread(new Runnable() {
            public void run() {
                Snackbar.make(mContentFrame, message, Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public double getScreenSize(Boolean need_x){
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels/dm.xdpi,2);
        double y = Math.pow(mHeightPixels/dm.ydpi,2);
        return need_x == null ? Math.sqrt(x+y) :
                (need_x ? x : y);
    }

    public boolean isTablet(){
        return getScreenSize(null) > 6.9;
    }



}
