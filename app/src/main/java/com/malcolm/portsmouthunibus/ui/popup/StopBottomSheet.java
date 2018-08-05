package com.malcolm.portsmouthunibus.ui.popup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.malcolm.portsmouthunibus.R;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class StopBottomSheet extends BottomSheetDialogFragment {

    public interface DialogListener{
        /**
         * Called when an item is selected
         * @param position Position of selected item
         */
        void onItemSelected(int position);
    }

    private Unbinder unbinder;
    protected DialogListener listener;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (DialogListener) getActivity();
        } catch (ClassCastException c){
            throw new ClassCastException(getActivity().toString()
                    + " must implement DialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_bottom_sheet, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setAdapter(new Adapter(this, getResources().getStringArray(R.array.bus_stops_home)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }



    private class Adapter extends RecyclerView.Adapter<ViewHolder>{

        private String[] stops;
        private StopBottomSheet stopBottomSheet;

        private Adapter(StopBottomSheet stopBottomSheet, String[] stops) {
            this.stops = stops;
            this.stopBottomSheet = stopBottomSheet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bottom_sheet, parent,false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.listItem.setText(stops[position]);
            holder.layout.setOnClickListener(v -> {
                listener.onItemSelected(position);
                stopBottomSheet.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return stops.length;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView listItem;
        private FrameLayout layout;

        private ViewHolder(View itemView) {
            super(itemView);
            listItem = itemView.findViewById(R.id.list_item);
            layout = itemView.findViewById(R.id.list_layout);
        }
    }
}
