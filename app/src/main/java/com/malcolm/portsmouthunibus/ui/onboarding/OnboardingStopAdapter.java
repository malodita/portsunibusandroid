package com.malcolm.portsmouthunibus.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.utilities.ImageGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnboardingStopAdapter extends RecyclerView.Adapter<OnboardingStopAdapter.ViewHolder> {

    private List<String> stops;
    private OnStopSelectedListener listener;

    interface OnStopSelectedListener{
        void onStopSelected(int position);
    }


    OnboardingStopAdapter(String[] stops, OnboardingStopFragment fragment) {
        this.stops = new ArrayList<>(Arrays.asList(stops));
        try {
            listener = fragment;
        } catch (ClassCastException e){
            throw new ClassCastException(fragment.toString()
                    + " must implement OnStopSelectedListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_intro, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.label.setText(stops.get(position));
        ImageGenerator.generateIntroStopImage(holder.itemView.getContext(), stops.get(position)).into(holder.image);
        holder.itemView.setOnClickListener(l -> listener.onStopSelected(position));
    }

    public void removeItems(){
        do {
            stops.remove(stops.size() - 1);
            notifyItemRemoved(stops.size());
            notifyItemRangeChanged(stops.size(), stops.size());
        } while(stops.size() > 0);

    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        ImageView image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.onboarding_0_text);
            image = itemView.findViewById(R.id.onboarding_0_image);

        }
    }
}
