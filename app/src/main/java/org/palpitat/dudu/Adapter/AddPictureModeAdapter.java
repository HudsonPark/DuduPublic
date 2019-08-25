package org.palpitat.dudu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.palpitat.dudu.Interface.AlbumDialogInterface;
import org.palpitat.dudu.R;

public class AddPictureModeAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mModeArr;
    private TextView btn_mode;

    // 2019.06.23 사진 추가 다이얼로그의 리스트뷰 관련 어댑터 by Hudson
    public AddPictureModeAdapter(Context m_context, String[] m_modeArr) {
        this.mContext = m_context;
        this.mModeArr = m_modeArr;
    }

    @Override
    public int getCount() {
        return mModeArr.length;
    }

    @Override
    public Object getItem(int position) {
        return mModeArr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_add_pic, null);
            btn_mode = convertView.findViewById(R.id.btn_picmode);
        }


        btn_mode.setText(mModeArr[position]);

        return convertView;
    }
}