package com.optic.socialmedia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.optic.socialmedia.R;
import com.optic.socialmedia.models.SliderModel;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {


    private Context context;
    private   List<SliderModel> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context, List<SliderModel> lista) {
        this.context = context;
        mSliderItems = lista;
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderModel sliderItem = mSliderItems.get(position);
        System.out.println("imagen adaptadadorr " + sliderItem.getImagen());
        if (sliderItem.getImagen() != null) {
            Picasso.with(context).load(sliderItem.getImagen()).into(viewHolder.imagen);
        }

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imagen;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagenSlider);
            this.itemView = itemView;
        }
    }

}