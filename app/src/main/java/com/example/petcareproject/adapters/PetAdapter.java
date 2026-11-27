package com.example.petcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcareproject.R;
import com.example.petcareproject.models.Pet;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    public interface OnPetEditListener {
        void onEditPet(Pet pet);
    }

    private List<Pet> petList;
    private OnPetEditListener listener;

    public PetAdapter(List<Pet> petList, OnPetEditListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

    public void updateList(List<Pet> newList) {
        this.petList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.tvPetNameSpecies.setText(pet.getName() + " • " + pet.getSpecies());
        holder.tvEditPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditPet(pet);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetNameSpecies, tvEditPet;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPetNameSpecies = itemView.findViewById(R.id.tvPetNameSpecies);
            tvEditPet = itemView.findViewById(R.id.tvEditPet);
        }
    }
}
