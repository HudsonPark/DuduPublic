package org.palpitat.dudu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.palpitat.dudu.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    GradientDrawable drawable;

    private Uri mImageCaptureUri;
    private String absolutePath;

    private ImageView maleProfileImage;
    private ImageView femaleProfileImage;

    private TextView maleName;
    private TextView femaleName;

    private TextView dDayCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        drawable = (GradientDrawable) getContext().getDrawable(R.drawable.rounded_profileimage);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        maleName = (TextView) view.findViewById(R.id.maleName);
        femaleName = (TextView) view.findViewById(R.id.femaleName);

        dDayCount = (TextView) view.findViewById(R.id.dDayCount);

        maleProfileImage = (ImageView) view.findViewById(R.id.maleProfileImage);
        maleProfileImage.setBackground(drawable);
        maleProfileImage.setClipToOutline(true);

        // 임시 이미지 저장중
        maleProfileImage.setImageResource(R.drawable.male);

        femaleProfileImage = (ImageView) view.findViewById(R.id.femaleProfileImage);
        femaleProfileImage.setBackground(drawable);
        femaleProfileImage.setClipToOutline(true);

        // 임시 이미지 저장중
        femaleProfileImage.setImageResource(R.drawable.female);

        maleProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test("male");
            }
        });

        femaleProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test("female");
            }
        });
    }

    public void test(final String gender) {
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(gender.equals("male")) {
                    doTakePhotoAction();
                } else {
                    doTakePhotoAction();
                }
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(gender.equals("male")) {
                    doTakeAlbumAction("male");
                } else {
                    doTakeAlbumAction("female");
                }
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(getContext())
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    }

    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + System.currentTimeMillis() + ".jpg";
        mImageCaptureUri = FileProvider.getUriForFile(
                getContext(),
                "com.example.duduproject.fileprovider",
                new File(Environment.getExternalStorageDirectory(), url)
        );
//        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction(String gender) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("gender", gender);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String gender = data.getStringExtra("gender");

        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM:
            {
                mImageCaptureUri = data.getData();
                // 이후 처리는 카메라와 같다.
                // 그러므로 break 처리를 하지 않음
            }

            case PICK_FROM_CAMERA:
            {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // CROP할 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE);
                break;
            }

            case CROP_FROM_iMAGE:
            {
                if(resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/SmartWheel" + System.currentTimeMillis() + ".jpg";

                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    if(gender.equals("male")) {
                        maleProfileImage.setImageBitmap(photo);
                        absolutePath = filePath;
                        break;
                    } else {
                        femaleProfileImage.setImageBitmap(photo);
                        absolutePath = filePath;
                        break;
                    }
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()) {
                    f.delete();
                }
            }
        }
    }

//    private void storeCropImage(Bitmap bitmap, String, filePath) {
//        String dirPath = Environment.getExternalStorageDirectory()
//
//    }
}