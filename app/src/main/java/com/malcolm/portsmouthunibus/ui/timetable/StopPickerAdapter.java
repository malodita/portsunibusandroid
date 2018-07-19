package com.malcolm.portsmouthunibus.ui.timetable;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malcolm.portsmouthunibus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StopPickerAdapter extends RecyclerView.Adapter<StopPickerAdapter.StopViewHolder> {

    private String[] stops;
    protected StopSelectedListener selectedListener;

    public StopPickerAdapter(TimetableFragment fragment, String[] stops) {
        try{
            selectedListener = fragment;
        } catch (ClassCastException c){
            throw new ClassCastException(fragment.toString()
                    + " must implement DialogListener");
        }
        this.stops = stops;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StopViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_stop_picker, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder stopViewHolder, int position) {
        stopViewHolder.stop.setText(stops[position]);
        stopViewHolder.itemView.setOnClickListener(view -> selectedListener.onStopSelected(position));

    }

    @Override
    public int getItemCount() {
        return stops.length;
    }

    protected interface StopSelectedListener{

        /**
         * Used to propagate the selected item on the RecyclerView list for available stops.
         * @param position Position on RecyclerView selected
         */
        void onStopSelected(int position);
    }

    class StopViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.picker_text) TextView stop;

        StopViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
