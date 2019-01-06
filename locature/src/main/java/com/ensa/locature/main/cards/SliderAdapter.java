package com.ensa.locature.main.cards;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ensa.locature.main.MapActivity;
import com.ensa.locature.main.R;

import java.util.ArrayList;


public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {


    private ArrayList<MapActivity.Agence> agenceList=new ArrayList<>();

    private final int count;
    private final int[] content;
    private final View.OnClickListener listener;

    public SliderAdapter(int[] content, int count, View.OnClickListener listener, ArrayList<MapActivity.Agence> agenceList) {
        this.content = content;
        this.count = count;
        this.listener = listener;
        this.agenceList=agenceList;
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

        if (listener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);
                }
            });
        }

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(SliderCard holder, int position) {
        holder.setContent(content[position % content.length], agenceList.get(position%content.length).getImage());
    }

    @Override
    public void onViewRecycled(SliderCard holder) {
        holder.clearContent();
    }

    @Override
    public int getItemCount() {
        return count;
    }

}
