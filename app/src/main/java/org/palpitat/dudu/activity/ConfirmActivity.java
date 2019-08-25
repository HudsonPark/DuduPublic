package org.palpitat.dudu.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.palpitat.dudu.R;

import java.util.HashMap;
import java.util.Map;

public class ConfirmActivity extends AppCompatActivity {
    private static final String TAG = "ConfirmActivity";

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    // 파이어베이스
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    // [START get_firestore_instance]
    FirebaseFirestore FireDB;
    // [END get_firestore_instance]

    // DB 조회 관련 상수값
    private static final int DB_USER = 2000;
    private static final int DB_COUPLE = 3000;

    String mStrEmail, mStrCode, mStrDateTime;
    TextView TxtNum, TxtTime;
    EditText EditOterCode;

    String mStrCoupleKey;
    String mStrOtherEmail;
    String mStrInvitecode;
    int mUserCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // 파이어베이스 세팅
        FirbaseSet();

        EditOterCode = (EditText) findViewById((R.id.TxtConfirm_OtherNum));
        EditOterCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        // 완료 버튼
                        mStrInvitecode = EditOterCode.getText().toString();
                        DBSearch(DB_USER);
                        break;
                    default:
                        // 기본 버튼
                        mStrInvitecode = EditOterCode.getText().toString();
                        DBSearch(DB_USER);
                        break;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        mStrEmail = intent.getStringExtra("USER_EMAIL");
        mStrCode = intent.getStringExtra("USER_INVITECODE");
        mStrDateTime = intent.getStringExtra("USER_INVITETIME");

        UserDataView();
    }

    // 유저 데이터 View (인증번호, 유효시간)
    private void UserDataView() {
        TxtNum = (TextView) findViewById(R.id.TxtConfirm_Num);
        TxtTime = (TextView) findViewById(R.id.TxtConfirm_Time);

        TxtNum.setText(mStrCode);
        TxtTime.setText(mStrDateTime);
    }

    // 파이어베이스 세팅
    private void FirbaseSet()
    {
        // [START set_firestore_settings]
        FireDB = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FireDB.setFirestoreSettings(settings);
        // [END set_firestore_settings]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        // [END auth_state_listener]
    }

    private boolean DBSearch(int nType) {
        // DB 조회 방식은 '컬렉션의 모든 문서 가져오기'
        // 설명 : where() 필터를 완전히 생략하여 컬렉션의 모든 문서를 검색할 수도 있습니다.
        // * 어느 index에 들어있는지 모르니까.

        try {
            // [START set_firestore_settings]
            FireDB = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            FireDB.setFirestoreSettings(settings);
            // [END set_firestore_settings]

            // 조회된 갯수 초기화
            mUserCnt = 0;

            if (nType == DB_COUPLE) {

            }
            else if (nType == DB_USER) {
                FireDB.collection("DU_TBL_USER").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 조회 성공
                        if (task.isSuccessful()) {
                            String strInvitecode;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                document.getData();
                                strInvitecode = document.get("USER_INVITECODE").toString();

                                if (mStrInvitecode.equals(strInvitecode)) {
                                    mUserCnt++;
                                    mStrOtherEmail = document.get("USER_EMAIL").toString();
                                    break;
                                }
                            }

                            if (mUserCnt == 0) {
                                Toast.makeText(getApplicationContext(), "일치하는 인증코드가 없습니다.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "일치하는 인증코드가 없습니다.");
                            }
                            else {
                                // DU_TBL_USER, 커플 유무 TRUE 로 변경
                                Map<String, Object> UserData = new HashMap<>();
                                mStrCoupleKey = String.format("%s%s", mStrEmail, mStrOtherEmail);

                                ///////////////////////////////////////////////////////////////////////

                                // ALBUM 기본 문서 추가
                                FireDB.collection("DU_TBL_ALBUM").document(mStrCoupleKey)
                                        .set(UserData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                // DU_TBL_ALBUM 등록 성공
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });

                                // CHAT 기본 문서 추가
                                FireDB.collection("DU_TBL_CHAT").document(mStrCoupleKey)
                                        .set(UserData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                // DU_TBL_ALBUM 등록 성공
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });

                                UserData.put("USER1_ID", mStrEmail);
                                UserData.put("USER2_ID", mStrOtherEmail);
                                // COUPLE 기본 문서 추가가
                                FireDB.collection("DU_TBL_COUPLE").document(mStrCoupleKey)
                                        .set(UserData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                // DU_TBL_COUPLE 등록 성공
                                                /////////////////////////////////////////////////////////////////////////////////////////
                                                // DU_TBL_USER 데이터는 있고 DU_TBL_COUPLE 데이터는 없으므로 인증화면으로 이동
                                                // strEmail, strCode, strDateTime
                                                Intent intent = new Intent(getApplicationContext(), PreparedActivity.class);
                                                intent.putExtra("USERKEY", mStrCoupleKey);
                                                intent.putExtra("MYEMAIL", mStrEmail);
                                                startActivity(intent);
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 조회 실패
                    }
                });

                //Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                //startActivity(intent);
            }
        }
        catch (Exception ex)
        {
            Log.d(TAG, "DBSearch Error : " + ex.toString());
        }

        return false;
    }


}
