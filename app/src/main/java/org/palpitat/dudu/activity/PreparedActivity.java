package org.palpitat.dudu.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.palpitat.dudu.R;
import org.palpitat.dudu.fragment.HomeFragment;

import java.util.List;

public class PreparedActivity extends AppCompatActivity {

    private EditText nicnameEditText;
    private ImageButton cancelButton;
    private ImageView checkImageView;
    private Button nextButton;

    private Handler mHandler = new Handler();

    // 퍼미션 여부 체크
    private boolean isPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepared);

        // Camera Permission Check
        tedPermission();

        configure();
    }

    private void configure() {
        checkImageView = findViewById(R.id.checkImageView);
        cancelButton = findViewById(R.id.cancelButton);
        nextButton = findViewById(R.id.nextButton);
        nicnameEditText = findViewById(R.id.nicnameEditText);

        nicnameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    nextButton.callOnClick();
                }
                return false;
            }
        });

        nicnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력 할때마다


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력이 끝났을 때
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력하기 전에
                if (s.length() == 0) {
                    nextButton.setEnabled(false);
                    checkImageView.setVisibility(View.INVISIBLE);
                } else {
                    nextButton.setEnabled(true);
                    checkImageView.setVisibility(View.VISIBLE);
                }
            }
        });

        // 키보드 자동으로 띄우
        mHandler.postDelayed(new Runnable() {
            public void run() {
                nicnameEditText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(nicnameEditText, InputMethodManager.SHOW_FORCED);
            }
        }, 1000);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo : 커플 테이블 Key값을 이용하여 커플 테이블 삭제
                Intent getIntent = getIntent(); /*데이터 수신*/
                String coupleTableKey = getIntent.getExtras().getString("USER1_ID");

                finish();
//                db.collection("DU_TBL_COUPLE").document(coupleTableKey)
//                        .delete()
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                finish();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nicnameStr = nicnameEditText.getText().toString();
                if (nicnameStr.equals("")) {
                    Toast.makeText(PreparedActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    String coupleKey = getIntent().getStringExtra("USERKEY");
                    String myEmail = getIntent().getStringExtra("MYEMAIL");

                    Intent intent = new Intent(getApplicationContext(), PreparedBirthdayActivity.class);
                    intent.putExtra("NICNAME", nicnameStr);
                    intent.putExtra("USERKEY", coupleKey);
                    intent.putExtra("MYEMAIL", myEmail);

                    startActivity(intent);
                }
            }
        });
    }

    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.\n[설정 > [권한] 에서 권한을 허할 수 있습니다.")
                .setDeniedMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }
}