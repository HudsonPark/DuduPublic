package org.palpitat.dudu.fragment;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import org.palpitat.dudu.Adapter.SchedulAdapter;
import org.palpitat.dudu.R;
import org.palpitat.dudu.activity.DateSaveActivity;
import org.palpitat.dudu.activity.ImgViewActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.DatePickerDialog.*;
import static com.gun0912.tedpermission.TedPermission.TAG;

public class ScheduleFragment extends Fragment  {
    private TextView mDate;

    private ArrayList<String> mDayList;
    private GridView mGridview;
    private Calendar mCal;
    private String mTempDate;
    private SchedulAdapter schedulAdapter;
    private List<User> datalist = new ArrayList<>();
    private Button mNextMonth;
    long now = System.currentTimeMillis();
    public static int mCountK;

    private FirebaseFirestore db;
    private DocumentReference docRef;
    private Date date = new Date(now);
    final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
    final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);


    public static  ArrayList<String> readDB = new ArrayList<>();
    public static int mCountKK=20191103;
    private Button mPreviousMonth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //날짜 변경 버튼 누르고 하단 프래그먼트 바 호출했을때 생기는 버그 해결
        mCountK=0;
        return inflater.inflate(R.layout.fragment_schedul, container, false);


    }

    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            mDayList.add("" + (i + 1));
        }
    }

    private OnDateSetListener listener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(getContext(), year + "년" + monthOfYear + "월" + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
            int k[] = {year,monthOfYear,dayOfMonth};
        }
    };


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNextMonth = view.findViewById(R.id.rightButton);
        mPreviousMonth = view.findViewById(R.id.leftButton);

        mDate = view.findViewById(R.id.monthText);
        mGridview = view.findViewById(R.id.gridview);

        mNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rightPage();
            }
        });
        mPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                leftPage();
            }
        });

            readToDB();

        //현제 날자 세팅

        mDate.setText(curYearFormat.format(date) + "년" + curMonthFormat.format(date));

        mDayList = new ArrayList<String>();

        mDayList.add("일");
        mDayList.add("월");
        mDayList.add("화");
        mDayList.add("수");
        mDayList.add("목");
        mDayList.add("금");
        mDayList.add("토");
        mCal = Calendar.getInstance();

        //이번달 1일이 무슨요일인지 판단
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일을 매칭하기위해서 공백을 add함

        for (int i = 1; i < dayNum; i++) {
            mDayList.add("");
        }
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        schedulAdapter = new SchedulAdapter(getActivity(), mDayList);
        mGridview.setAdapter(schedulAdapter);


        Button Databutton = view.findViewById(R.id.datap);
        Databutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DateSaveActivity.class);
                v.getContext().startActivity(intent);



/*               DatePickerDialog dialog = new DatePickerDialoog, new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Toast.makeText(view.getContext(),"TOAS",Toast.LENGTH_SHORT).show();
                    }
                });

            }
*/
        }
        });



    }


    private void rightPage(){

//                schedulAdapter.isEmpty();
                mCountK++;
                mDayList.clear();
                schedulAdapter = new SchedulAdapter(getActivity(), mDayList);
                mGridview.setAdapter(schedulAdapter);

                mDayList.add("일");
                mDayList.add("월");
                mDayList.add("화");
                mDayList.add("수");
                mDayList.add("목");
                mDayList.add("금");
                mDayList.add("토");
                mCal.add(mCal.MONTH, +1);
                int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                for (int i = 1; i < dayNum; i++) {
                    mDayList.add("");
                }

                setCalendarDate(mCal.get(Calendar.MONTH) + 1);


                mDate.setText(mCal.get(Calendar.YEAR) + "년" + (mCal.get(Calendar.MONTH)+1)) ;


                //  Toast.makeText(getContext(), "다음달로이동" + mCal, Toast.LENGTH_SHORT).show();

                schedulAdapter.notifyDataSetChanged();


            }



    private void leftPage(){

                mCountK--;
                mDayList.clear();
                schedulAdapter = new SchedulAdapter(getActivity(), mDayList);
                mGridview.setAdapter(schedulAdapter);

                mDayList.add("일");
                mDayList.add("월");
                mDayList.add("화");
                mDayList.add("수");
                mDayList.add("목");
                mDayList.add("금");
                mDayList.add("토");
                mCal.add(mCal.MONTH, -1);
                int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                for (int i = 1; i < dayNum; i++) {
                    mDayList.add("");
                }

                setCalendarDate(mCal.get(Calendar.MONTH) + 1);


                mDate.setText(mCal.get(Calendar.YEAR) + "년" + (mCal.get(Calendar.MONTH)+1));

                //  Toast.makeText(getContext(), "다음달로이동" + mCal, Toast.LENGTH_SHORT).show();

                schedulAdapter.notifyDataSetChanged();

            }
        private void readToDB(){

            //DB 읽기 로직
            db = FirebaseFirestore.getInstance();
            db.collection("DU_TBL_SCHEDULE").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            readDB.add(document.getData().toString());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
            /*docRef = db.collection("DU_TBL_SCHEDULE").document();
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        readDB=docRef.toString();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });*/
        }
    private void writetoDB(){
// 저장처리
        Map<String, Object> dbDate = new HashMap<>();
        dbDate.put("2019", "2018");
        db.collection("DU_TBL_SCHEDULE").add(dbDate).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.w(TAG, "DB SAVE Error ");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "DB SAVE Error ", e);

            }
        });

    }
            /*
    @Override
    public void pluseMonthCount() {
        mCountK++;
    }

    @Override
    public void minusMonthCount() {
        mCountK--;
    }*/
     /*  private void DatePick(){
           OnDateSetListener datePickerDialog = new OnDateSetListener(){

               @Override
               public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                   Toast.makeText(view.getContext(), year + "년" + month + "월" + dayOfMonth +"일", Toast.LENGTH_SHORT).show();

               }
           };
       }*/
}


