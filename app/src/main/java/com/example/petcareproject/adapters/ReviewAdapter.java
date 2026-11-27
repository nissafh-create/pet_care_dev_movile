package com.example.petcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.R;
import com.example.petcareproject.models.Review;
import com.example.petcareproject.models.User;
import com.example.petcareproject.utils.FakeDataManager;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        User owner = FakeDataManager.getUserById(review.getOwnerId());
        holder.tvReviewerName.setText(owner != null ? owner.getFullName() : "Owner");

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < review.getRating(); i++) {
            stars.append("★");
        }
        holder.tvStars.setText(stars.toString());
        holder.tvComment.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvStars, tvComment;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvStars = itemView.findViewById(R.id.tvStars);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}
