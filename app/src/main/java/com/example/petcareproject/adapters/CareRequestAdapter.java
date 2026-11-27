package com.example.petcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.R;
import com.example.petcareproject.models.CareRequest;

import java.util.List;

public class CareRequestAdapter extends RecyclerView.Adapter<CareRequestAdapter.RequestViewHolder> {

    public interface OnRequestClickListener {
        void onRequestClick(CareRequest request);
    }

    private List<CareRequest> requestList;
    private OnRequestClickListener listener;

    public CareRequestAdapter(List<CareRequest> requestList, OnRequestClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    public void updateList(List<CareRequest> newList) {
        this.requestList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_care_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        CareRequest request = requestList.get(position);
        holder.tvPetInfo.setText(request.getPetName() + " • " + request.getPetSpecies());
        holder.tvCityDates.setText(request.getCity() + " • " + request.getStartDate() + " → " + request.getEndDate());
        holder.tvPrice.setText((int) request.getPricePerDay() + " TND/day");
        holder.tvStatus.setText(request.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRequestClick(request);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetInfo, tvStatus, tvCityDates, tvPrice, tvExtra;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPetInfo = itemView.findViewById(R.id.tvPetInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCityDates = itemView.findViewById(R.id.tvCityDates);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvExtra = itemView.findViewById(R.id.tvExtra);
        }
    }
}
