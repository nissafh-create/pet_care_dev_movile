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
import com.example.petcareproject.utils.FakeDataManager;

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
        User sitter = FakeDataManager.getUserById(app.getSitterId());

        String name = sitter != null ? sitter.getFullName() : app.getSitterName();
        holder.tvSitterName.setText(name);
        holder.tvInitial.setText(!name.isEmpty() ? String.valueOf(name.charAt(0)).toUpperCase() : "S");
        holder.tvMessage.setText(app.getMessage());

        if (sitter != null) {
            holder.tvSitterCity.setText(sitter.getCity());
            holder.tvBadge.setText(sitter.getBadge());
            String ratingText = sitter.getCompletedCount() > 0
                    ? String.valueOf(sitter.getAverageRating())
                    : "–";
            holder.tvRatingStays.setText("★ " + ratingText + " • " + sitter.getCompletedCount() + " stays");
        } else {
            holder.tvSitterCity.setText("");
            holder.tvBadge.setText("New");
            holder.tvRatingStays.setText("★ – • 0 stays");
        }

        holder.btnChoose.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChooseClick(app);
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
