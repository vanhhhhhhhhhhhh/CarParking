package com.example.carparking.fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carparking.R;
import com.example.carparking.adapters.SearchResultAdapter;
import com.example.carparking.model.SearchResult;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static interface SearchQueryListener {
        void doSearch(String query);
    }

    private TextInputLayout tilSearch;
    private TextInputEditText etSearch;
    private MaterialCardView cvSearchResults;
    private RecyclerView rvSearchResults;
    private SearchResultAdapter adapter;

    private SearchResultAdapter.OnSearchItemSelectedListener onSearchItemSelectedCallback;
    private SearchQueryListener searchQueryListener;
    private List<SearchResult> searchResults = new ArrayList<>();
    private boolean disableSearch = false;
    public void setOnSearchItemSelectedCallback(SearchResultAdapter.OnSearchItemSelectedListener callback) {
        this.onSearchItemSelectedCallback = searchResult -> {
            disableSearch = true;
            etSearch.setText(searchResult.getTitle());
            etSearch.clearFocus();
            cvSearchResults.setVisibility(View.GONE);
            hideSoftKeyBoard();

            callback.onSearchItemSelected(searchResult);
        };

        if (adapter != null) {
            adapter = new SearchResultAdapter(onSearchItemSelectedCallback);
            rvSearchResults.setAdapter(adapter);
            adapter.setSearchResults(searchResults);
        }
    }

    private void hideSoftKeyBoard() {
        Context context = getContext();
        if (context == null) return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

        if(imm != null && imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    public void setSearchQueryListener(SearchQueryListener listener) {
        this.searchQueryListener = listener;
    }

    public void setSearchResults(List<SearchResult> results) {
        this.searchResults = results != null ? results : new ArrayList<>();
        if (adapter != null) {
            adapter.setSearchResults(this.searchResults);
        }

        updateResultsVisibility();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupSearchField();
    }

    private void initViews(View view) {
        tilSearch = view.findViewById(R.id.til_search);
        etSearch = view.findViewById(R.id.et_search);
        cvSearchResults = view.findViewById(R.id.cv_search_results);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
    }

    private void setupRecyclerView() {
        adapter = new SearchResultAdapter(onSearchItemSelectedCallback);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(adapter);
        adapter.setSearchResults(searchResults);
    }

    private void setupSearchField() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (disableSearch) {
                    disableSearch = false;
                    return;
                }
                updateResultsVisibility();
                if (searchQueryListener != null)
                    searchQueryListener.doSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH && searchQueryListener != null) {
                String query = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
                if (!query.isEmpty()) {
                    searchQueryListener.doSearch(query);
                }
                return true;
            }
            return false;
        });
    }

    private void updateResultsVisibility() {
        if (getView() == null) return;
        String searchText = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        boolean hasText = !searchText.isEmpty();
        boolean hasResults = searchResults != null && !searchResults.isEmpty();
        
        cvSearchResults.setVisibility(hasText && hasResults ? View.VISIBLE : View.GONE);
    }

    public String getSearchQuery() {
        return etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
    }

    public void clearSearch() {
        etSearch.setText("");
        searchResults.clear();
        if (adapter != null) {
            adapter.setSearchResults(searchResults);
        }
        updateResultsVisibility();
    }
} 