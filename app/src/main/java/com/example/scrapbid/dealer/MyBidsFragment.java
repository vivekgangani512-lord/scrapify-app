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
import com.example.scrapbid.adapter.BidAdapter;
import com.example.scrapbid.model.Bid;
import com.example.scrapbid.model.Scrap;

import java.util.List;

public class MyBidsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private DatabaseHelper db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_bids, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());
        session = new SessionManager(requireContext());
        recyclerView = view.findViewById(R.id.recycler_my_bids);
        emptyState = view.findViewById(R.id.empty_state);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadBids();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBids();
    }

    private void loadBids() {
        List<Bid> bids = db.getBidsByDealer(session.getUserId());
        if (bids.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            BidAdapter adapter = new BidAdapter(bids, bid -> {}, false, Scrap.STATUS_OPEN);
            recyclerView.setAdapter(adapter);
        }


    }
}
