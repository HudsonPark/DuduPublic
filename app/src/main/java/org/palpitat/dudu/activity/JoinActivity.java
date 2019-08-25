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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.palpitat.dudu.R;


public class JoinActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;                                 // 객체의 공유 인스턴스 Flag
    private static final String TAG = "JoinActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    // DB 조회 관련 상수값
    private static final int DB_COUPLE = 1000;
    private static final int DB_USER = 2000;
    // 파이어베이스
    // [START get_firestore_instance]
    FirebaseFirestore db;
    // [END get_firestore_instance]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // 구글 로그인 세팅
        SetGoogle();

        // 파이어 베이스 세팅
        setup();

        db.collection("test").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Toast.makeText(JoinActivity.this, "onComplete", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JoinActivity.this, "addOnFailureListener", Toast.LENGTH_SHORT).show();
            }
        });
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
                signIn();
            }
        });
    }

    public void setup() {
        db = FirebaseFirestore.getInstance();
        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

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
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        Toast.makeText(this, "구글 로그인 signIn()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "onActivityResult 진입", Toast.LENGTH_SHORT).show();

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

                // DB 조회 결과값 Flag
                boolean bIsdata = false;

                // 로그인 성공 == 회원가입 성공
                // 1. DU_TBL_COUPLE 데이터 조회 ('USER1_ID, USER2_ID')
                // 1번 단계 값 유효시, D-Day 메인 액티비티로 이동
                Toast.makeText(this, "DB 조회 DB_COUPLE", Toast.LENGTH_SHORT).show();
                bIsdata = DBSearch(DB_COUPLE);
                if (bIsdata) {

                }
                else {
                    // 2. DU_TBL_USER 데이터 조회 ('USER ID')
                    // 2번 단계 값 유효시, 인증 액티비티로 이동
                    // 2번 단계 값 유효 안하면 새롭게 등록 후, 인증 액티비티로 이동
                    Toast.makeText(this, "DB 조회 DB_USER", Toast.LENGTH_SHORT).show();
                    bIsdata = DBSearch(DB_USER);
                    if (bIsdata) {

                    }
                    else {

                    }
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

                // 로그인 실패
            }
        }
    }

    private boolean DBSearch(int nType) {
        // DB 조회 방식은 '컬렉션의 모든 문서 가져오기'
        // 설명 : where() 필터를 완전히 생략하여 컬렉션의 모든 문서를 검색할 수도 있습니다.
        // * 어느 index에 들어있는지 모르니까.

        try {
            if (nType == DB_COUPLE) {
                // CollectionReference couples = db.collection("DB_TBL_COUPLE");
                db.collection("DU_TBL_COUPLE").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 조회 성공
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                document.getData();

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
            }
            else if (nType == DB_USER) {
                CollectionReference users = db.collection("DB_TBL_USER");

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        }
        catch (Exception ex)
        {
            Log.d(TAG, "DBSearch Error : " + ex.toString());
        }

        return false;
    }


}
