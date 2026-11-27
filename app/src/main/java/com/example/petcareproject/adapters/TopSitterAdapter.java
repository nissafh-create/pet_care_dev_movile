package com.example.petcareproject.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.R;
import com.example.petcareproject.models.User;

import java.util.List;

public class TopSitterAdapter extends RecyclerView.Adapter<TopSitterAdapter.TopSitterViewHolder> {

    private List<User> sitterList;

    public TopSitterAdapter(List<User> sitterList) {
        this.sitterList = sitterList;
    }

    @NonNull
    @Override
    public TopSitterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_sitter, parent, false);
        return new TopSitterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopSitterViewHolder holder, int position) {
        User sitter = sitterList.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        String name = sitter.getFullName();
        holder.tvSitterName.setText(name);
        holder.tvInitial.setText(!name.isEmpty() ? String.valueOf(name.charAt(0)).toUpperCase() : "S");
        holder.tvSitterCity.setText(sitter.getCity());
        holder.tvBadge.setText(sitter.getBadge());

        String ratingText = sitter.getCompletedCount() > 0
                ? String.valueOf(sitter.getAverageRating())
                : "–";
        holder.tvRatingStays.setText("★ " + ratingText + " • " + sitter.getCompletedCount() + " stays");

        if (sitter.getAcceptedAnimals() != null && !sitter.getAcceptedAnimals().isEmpty()) {
            holder.tvAcceptedAnimals.setText(TextUtils.join(" • ", sitter.getAcceptedAnimals()));
        } else {
            holder.tvAcceptedAnimals.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return sitterList.size();
    }

    public static class TopSitterViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvInitial, tvSitterName, tvSitterCity, tvRatingStays, tvBadge, tvAcceptedAnimals;

        public TopSitterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvInitial = itemView.findViewById(R.id.tvInitial);
            tvSitterName = itemView.findViewById(R.id.tvSitterName);
            tvSitterCity = itemView.findViewById(R.id.tvSitterCity);
            tvRatingStays = itemView.findViewById(R.id.tvRatingStays);
            tvBadge = itemView.findViewById(R.id.tvBadge);
            tvAcceptedAnimals = itemView.findViewById(R.id.tvAcceptedAnimals);
        }
    }
}
