package com.andrew.printer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.andrew.printf.model.BluetoothModel;

import java.util.List;

/**
 * 蓝牙列表RecyclerView适配器
 */
public class BluetoothRecyclerViewAdapter extends RecyclerView.Adapter<BluetoothRecyclerViewAdapter.Holder> {

    Context context;

    List<BluetoothModel> dates;

    private OnClickItemLister onClickItemLister;

    public void setOnClickItemLister(OnClickItemLister onClickItemLister) {
        this.onClickItemLister = onClickItemLister;
    }

    public BluetoothRecyclerViewAdapter(Context context, List<BluetoothModel> dates) {
        this.context = context;
        this.dates = dates;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_show_blue, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        BluetoothModel bluetoothModel = dates.get(i);
        holder.tvBlueListAddress.setText(bluetoothModel.getBluetoothMac());
        holder.tvBlueListName.setText(bluetoothModel.getBluetoothName());
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView tvBlueListName;
        private final TextView tvBlueListAddress;

        public Holder(@NonNull final View itemView) {
            super(itemView);
            tvBlueListName = itemView.findViewById(R.id.tv_blue_list_name);
            tvBlueListAddress = itemView.findViewById(R.id.tv_blue_list_address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == -1) {
                        return;
                    }
                    if (onClickItemLister != null) {
                        onClickItemLister.onClick(itemView, position);
                    }
                }
            });
        }
    }

    public interface OnClickItemLister {
        void onClick(View view, int position);
    }
}
