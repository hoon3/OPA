package ncrc.nise.ajou.ac.kr.opa.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Byunsunghoon on 9/6/2015.
 */
public class RestartService extends BroadcastReceiver {
    public static final String ACTION_RESTART_PERSISTENTSERVICE = "ACTION.Restart.PersistentService";

    @Override
    public void onReceive(Context context, Intent intent) {

		/* 서비스 죽일때 알람으로 다시 서비스 등록 */
        if (intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)) {  //Action이 restart인 경우
            Intent i = new Intent(context, PersistentService.class); // PersistentService를 인텐트 객체 i에 저장
            context.startService(i);  // 인텐트 객체 i를 재시작
        }

		/* 폰 재부팅할때 서비스 등록 */
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {  //부팅 완료 되었을 경우
            Intent i = new Intent(context, PersistentService.class); // PersistentService를 인텐트 객체 i에 저장
            context.startService(i); // 인텐트 객체 i를 재시작
        }
    }
}

