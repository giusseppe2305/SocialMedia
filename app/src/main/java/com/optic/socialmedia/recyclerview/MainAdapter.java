package com.optic.socialmedia.recyclerview;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.optic.socialmedia.R;
import com.optic.socialmedia.models.Photo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    ArrayList<Photo> mainModels;
    Activity context;

    public MainAdapter( Activity context) {
        mainModels= new ArrayList<>();



        this.context = context;
    }
    public ArrayList<Photo> getList(){
        return mainModels;
    }
    public Photo getLastItem(){
        return mainModels.get(getItemCount()-1);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_irem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
          if(mainModels.get(position).isFromXMLDrawable()){
              holder.iv.setImageResource(mainModels.get(position).getImageDrawable());
          }else{
              String url=mainModels.get(position).getAbsolutePhotoPath();

              Bitmap bitmap =  BitmapFactory.decodeFile(url);
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);

              holder.iv.setImageBitmap( BitmapFactory.decodeByteArray(baos.toByteArray(),0,baos.size()));
          }

          holder.btnDeleteImage.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  mainModels.remove(position);
                  notifyItemRemoved(position);
              }
          });
    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        CircleImageView btnDeleteImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.imageViewItem);
            btnDeleteImage=itemView.findViewById(R.id.btnDeleteImage);
        }
    }
}
