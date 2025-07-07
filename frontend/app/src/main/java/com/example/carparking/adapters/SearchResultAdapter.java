package com.example.carparking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carparking.R;
import com.example.carparking.model.SearchResult;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    public interface OnSearchItemSelectedListener {
        void onSearchItemSelected(SearchResult searchResult);
    }

    private List<SearchResult> searchResults = new ArrayList<>();
    private OnSearchItemSelectedListener onSearchItemSelectedListener;

    public SearchResultAdapter(OnSearchItemSelectedListener listener) {
        this.onSearchItemSelectedListener = listener;
    }

    public void setSearchResults(List<SearchResult> results) {
        this.searchResults = results != null ? results : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        SearchResult result = searchResults.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final MaterialTextView tvTitle;
        private final MaterialTextView tvSubtitle;
        private final View divider;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            divider = itemView.findViewById(R.id.divider);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onSearchItemSelectedListener != null) {
                    onSearchItemSelectedListener.onSearchItemSelected(searchResults.get(position));
                }
            });
        }

        public void bind(SearchResult result) {
            tvTitle.setText(result.getTitle());
            tvSubtitle.setText(result.getSubtitle());

            // Set appropriate icon based on result type
            if (result.getType() == SearchResult.SearchResultType.PARKING_LOCATION) {
                ivIcon.setImageResource(R.drawable.ic_parking);
            } else {
                ivIcon.setImageResource(R.drawable.ic_location_pin);
            }

            // Hide divider for last item
            int position = getAdapterPosition();
            divider.setVisibility(position == searchResults.size() - 1 ? View.GONE : View.VISIBLE);
        }
    }
} 