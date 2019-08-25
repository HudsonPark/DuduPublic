package org.palpitat.dudu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.palpitat.dudu.Interface.AlbumDialogInterface;
import org.palpitat.dudu.R;

public class SetPictureNameDialog implements View.OnClickListener {

    private Context mContext;
    private String pictureName;
    private Dialog dialog;
    private EditText edt_pictureName;
    private Button btn_upload;
    private Button btn_cancelUpload;
    private AlbumDialogInterface dialogInterface;

    public SetPictureNameDialog(Context context){
        this.mContext = context;
    }

    // 2019.06.23 다이얼로그의 결과값을 리턴해주는 리스너 설정 by Hudson
    public void setDialogListener(AlbumDialogInterface albumDialogInterface){
        this.dialogInterface = albumDialogInterface;
    }


    // 2019.06.23 폴더 추가 다이얼로그를 호출하는 로직 by Hudson
    public void callDialog(){

        dialog = new Dialog(mContext);

        // 액티비티의 타이틀바를 숨긴다.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃 설정
        dialog.setContentView(R.layout.dialog_set_name);

        // 다이얼로그 바깥 쪽 클릭 시 무시
        dialog.setCanceledOnTouchOutside(false);

        // 커스텀 다이얼로그 보이기
        dialog.show();

        edt_pictureName = dialog.findViewById(R.id.edt_pictureName);
        btn_upload = dialog.findViewById(R.id.btn_upload);
        btn_cancelUpload = dialog.findViewById(R.id.btn_cancelUpload);
        btn_upload.setOnClickListener(this);
        btn_cancelUpload.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_upload :

                // 입력한 폴더명이 비어 있는지 체크
                if (!TextUtils.isEmpty(edt_pictureName.getText().toString())) {
                    // 문자열 앞 뒤에 띄어쓰기 제거
                    String trimmedName = edt_pictureName.getText().toString().trim();

                    if (trimmedName.length()!=0) {

                        // todo : 동일 이름의 폴더가 있는 지 검증하는 로직 필요

                        pictureName = trimmedName;
                        dialogInterface.onPositiveClicked(3, pictureName);
                        dialog.dismiss();

                    } else {
                        Toast.makeText(mContext, "폴더명 앞뒤에 띄어쓰기는 포함 될 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "폴더명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_cancelUpload :
                dialog.dismiss();
                break;
        }
    }


}
