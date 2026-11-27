package com.example.petcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.R;
import com.example.petcareproject.models.Application;
import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    public interface OnApplicationClickListener {
        void onApplicationClick(Application application);
    }

    private List<Application> applicationList;
    private OnApplicationClickListener listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ApplicationAdapter(List<Application> applicationList, OnApplicationClickListener listener) {
        this.applicationList = applicationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application app = applicationList.get(position);

        holder.tvPetInfo.setText("Loading request...");
        holder.tvCityDates.setText("");
        holder.tvPrice.setText("");

        if (app.getRequestId() != null) {
            db.collection("careRequests").document(app.getRequestId()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            CareRequest req = doc.toObject(CareRequest.class);
                            if (req != null) {
                                holder.tvPetInfo.setText(req.getPetName() + " • " + req.getPetSpecies());
                                holder.tvCityDates.setText(req.getCity() + " • " + req.getStartDate() + " → " + req.getEndDate());
                                holder.tvPrice.setText((int) req.getPricePerDay() + " TND/day");
                            }
                        }
                    });
        }

        holder.tvAppStatus.setText(app.getStatus());
        if (Constants.APP_ACCEPTED.equals(app.getStatus())) {
            holder.tvAppStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_dark));
        } else if (Constants.APP_REJECTED.equals(app.getStatus())) {
            holder.tvAppStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.orange));
        } else {
            holder.tvAppStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brown));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApplicationClick(app);
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetInfo, tvAppStatus, tvCityDates, tvPrice;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPetInfo = itemView.findViewById(R.id.tvPetInfo);
            tvAppStatus = itemView.findViewById(R.id.tvAppStatus);
            tvCityDates = itemView.findViewById(R.id.tvCityDates);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
