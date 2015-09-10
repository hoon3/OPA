package ncrc.nise.ajou.ac.kr.opa.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;

import ncrc.nise.ajou.ac.kr.opa.R;
import ncrc.nise.ajou.ac.kr.opa.data.DBAdapter;
import ncrc.nise.ajou.ac.kr.opa.fragment.ActRecommenderFragment;
import ncrc.nise.ajou.ac.kr.opa.fragment.ManboFragment;
import ncrc.nise.ajou.ac.kr.opa.fragment.MyAvatarFragment;
import ncrc.nise.ajou.ac.kr.opa.fragment.NavigationDrawerFragment;
import ncrc.nise.ajou.ac.kr.opa.fragment.SchedulerFragment;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    // DB 관련
    private DBAdapter dbAdapter;
    private Cursor c;

    // 서비스 재시작 관련
    BroadcastReceiver receiver;  //RestartService 클래스를 메소드화 해서 브로드캐스트를 받을 객체
    Intent intentMyService; //PersistentService 클래스를 담을 변수

    /*
     * DB Adapter GET
     */
    public DBAdapter getDbAdapter() {
        return dbAdapter;
    }

    /*
     * Cursor GET
     */
    public Cursor getC() {
        return c;
    }

    public static final String TAG = "GOOGLE";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    //google api client
    private GoogleApiClient mClient;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        // activity_splash 화면 띄우기
        startActivity(new Intent(this, SplashActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 별도 쓰레드에서 초기화 작업
        initialize();
    }

    public void onResume() {
        //홈버튼을 이용해 나가던지 Back버튼으로 나가던지 onDestory() 함수를 사용하지 않는다면, 다시 앱을 실행되면 onCreate()함수가 실행이 되지 않고 않고 않고 onResume()함수가 실행
        //프로그램 처음 실행시에도 onCreate()함수 실행 후 onResume()함수가 실행됨 -> 코드 중복이 없어야 됨

        super.onResume();

        IntentFilter filter = new IntentFilter("Action_resume"); //새롭게 IntentFilter 선언
    }
    public void OnDestroy() {
        unregisterReceiver(receiver);    //RestartService 방송을 수신하는 BroadcastReceiver를 해제
        super.onDestroy();
    }

    /* 쓰레드 */
    private void initialize() {
        InitializationRunnable init = new InitializationRunnable();
        new Thread(init).start();
    }

    /* 스플래시 동안 로직 처리 */
    private class InitializationRunnable implements Runnable {

        @Override
        public void run() {
            /* DB 읽어오기 */
            //DB Adapter
            dbAdapter = new DBAdapter(getApplicationContext());
            c = dbAdapter.read_user_info();

            /*
             * Persistence 서비스 시작 + 서비스 재시작을 위한 리시버 등록
             */
            intentMyService = new Intent(getApplicationContext(), PersistentService.class);  // PersistentService의 객체인 intent 객체 생성
            receiver = new RestartService(); //RestartService 클래스로 객체 선언

            try	{
                IntentFilter mainFilter = new IntentFilter("ncrc.nise.ajou.ac.kr.opa.persistent");  //IntentFilter 객체 생성
                registerReceiver(receiver, mainFilter);    //mainFilter와 함께 방송을 받기 시작(Restart)
                startService(intentMyService);  //PersistentService 작동 시작
            } catch (Exception e) {  //예외처리
                e.printStackTrace();
            }

            /* 내비게이션 드로어 */
            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            mTitle = getTitle();

            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    /* 내비게이션 드로어 선택시 */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        // different fragments
        if (position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MyAvatarFragment.newInstance(position + 1))
                    .commit();
        } else if (position == 1) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SchedulerFragment.newInstance(position + 1))
                    .commit();
        } else if (position == 2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ActRecommenderFragment.newInstance(position + 1))
                    .commit();
        } else if (position == 3) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ManboFragment.newInstance(position + 1))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(R.drawable.ic_action_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), UserSettingsActivity.class));
            //Animation: none
            overridePendingTransition(0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
