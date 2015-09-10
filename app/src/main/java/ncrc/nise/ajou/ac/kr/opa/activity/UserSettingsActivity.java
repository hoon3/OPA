package ncrc.nise.ajou.ac.kr.opa.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import ncrc.nise.ajou.ac.kr.opa.R;
import ncrc.nise.ajou.ac.kr.opa.data.DBAdapter;
import ncrc.nise.ajou.ac.kr.opa.data.User;


public class UserSettingsActivity extends ActionBarActivity implements RadioGroup.OnCheckedChangeListener {
    // User (Temp)
    private User user;

    /* Elements of Layout */
    private TextView textViewWeight;
    private TextView textViewHeight;
    private TextView textViewAge;
    private TextView textViewSex;

    private EditText editTextWeight;
    private EditText editTextHeight;
    private EditText editTextAge;

    private RadioGroup radioGroupSex;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;

    // sex 1:male, 0:female
    private int sex = 9;

    private Button buttonSubmit;

    // DB Settings
    private DBAdapter dbAdapter;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        textViewWeight = (TextView) findViewById(R.id.textViewTitleBMI);
        textViewHeight = (TextView) findViewById(R.id.textViewTitleObesityStatus);
        textViewAge = (TextView) findViewById(R.id.textViewTileDailyBMR);
        textViewSex = (TextView) findViewById(R.id.textViewSex);

        editTextWeight = (EditText) findViewById(R.id.textViewBMI);
        editTextHeight = (EditText) findViewById(R.id.textViewObesityStatus);
        editTextAge = (EditText) findViewById(R.id.textViewDailyBMR);

        radioGroupSex = (RadioGroup)findViewById(R.id.radiogroupSex);
        radioGroupSex.setOnCheckedChangeListener(this);

        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // if any field is empty
                if(TextUtils.isEmpty(editTextHeight.getText().toString())
                        || TextUtils.isEmpty(editTextWeight.getText().toString())
                        || TextUtils.isEmpty(editTextAge.getText().toString())
                        || sex == 9) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(UserSettingsActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    // Alert empty info
                    alert.setMessage("빈 칸 없이 모두 입력해주세요");
                    alert.show();
                } else {
                    /* register user info */
                    // if DB opens successfully
                    if(c != null) {
                        // create User wiht double height, double weight, int age, int sex
                        user = new User(Double.parseDouble(editTextHeight.getText().toString()),
                                Double.parseDouble(editTextWeight.getText().toString()),
                                Integer.parseInt(editTextAge.getText().toString()),
                                sex);

                        // insert into db
                        dbAdapter.insertUser(user.getHeight(),
                                user.getWeight(),
                                user.getAge(),
                                user.getSex());

                        //register succeed
                        Toast.makeText(getApplicationContext(), "정보가 등록되었습니다.", Toast.LENGTH_LONG).show();

                        // activity 종료
                        finish();
                    }
                }
            }
        });

        /* DB 처리 */
        //DB Adapter
        dbAdapter = new DBAdapter(getApplicationContext());
        c = dbAdapter.read_user_info();

        // if DB opens successfully
        if(c != null) { //If there are any info yet
            // alert diaglog - empty
            if(c.getCount()==0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                // Alert empty info
                alert.setMessage("사용자 정보를 입력해주세요");
                alert.show();

                //focus edittext of weight
                editTextWeight.requestFocus();
                // show keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextWeight, InputMethodManager.SHOW_FORCED);
            } else {
                c.moveToFirst();
                Log.i("USER", c.getString(0));

                user = new User(c.getDouble(1), c.getDouble(2), c.getInt(3), c.getInt(4));

                // fill edittext of weight, height, age
                editTextWeight.setText(String.valueOf(user.getWeight()));
                editTextHeight.setText(String.valueOf(user.getHeight()));
                editTextAge.setText(String.valueOf(user.getAge()));

                // set radiobutton checked
                if(user.getSex() == 1) {
                    radioGroupSex.check(R.id.radioButtonMale);
                } else {
                    radioGroupSex.check(R.id.radioButtonFemale);
                }
            }
        }
    }

    /*
        For animation when activity finished
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    /* Set sex when radio button is checked */
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i) {
            case R.id.radioButtonMale:
                sex = 1;
                break;
            case R.id.radioButtonFemale:
                sex = 0;
                break;
        }
    }
}
