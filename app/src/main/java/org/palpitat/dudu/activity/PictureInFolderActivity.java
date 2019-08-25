package org.palpitat.dudu.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.palpitat.dudu.Adapter.PictureInFolderAdapter;
import org.palpitat.dudu.Dialog.AddPictureDialog;
import org.palpitat.dudu.Interface.AlbumDialogInterface;
import org.palpitat.dudu.Model.PictureData;
import org.palpitat.dudu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PictureInFolderActivity extends AppCompatActivity {

    private RecyclerView rv_pictureList;
    private FloatingActionButton fb_addPicInFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_in_folder);

        // 2019.08.18 뷰 초기화 관련 작업 진행 by Hudson

        // 뷰 초기화
        initview();

        // 앨범 이미지 초기화
        initPictureData();

        // 이미지 추가 버튼 클릭 시 처리
        fb_addPicInFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddPictureDialog addPictureDialog = new AddPictureDialog(getApplicationContext());
                addPictureDialog.setDialogListener(new AlbumDialogInterface() {
                    @Override
                    public void onPositiveClicked(int resultCode, String result) {


                    }

                    @Override
                    public void onNegativeClicked() {
                        Toast.makeText(PictureInFolderActivity.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                addPictureDialog.callDialog();
            }
        });




    }

    private void initPictureData() {
        ArrayList<Integer> pictureDataArrayList = new ArrayList<>();
        pictureDataArrayList.add(R.drawable.img_couple1);
        pictureDataArrayList.add(R.drawable.img_couple2);

        PictureInFolderAdapter pictureInFolderAdapter = new PictureInFolderAdapter(getApplicationContext(),pictureDataArrayList);

        // span이 3인 그리드 레이아웃 매니저
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);

        rv_pictureList.setLayoutManager(gridLayoutManager);
        rv_pictureList.setAdapter(pictureInFolderAdapter);
    }

    private void initview() {

        rv_pictureList = findViewById(R.id.rv_pictureList);
        fb_addPicInFolder = findViewById(R.id.fb_addPicInFolder);

    }
}
