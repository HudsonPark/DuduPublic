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

public class AddFolderDialog implements View.OnClickListener {

    private Context mContext;
    private String folderName;
    private Dialog dialog;
    private EditText edt_folderName;
    private Button btn_createFolder;
    private Button btn_cancelFolder;
    private AlbumDialogInterface dialogInterface;

    public AddFolderDialog(Context context){
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
        dialog.setContentView(R.layout.dialog_add_folder);

        // 다이얼로그 바깥 쪽 클릭 시 무시
        dialog.setCanceledOnTouchOutside(false);

        // 커스텀 다이얼로그 보이기
        dialog.show();

        edt_folderName = dialog.findViewById(R.id.edt_folderName);
        btn_createFolder = dialog.findViewById(R.id.btn_createFolder);
        btn_cancelFolder = dialog.findViewById(R.id.btn_cancelFolder);
        btn_createFolder.setOnClickListener(this);
        btn_cancelFolder.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_createFolder :

                // 입력한 폴더명이 비어 있는지 체크
                if (!TextUtils.isEmpty(edt_folderName.getText().toString())) {
                    // 문자열 앞 뒤에 띄어쓰기 제거
                    String trimmedName = edt_folderName.getText().toString().trim();

                    if (trimmedName.length()!=0) {

                        folderName = trimmedName;
                        dialogInterface.onPositiveClicked(2, folderName);
                        dialog.dismiss();

                    } else {
                        Toast.makeText(mContext, "폴더명 앞뒤에 띄어쓰기는 포함 될 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "폴더명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_cancelFolder :
                dialog.dismiss();
                break;
        }
    }
}
