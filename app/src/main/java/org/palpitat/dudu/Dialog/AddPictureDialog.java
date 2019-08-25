package org.palpitat.dudu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import org.palpitat.dudu.Adapter.AddPictureModeAdapter;
import org.palpitat.dudu.Interface.AlbumDialogInterface;
import org.palpitat.dudu.R;

public class AddPictureDialog {

    private Context mContext;
    private Dialog dialog;
    private ListView lv_pictureMode;
    private Button btn_cancelPic;
    private String[] modeArr = {"카메라로", "앨범에서"};
    private AlbumDialogInterface albumDialogInterface;

    public AddPictureDialog(Context context) {
        this.mContext = context;
    }

    // 2019.06.23 다이얼로그 리스너 설정 by Hudson
    public void setDialogListener(AlbumDialogInterface albumDialogInterface) {
        this.albumDialogInterface = albumDialogInterface;

    }

    // 2019.06.23 사진 추가 다이얼로그를 호출하는 로직 by Hudson
    public void callDialog() {

        dialog = new Dialog(mContext);

        // 액티비티의 타이틀바를 숨긴다.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃 설정
        dialog.setContentView(R.layout.dialog_add_pic);

        // 다이얼로그 바깥 쪽 클릭 시 무시
        dialog.setCanceledOnTouchOutside(false);

        // 커스텀 다이얼로그 보이기
        dialog.show();

        lv_pictureMode = dialog.findViewById(R.id.lv_addPic);
        btn_cancelPic = dialog.findViewById(R.id.btn_cancelPic);

        AddPictureModeAdapter addPictureModeAdapter = new AddPictureModeAdapter(mContext, modeArr);

        lv_pictureMode.setAdapter(addPictureModeAdapter);

        lv_pictureMode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        albumDialogInterface.onPositiveClicked(0,null);
                        dialog.dismiss();
                        break;

                    case 1:
                        albumDialogInterface.onPositiveClicked(1,null);
                        dialog.dismiss();
                        break;
                }
            }
        });

        btn_cancelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
