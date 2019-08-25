package org.palpitat.dudu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.palpitat.dudu.R;

public class ChatActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;

    private EditText mEdtMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    /*
    private void initViews() {
        mListView = (ListView) findViewById(R.id.list_message);
        mAdapter = new ChatAdapter(this, R.layout.listitem_chat);
        mListView.setAdapter(mAdapter);
        mEdtMessage = (EditText) findViewById(R.id.edit_message);
        findViewById(R.id.btn_send).setOnClickListener(this);
    }
    */


    private void initFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("message");
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message = dataSnapshot.getValue(String.class);
                mAdapter.add(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String message = dataSnapshot.getValue(String.class);
                mAdapter.remove(message);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    /*
    @Override
    protected void onDestory() {
        super.onDestory();
        mDatabaseReference.removeEventListener(mChildEventListener);
    }

    @Override
    public void onClick(View v) {
        String message = mEdtMessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            mEdtMessage.setText("");
            mDatabaseReference.push().setValue(message);
        }
    }
    */



}
