package org.palpitat.dudu.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.palpitat.dudu.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.opencensus.tags.Tag;

public class PreparedBirthdayActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText birthdayEditText;
    private ImageButton cancelButton;
    private ImageView checkImageView;
    private Button finishButton;

    private Boolean isUser1 = false;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepared_birthday);

        configure();
    }

    private void updateCoupleTable(String coupleKey, String nicnameStr) {
        Map<String, Object> preparedInfo = new HashMap<>();
        String birthDay = birthdayEditText.getText().toString();

        if (isUser1) {
            // User1 정보가 존재한다면
            preparedInfo.put("USER2_NICK", nicnameStr);
            preparedInfo.put("USER2_BIRTH", birthDay);
        } else {
            preparedInfo.put("USER1_NICK", nicnameStr);
            preparedInfo.put("USER1_BIRTH", birthDay);
        }

        db.collection("DU_TBL_COUPLE").document(coupleKey)
                .update(preparedInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("[Log]", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("[Log]", "Error writing document", e);
                    }
                });
    }

    private void checkUser1PreparedInfo(String coupleKey) {
        DocumentReference docRef = db.collection("DU_TBL_COUPLE").document(coupleKey);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        isUser1 = document.getData().containsKey("USER1_NICK");
                    } else {
                        Log.d("[Log]", "No such document");
                    }
                } else {
                    Log.d("[Log]", "get failed with ", task.getException());
                }
            }
        });
    }

    private void deleteRelatedCoupleTable(String coupleKey) {
        db.collection("DU_TBL_COUPLE").document(coupleKey)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        db.collection("DU_TBL_ALBUL").document(coupleKey)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        db.collection("DU_TBL_CHAT").document(coupleKey)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void dataPickerFunc() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                try {
                    birthdayEditText.setText(year + ". " + (monthOfYear + 1) + ". " + dayOfMonth);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.show();
    }

    private void configure() {
        checkImageView = findViewById(R.id.checkImageView);
        finishButton = findViewById(R.id.finishButton);
        cancelButton = findViewById(R.id.cancelButton);
        birthdayEditText = findViewById(R.id.birthdayEditText);

        // 키보드 자동으로 띄우
        mHandler.postDelayed(new Runnable() {
            public void run() {
                birthdayEditText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(birthdayEditText, InputMethodManager.SHOW_FORCED);
            }
        }, 1000);

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPickerFunc();
            }
        });

        birthdayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력하기 전에
                if (s.length() == 0) {
                    finishButton.setEnabled(false);
                    checkImageView.setVisibility(View.INVISIBLE);
                } else {
                    finishButton.setEnabled(true);
                    checkImageView.setVisibility(View.VISIBLE);
                }
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                String coupleKey = getIntent().getStringExtra("USERKEY");
                String myEmail = getIntent().getStringExtra("MYEMAIL");
                String nicnameStr = getIntent().getStringExtra("NICNAME");

                checkUser1PreparedInfo(coupleKey);
                updateCoupleTable(coupleKey, nicnameStr);

                intent.putExtra("USERKEY", coupleKey);
                intent.putExtra("MYEMAIL", myEmail);
                intent.putExtra("NICNAME", nicnameStr);
                startActivity(intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo : 커플 테이블 Key값을 이용하여 커플 테이블 삭제
                String coupleKey = getIntent().getStringExtra("USERKEY");

                deleteRelatedCoupleTable(coupleKey);

                finish();
            }
        });
    }
}
