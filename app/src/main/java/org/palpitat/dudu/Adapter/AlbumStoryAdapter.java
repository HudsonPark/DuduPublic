package org.palpitat.dudu.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.palpitat.dudu.Model.PictureData;

import org.palpitat.dudu.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AlbumStoryAdapter extends RecyclerView.Adapter<AlbumStoryAdapter.StoryViewHolder>  {


    private ArrayList<PictureData> mPictureDataArrayList;
    private Context mContext;

    public AlbumStoryAdapter(Context context, ArrayList<PictureData> pictureDataArrayList) {
        this.mPictureDataArrayList = pictureDataArrayList;
        this.mContext = context;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_story,viewGroup,false);

        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoryViewHolder storyViewHolder, int position) {

        // 날짜 데이터 포맷 변경
        SimpleDateFormat dateFormat = new SimpleDateFormat("y 년 M 월 d 일", Locale.KOREA);

        // 현재 아이템의 날짜 포맷 변경
        String nowDate = dateFormat.format(mPictureDataArrayList.get(position).getDate());

        // 2019.07.17 업로드 시간에 따라 by Hudson
        if (position!=0){

            // 이전 아이템의 날짜 포맷 변경
            String pastDate = dateFormat.format(mPictureDataArrayList.get(position -1).getDate());

            // 이전 데이터의 업로드 시간과 비교해서 다르다면
            if (!(nowDate.equals(pastDate))){

                // 업로드 시간 관련 뷰 VISIBLE 처리
                storyViewHolder.tv_date.setVisibility(View.VISIBLE);
                storyViewHolder.img_dash1.setVisibility(View.VISIBLE);
                storyViewHolder.img_dash2.setVisibility(View.VISIBLE);
                storyViewHolder.tv_date.setText(nowDate);

            } else {

                // 업로드 시간 관련 뷰 GONE 처리
                storyViewHolder.tv_date.setVisibility(View.GONE);
                storyViewHolder.img_dash1.setVisibility(View.GONE);
                storyViewHolder.img_dash2.setVisibility(View.GONE);

            }
        } else {

            // 첫번째 데이터
            storyViewHolder.tv_date.setVisibility(View.VISIBLE);
            storyViewHolder.tv_date.setText(nowDate);
            storyViewHolder.img_dash1.setVisibility(View.VISIBLE);
            storyViewHolder.img_dash2.setVisibility(View.VISIBLE);

        }

        storyViewHolder.tv_title.setText(mPictureDataArrayList.get(position).getTitle());
        Glide.with(mContext).load(mPictureDataArrayList.get(position).getImage()).into(storyViewHolder.img_pic);

    }

    @Override
    public int getItemCount() {
        return mPictureDataArrayList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_date;
        private ImageView img_dash1;
        private ImageView img_dash2;
        private ImageView img_pic;
        private TextView tv_title;

        public StoryViewHolder(View itemView) {
            super(itemView);

            if (itemView != null) {
                tv_date = itemView.findViewById(R.id.tv_date);
                img_dash1 = itemView.findViewById(R.id.img_dash1);
                img_dash2 = itemView.findViewById(R.id.img_dash2);
                img_pic = itemView.findViewById(R.id.img_pic);
                tv_title = itemView.findViewById(R.id.tv_title);
            }
        }
    }
}
