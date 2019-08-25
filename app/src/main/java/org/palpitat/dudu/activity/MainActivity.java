package org.palpitat.dudu.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.palpitat.dudu.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // 구글계정 파이어베이스 로그인
    private FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

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

    // User Info : UID, Email
    String m_strUID, mStrEmail, mStrcode, mStrDateTime;
    int mUserCnt;
    String mStrUserKey, mStrUserID1, mStrUserID2;

    // DB 조회 관련 상수값
    private static final int DB_COUPLE = 1000;
    private static final int DB_USER = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 구글 로그인 세팅
        SetGoogle();

        // 파이어베이스 세팅
        FirbaseSet();

        mUserCnt = 0;
    }

    private void SetGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        SignInButton signInButton = (SignInButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
    }

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

    private void SignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Toast.makeText(getApplicationContext(), "구글 로그인 signIn()", Toast.LENGTH_SHORT).show();
    }

    private void FirebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "FirebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);

                            DBSearch(DB_COUPLE);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FirebaseAuthWithGoogle(account);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Get UID
                    m_strUID = user.getUid();
                    mStrEmail = user.getEmail();
                    Toast.makeText(getApplicationContext(), "onActivityResult [" + m_strUID + "]", Toast.LENGTH_SHORT).show();
                } else {
                    // No user is signed in
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
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
                // CollectionReference couples = db.collection("DB_TBL_COUPLE");
                FireDB.collection("DU_TBL_COUPLE").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 조회 성공
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                document.getData();

                                mStrUserID1 = document.get("USER1_ID").toString();
                                mStrUserID2 = document.get("USER2_ID").toString();

                                //if (m_strUID.equals(strUserID1) || m_strUID.equals(strUserID2)) {
                                if (mStrEmail.equals(mStrUserID1) || mStrEmail.equals(mStrUserID2))
                                {
                                    mUserCnt++;
                                    break;
                                }
                            }

                            if (mUserCnt == 0)
                                DBSearch(DB_USER);
                            else
                            {
                                // DU_TBL_COUPLE 데이터가 있으면 메인으로 이동
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                mStrUserKey = String.format("%s%s", mStrUserID1, mStrUserID2);
                                intent.putExtra("USERKEY", mStrUserKey);
                                intent.putExtra("MYEMAIL", mStrEmail);
                                Toast.makeText(getApplicationContext(), "[USERKEY][" + mStrUserKey + "]", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 조회 실패
                        Toast.makeText(getApplicationContext(), "DB 조회에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if (nType == DB_USER) {
                FireDB.collection("DU_TBL_USER").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 조회 성공
                        if (task.isSuccessful()) {
                            String strEmail, strCode, strDateTime;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                document.getData();
                                strEmail = document.get("USER_EMAIL").toString();

                                if (mStrEmail.equals(strEmail)) {
                                    mUserCnt++;

                                    mStrcode = document.get("USER_INVITECODE").toString();
                                    mStrDateTime = document.get("USER_INVITETIME").toString();
                                    break;
                                }
                            }

                            if (mUserCnt == 0) {
                                Log.d(TAG, "USER 정보 등록 필요");

                                // DU_TBL_USER INSERT 로직 추가
                                Map<String, Object> UserData = new HashMap<>();
                                UserData.put("USER_EMAIL", mStrEmail);

                                // 8자리의 인증번호 생성(8자리, 중복X)
                                mStrcode = numberGen(8, 2);
                                UserData.put("USER_INVITECODE", mStrcode);

                                // 시간 생성 포맷 ("yyyy-MM-dd HH:mm:ss")
                                mStrDateTime = GetDateTime();
                                UserData.put("USER_INVITETIME", mStrDateTime);

                                // 커플 유무 Default 값으로 false
                                UserData.put("IS_COUPLE", false);

                                FireDB.collection("DU_TBL_USER").document(m_strUID)
                                        .set(UserData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                // DU_TBL_USER 등록 성공
                                                Intent intent = new Intent(getApplicationContext(), ConfirmActivity.class);
                                                intent.putExtra("USER_EMAIL", mStrEmail);
                                                intent.putExtra("USER_INVITECODE", mStrcode);
                                                intent.putExtra("USER_INVITETIME", mStrDateTime);
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
                            else {
                                try {
                                    // DU_TBL_USER 데이터는 있고 DU_TBL_COUPLE 데이터는 없으므로 인증화면으로 이동
                                    // strEmail, strCode, strDateTime
                                    Intent intent = new Intent(getApplicationContext(), ConfirmActivity.class);
                                    intent.putExtra("USER_EMAIL", mStrEmail);
                                    intent.putExtra("USER_INVITECODE", mStrcode);
                                    intent.putExtra("USER_INVITETIME", mStrDateTime);
                                    startActivity(intent);
                                    finish();
                                }
                                catch (Exception ex){
                                    Log.d(TAG, "Error getting documents: " + ex.toString());
                                }
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

    /*
     * 전달된 파라미터에 맞게 난수를 생성한다
     * @param len : 생성할 난수의 길이
     * @param dupCd : 중복 허용 여부 (1: 중복허용, 2:중복제거)
     */
    public static String numberGen(int len, int dupCd ) {
        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수

        for(int i=0;i<len;i++) {
            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if(dupCd==1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            }else if(dupCd==2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if(!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                }else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i-=1;
                }
            }
        }
        return numStr;
    }

    public static String GetDateTime()
    {
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        return formatDate;
    }
}
