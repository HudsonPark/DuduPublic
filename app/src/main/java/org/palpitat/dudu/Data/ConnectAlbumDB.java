package org.palpitat.dudu.Data;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.palpitat.dudu.Model.PictureData;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class ConnectAlbumDB {

    private FirebaseFirestore db;
    private Context mContext;
    private String ALBUM_COLLECTION = "DU_TBL_ALBUM";
    private String COUPLE_COLLECTION = "DU_TBL_COUPLE";
    private String ALBUM_COUPLE_KEY = "index";
    private String ALBUM_FOLDER = "uploadImages";

    public ConnectAlbumDB(Context context) {
        this.mContext = context;
    }


    public void connection() {
        db = FirebaseFirestore.getInstance();
    }

    private void createFolder(String coupleKey, final Map defaultMap) {
        this.ALBUM_COUPLE_KEY = coupleKey;

        db.collection(ALBUM_COLLECTION)
                .document(ALBUM_COUPLE_KEY)
                .collection(ALBUM_FOLDER).add(defaultMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(mContext, defaultMap.get("folderName") + " 폴더가 생성되었습니다."
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void putAlbumData(String coupleKey, Map imageMap) {

        this.ALBUM_COUPLE_KEY = coupleKey;

        db.collection(ALBUM_COLLECTION)
                .document(ALBUM_COUPLE_KEY)
                .collection(ALBUM_FOLDER)
                .add(imageMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        // 데이터 업로드에 성공했다면 Storage에 사진 데이터 저장
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                DocumentSnapshot documentSnapshot = task.getResult();

                                // 데이터가 저장되었고,
                                if (documentSnapshot.exists()) {

                                    Map<String, Object> resultMap = documentSnapshot.getData();

                                    // Firebase Storage에 이미지 저장
                                    uploadImage(ALBUM_COUPLE_KEY, resultMap);

                                }
                            }
                        });
                    }
                });
    }

    /*
     *  param1 : 사진의 상위 디렉터리로 쓰일 커플 키
     *  param2 : 업로드 된 데이터
     */

    public String getCouplekey(final int resultCode, final Map data) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            // 현재 접속한 유저의 이메일
            final String userEmail = user.getEmail();

            db.collection(COUPLE_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot snapshots : task.getResult()) {

                            // 접속한 유저의 이메일이 포함된 key가 있다면
                            if ((snapshots.getId()).contains(userEmail)) {

                                // 해당 커플 키 얻기
                                ALBUM_COUPLE_KEY = snapshots.getId();

                                // 폴더 생성 요청이라면
                                if (resultCode == 2) {

                                    // 폴더 생성
                                    createFolder(ALBUM_COUPLE_KEY,data);

                                    // 이미지 데이터 업로드 요청이라면
                                } else if (resultCode == 3) {

                                    // 이미지 정보 업로드
                                    putAlbumData(ALBUM_COUPLE_KEY, data);
                                }
                            }
                        }
                    }
                }
            });
        }

        return ALBUM_COUPLE_KEY;
    }
    // 업로드 된 사진 데이터의 이미지를 저장하는 로직
    private void uploadImage(String directory, Map resultMap) {

        // 업로드한 사진의 path 획득해서 uri 주소로 전환
        Uri file = Uri.fromFile(new File(resultMap.get("imagePath").toString()));

        // FirebaseStorage를 참조하는 storage를 생성
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Storage 객체 얻어서 업로드할 주소 명시
        StorageReference storageReference = storage.getReferenceFromUrl("gs://dudu-dd003.appspot.com");

        StorageReference imageReference = storageReference
                .child("images/" + directory + "/" + file.getLastPathSegment());

        // 파일을 업로드하는 인스턴스
        UploadTask uploadTask = imageReference.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(mContext, "사진 업로드 성공"
                        , Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(mContext, "사진 업로드 실패, 다시 시도해주세요!"
                        , Toast.LENGTH_SHORT).show();

            }
        });

    }

    public ArrayList<PictureData> initAlbumData(String coupleKey) {



        return null;
    }


}