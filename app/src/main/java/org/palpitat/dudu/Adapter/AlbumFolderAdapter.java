package org.palpitat.dudu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.palpitat.dudu.Model.PictureData;
import org.palpitat.dudu.R;
import org.palpitat.dudu.activity.ImgViewActivity;

import java.util.ArrayList;

public class AlbumFolderAdapter extends RecyclerView.Adapter<AlbumFolderAdapter.FolderViewHolder> {
    private ArrayList<PictureData> folderArrayList;
    private Context context;

    public AlbumFolderAdapter(Context context , ArrayList<PictureData> FolderArrayList){
        this.context = context;
        this.folderArrayList = FolderArrayList;

    }

    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_folder,viewGroup,false);

        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder folderViewHolder, int i) {

        Glide.with(context).load(folderArrayList.get(i).getImage()).into(folderViewHolder.selectImage);

    }

    @Override
    public int getItemCount() {
        return folderArrayList.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        private ImageView selectImage;

        public FolderViewHolder(@NonNull final View itemView) {
            super(itemView);

            if (itemView != null) {
                selectImage = itemView.findViewById(R.id.selectedimage);
            }


            selectImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    
                Intent intent2 = new Intent(view.getContext(), ImgViewActivity.class);

                    view.getContext().startActivity(intent2);
                }
            });

        }
    }


}
