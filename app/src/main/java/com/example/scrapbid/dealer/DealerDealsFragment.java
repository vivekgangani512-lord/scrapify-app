package com.example.scrapbid.dealer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrapbid.DatabaseHelper;
import com.example.scrapbid.R;
import com.example.scrapbid.SessionManager;
import com.example.scrapbid.adapter.DealAdapter;
import com.example.scrapbid.model.Deal;

import java.util.List;

public class DealerDealsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private DatabaseHelper db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dealer_deals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());
        session = new SessionManager(requireContext());
        recyclerView = view.findViewById(R.id.recycler_deals);
        emptyState = view.findViewById(R.id.empty_state);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadDeals();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeals();
    }

    private void loadDeals() {
        List<Deal> deals = db.getDealsByDealer(session.getUserId());
        if (deals.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            DealAdapter adapter = new DealAdapter(deals, deal -> {}, false);
            recyclerView.setAdapter(adapter);
        }
    }
}
