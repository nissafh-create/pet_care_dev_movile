package com.example.petcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.R;
import com.example.petcareproject.models.Application;
import com.example.petcareproject.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {

    public interface OnChooseClickListener {
        void onChooseClick(Application application);
    }

    private List<Application> applicantList;
    private OnChooseClickListener listener;

    public ApplicantAdapter(List<Application> applicantList, OnChooseClickListener listener) {
        this.applicantList = applicantList;
        this.listener = listener;
    }

    public void updateList(List<Application> newList) {
        this.applicantList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        Application app = applicantList.get(position);

        String initialName = app.getSitterName() != null ? app.getSitterName() : "Sitter";
        holder.tvSitterName.setText(initialName);
        holder.tvInitial.setText(!initialName.isEmpty() ? String.valueOf(initialName.charAt(0)).toUpperCase() : "S");
        holder.tvMessage.setText(app.getMessage());
        holder.tvSitterCity.setText("");
        holder.tvBadge.setText("New");
        holder.tvRatingStays.setText("★ – • 0 stays");

        if (app.getSitterId() != null) {
            FirebaseFirestore.getInstance().collection("users").document(app.getSitterId()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            if (doc.exists()) {
                                User sitter = doc.toObject(User.class);
                                if (sitter != null) {
                                    holder.tvSitterName.setText(sitter.getFullName());
                                    if (sitter.getFullName() != null && !sitter.getFullName().isEmpty()) {
                                        holder.tvInitial.setText(String.valueOf(sitter.getFullName().charAt(0)).toUpperCase());
                                    }
                                    holder.tvSitterCity.setText(sitter.getCity());
                                    holder.tvBadge.setText(sitter.getBadge());
                                    String ratingText = sitter.getCompletedCount() > 0
                                            ? String.valueOf(sitter.getAverageRating())
                                            : "–";
                                    holder.tvRatingStays.setText("★ " + ratingText + " • " + sitter.getCompletedCount() + " stays");
                                }
                            }
                        }
                    });
        }

        holder.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChooseClick(app);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    public static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvSitterName, tvSitterCity, tvBadge, tvRatingStays, tvMessage;
        Button btnChoose;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tvInitial);
            tvSitterName = itemView.findViewById(R.id.tvSitterName);
            tvSitterCity = itemView.findViewById(R.id.tvSitterCity);
            tvBadge = itemView.findViewById(R.id.tvBadge);
            tvRatingStays = itemView.findViewById(R.id.tvRatingStays);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            btnChoose = itemView.findViewById(R.id.btnChoose);
        }
    }
}
