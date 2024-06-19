package com.example.blacksuits.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacksuits.DataClass.LegalAgreementDataClass;
import com.example.blacksuits.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterForLegalAgreement extends RecyclerView.Adapter<RecyclerViewAdapterForLegalAgreement.ViewHolder>{
    private final LayoutInflater mInflater;
    private final List<LegalAgreementDataClass> mData;
    private ItemClickListener mClickListener;

    private ArrayList<LegalAgreementDataClass> filteredData;


    public RecyclerViewAdapterForLegalAgreement(LayoutInflater mInflater, ArrayList<LegalAgreementDataClass> mData) {
        this.mInflater = mInflater;
        this.mData = mData;
        this.filteredData = new ArrayList<>(mData);
        Log.e("klajsdfa",";klasjdfa");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.legal_agreement_item_layout, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String clientUsername = mData.get(position);
//        holder.clientTextView.setText(clientUsername);

        LegalAgreementDataClass legalAgreement = filteredData.get(position);

        holder.title.setText(legalAgreement.getTitle());
        holder.textDescription.setText(legalAgreement.getContent());

//        holder.textDescription.setText("Legal doctrines must uphold principles of fairness and equity. They should promote just outcomes and avoid discrimination or bias. A fair legal doctrine contributes to a justice system that is perceived as impartial and reliable.Legal doctrines must uphold principles of fairness and equity. They should promote just outcomes and avoid discrimination or bias. A fair legal doctrine contributes to a justice system that is perceived as impartial and reliable.Legal doctrines must uphold principles of fairness and equity. They should promote just outcomes and avoid discrimination or bias. A fair legal doctrine contributes to a justice system that is perceived as impartial and reliable.Legal doctrines must uphold principles of fairness and equity. They should promote just outcomes and avoid discrimination or bias. A fair legal doctrine contributes to a justice system that is perceived as impartial and reliable.");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.collapsibleContent.getVisibility() == View.VISIBLE) {
                    holder.collapsibleContent.setVisibility(View.GONE);
                    holder.cardView.setCardBackgroundColor(
                            ContextCompat.getColor(holder.cardView.getContext(), R.color.collapsed_color));

                    holder.title.setTextColor(ContextCompat.getColor(holder.title.getContext(), R.color.white));
                    holder.backButton.setVisibility(View.VISIBLE);
                } else {
                    holder.collapsibleContent.setVisibility(View.VISIBLE);
                    holder.cardView.setCardBackgroundColor(
                            ContextCompat.getColor(holder.cardView.getContext(), R.color.expanded_color));
                    holder.title.setTextColor(ContextCompat.getColor(holder.title.getContext(), R.color.black));
                    holder.backButton.setVisibility(View.GONE);
                }
            }
        });
    }




    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public LegalAgreementDataClass getItem(int position) {
        return filteredData.get(position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textDescription, title;
        CardView cardView;
        LinearLayout collapsibleContent;
        ImageView backButton ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.textDescription);
            cardView = itemView.findViewById(R.id.agreementCardView);
            collapsibleContent = itemView.findViewById(R.id.collapsibleContent);
            title = itemView.findViewById(R.id.title);
            backButton = itemView.findViewById(R.id.back_button_recyclerview);
            itemView.setOnClickListener(this);

        }


        public void onClick(View view) {
            if (mClickListener != null) {
//                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public void filter(@NonNull String query) {
        filteredData.clear();

        if (query.isEmpty()) {
            filteredData.addAll(mData);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            for (LegalAgreementDataClass item : mData) {
                // Check if the title contains the query (case-insensitive)
                if (item.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredData.add(item);
                    Log.e("asldjfaslkdfj","klasjdfaskljdf");
                }
            }
        }

        notifyDataSetChanged();
    }

}
