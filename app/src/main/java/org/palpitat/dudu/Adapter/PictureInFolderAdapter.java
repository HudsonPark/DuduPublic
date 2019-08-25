package org.palpitat.dudu.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.palpitat.dudu.Model.PictureData;
import org.palpitat.dudu.R;

import java.util.ArrayList;

public class PictureInFolderAdapter extends RecyclerView.Adapter<PictureInFolderAdapter.PictureViewHolder> {

    private ArrayList<Integer> mPictureArrayList;
    private Context mContext;

    public PictureInFolderAdapter(Context context, ArrayList<Integer> mPictureArrayList){

        this.mPictureArrayList = mPictureArrayList;
        this.mContext = context;

    }


    @Override
    public PictureInFolderAdapter.PictureViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture_in_folder,viewGroup,false);

        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PictureInFolderAdapter.PictureViewHolder pictureViewHolder, int i) {

        // 앨범 이미지 세팅
        Glide.with(mContext).load(mPictureArrayList.get(i)).into(pictureViewHolder.img_pictureInFolder);

    }

    @Override
    public int getItemCount() {
        return mPictureArrayList.size();
    }


    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_pictureInFolder;

        public PictureViewHolder(View itemView) {
            super(itemView);

            if (itemView != null) {
                img_pictureInFolder = itemView.findViewById(R.id.img_pictureInFolder);
            }

        }
    }
}
