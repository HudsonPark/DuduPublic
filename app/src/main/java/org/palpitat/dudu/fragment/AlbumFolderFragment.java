package org.palpitat.dudu.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.palpitat.dudu.Adapter.AlbumFolderAdapter;
import org.palpitat.dudu.Data.ConnectAlbumDB;
import org.palpitat.dudu.Dialog.AddFolderDialog;
import org.palpitat.dudu.Dialog.AddPictureDialog;
import org.palpitat.dudu.Dialog.SetPictureNameDialog;
import org.palpitat.dudu.Interface.AlbumDialogInterface;
import org.palpitat.dudu.Model.PictureData;
import org.palpitat.dudu.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlbumFolderFragment extends Fragment implements View.OnClickListener {

    // View
    private RecyclerView rv_folder;
    private FloatingActionButton fb_addFolder;
    private AlbumFolderAdapter folderAdapter;

    // Request Code
    private static final int RC_FROM_CAMERA = 0;
    private static final int RC_FROM_ALBUM = 1;
    private static final int RC_ADD_FOLDER = 2;
    private static final int RC_SET_NAME = 3;
    private static final int RC_PERMISSION = 1000;

    private boolean isPermission = false;

    // 이미지 관련 변수
    private File folder;
    private String currentPhotoPath; // 실제 사진 파일 경로
    private Uri photoUri;

    private ArrayList<PictureData> folderArrayList;

    // 앨범 DB 접근 클래스
    private ConnectAlbumDB connectAlbumDB;
    // DB에 저장될 폴더 이름
    private String folderName = "기본폴더";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_albumfolder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_folder = view.findViewById(R.id.rv_folder);
        fb_addFolder = view.findViewById(R.id.fb_addFolder);

        fb_addFolder.setOnClickListener(this);

        // 그리드 레이아웃 선언 및 적용
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rv_folder.setLayoutManager(gridLayoutManager);

        folderArrayList = new ArrayList<>();
        folderAdapter = new AlbumFolderAdapter(getActivity(), folderArrayList);
        rv_folder.setAdapter(folderAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fb_addFolder:
                AddFolderDialog folderDialog = new AddFolderDialog(getActivity());
                folderDialog.setDialogListener(new AlbumDialogInterface() {
                    @Override
                    public void onPositiveClicked(int resultCode, String result) {
                        Toast.makeText(getContext(), result + "가 생성되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNegativeClicked() {
                        Toast.makeText(getContext(), "취소", Toast.LENGTH_SHORT).show();
                    }
                });
                folderDialog.callDialog();

                break;


            case R.id.fb_addPic:
                AddPictureDialog pictureDialog = new AddPictureDialog(getActivity());

                // 2019.06.23 사진 다이얼로그에 리스너 설정 by Hudson
                pictureDialog.setDialogListener(new AlbumDialogInterface() {
                    @Override
                    public void onPositiveClicked(int resultCode, String result) {
                        // 권한 요청
                        requestPermission();

                        // 모든 권한이 허가되었을 때
                        if (isPermission) {
                            if (resultCode == RC_FROM_CAMERA) {
                                goToCamera();

                            } else if (resultCode == RC_FROM_ALBUM) {
                                goToAlbum();
                            }
                        }
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });

                pictureDialog.callDialog();
                break;
        }
    }

    private void requestPermission() {
        // 권한 허가 안 된 퍼미션 array
        ArrayList<String> permissionArray = new ArrayList<>();

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        // 카메라 권한 부여되어 있는지 체크
        if (permissionCheck !=
                PackageManager.PERMISSION_GRANTED) {

            permissionArray.add(Manifest.permission.CAMERA);
        }

        // SD카드 읽기 권한 부여되어 있는지 체크
        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            permissionArray.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // SD카드 쓰기 권한 부여되어 있는지 체크
        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            permissionArray.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionArray.size() > 0) {

            String[] requestArr = new String[permissionArray.size()];
            requestArr = permissionArray.toArray(requestArr);

            // 권한 요청
            ActivityCompat.requestPermissions(getActivity(), requestArr, RC_PERMISSION);

            // 모든 권한이 허가 되어 있을 때
        } else {
            isPermission = true;
        }

    }

    // 2019.06.29 permission Check 후 처리 by Hudson
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case RC_PERMISSION:
                if (grantResults.length < 1) {
                    Toast.makeText(getActivity(), "해당 기능은 권한을 허가해주셔야 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                    break;
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void goToCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {

            // default 폴더 생성
            folder = createDefaultFile();

        } catch (IOException e) {

            Toast.makeText(getActivity(), "이미지 처리 오류!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
        // 디폴트 폴더가 있고, 빌드 버젼이 마시멜로우 이하인 경우 실행
        if (folder != null && Build.VERSION.SDK_INT <= 23) {

            photoUri = Uri.fromFile(folder);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, RC_FROM_CAMERA);

        } else {

            // API 24(누가 버전 이상부터)
            String mountState = Environment.getExternalStorageState();

            // SD카드 마운트된 상태 체크
            if (Environment.MEDIA_MOUNTED.equals(mountState)) {

                // 인텐트 수신할 앱(카메라)이 있는 지 체크
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

                    folder = null;

                    try {

                        folder = createDefaultFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (folder != null) {

                        // 누가 버젼 이상부터는 FileProvider를 활용해 파일 Uri를 얻어야 함
                        photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), folder);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, RC_FROM_CAMERA);
                    }

                }
            }

        }


    }

    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RC_FROM_ALBUM);
        folderAdapter.notifyDataSetChanged();
    }

    // 2019.06.23 받아온 결과 값 보여주는 토스트 창 띄우기 by Hudson
    private void createFolder(int resultCode, String result) {

        if (resultCode == RC_ADD_FOLDER) {

            Toast.makeText(getActivity(), result + " 폴더가 생성되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 결과값이 ok가 아닌경우(취소했을 경우)
        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(getActivity(), "취소되었습니다.", Toast.LENGTH_SHORT).show();

            // 결과값 ok
        } else if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RC_FROM_ALBUM) {

                String imagePath = getRealPathFromUri(data.getData());
                sendPicture(imagePath);

            } else if (requestCode == RC_FROM_CAMERA) {

                sendPicture(currentPhotoPath);
            }
        }
    }

    // 2019.06.30 카메라와 앨범에서 얻은 사진 저장 및 세팅 by Hudson
    private void sendPicture(final String path) {

        // 2019.07.16 사진 이름 띄어주는 다이얼로그 생성 및 리스너 설정 by Hudson
        SetPictureNameDialog pictureNameDialog = new SetPictureNameDialog(getContext());

        pictureNameDialog.setDialogListener(new AlbumDialogInterface() {
            @Override
            public void onPositiveClicked(int resultCode, String result) {

                // 요청코드가 이미지 이름 설정 다이얼로그에서 왔다면
                if (resultCode == RC_SET_NAME) {

                    // 업로드 할 사진 데이터 초기화
                    // 사진 타이틀
                    String title = result;

                    // 사진 업로드 시간
                    Calendar calendar = Calendar.getInstance();
                    long date = calendar.getTimeInMillis();

                    // 2019.07.18 사진 데이터 DB에 추가 by Hudson
                    connectAlbumDB = new ConnectAlbumDB(getContext());

                    connectAlbumDB.connection();

                    PictureData pictureData = new PictureData(folderName, date
                            , path, title);

                    // 이미지 데이터를 map 형태로 전달
                    Map<String, Object> imageMap = new HashMap<>();

                    imageMap.put("folderName", pictureData.getfolderName());
                    imageMap.put("date", pictureData.getDate());
                    imageMap.put("imagePath", pictureData.getImage());
                    imageMap.put("title", pictureData.getTitle());

                    // 커플 키 얻고 그 커플 키가 있다면 이미지 업로드
                    connectAlbumDB.getCouplekey(resultCode, imageMap);
                }
            }

            @Override
            public void onNegativeClicked() {

                Toast.makeText(getContext(), "업로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }

        });
        // 다이얼로그 호출
        pictureNameDialog.callDialog();


        folderAdapter.notifyDataSetChanged();

    }

    // 2019.06.30 실제 파일 경로 획득 by Hudson
    private String getRealPathFromUri(Uri imageUri) {

        int index = 0;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(imageUri, projection, null, null, null);
        // 커서가 맨 위로 이동했다면
        if (cursor.moveToFirst()) {
            index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(index);
    }

    // 2019.06.29 사진의 회전 값 얻기 by Hudson
    private int exifOrientationToDegrees(int exifOrientation) {

        int degree = 0;

        // 사진이 90도 회전일 때
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {

            degree = 90;

            // 사진이 180도 회전일 때
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            degree = 180;

            // 사진이 270도 회전일 때
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {

            degree = 270;

        }

        return degree;
    }


    // 2019.06.29 사진을 정방향대로 회전하기 by Hudson
    private Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);

        // 이미지와 Matrix를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


    //2019.06.29  카메라로 찍은 사진을 실제 파일로 생성하는 코드 by Hudson
    private File createDefaultFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp;


        // 내장 SD CARD 경로 구하기, 이미지가 저장될 폴더 이름
        File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");

        // default 파일 생성
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/DCIM/Camera/"
                + imageFileName);
        // 이미지가 생성될 경로
        currentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }
}