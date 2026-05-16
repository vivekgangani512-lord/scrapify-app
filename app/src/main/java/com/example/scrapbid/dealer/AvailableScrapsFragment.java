package com.example.scrapbid.dealer;

import android.content.Intent;
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
import com.example.scrapbid.adapter.ScrapAdapter;
import com.example.scrapbid.model.Scrap;

import java.util.List;

public class AvailableScrapsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private DatabaseHelper db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_scraps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());
        session = new SessionManager(requireContext());
        recyclerView = view.findViewById(R.id.recycler_available);
        emptyState = view.findViewById(R.id.empty_state);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadScraps();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadScraps();
    }

    private void loadScraps() {
        List<Scrap> scraps = db.getAvailableScraps(session.getUserId());
        if (scraps.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            ScrapAdapter adapter = new ScrapAdapter(scraps, scrap -> {
                Intent intent = new Intent(requireContext(), PlaceBidActivity.class);
                intent.putExtra("scrap_id", scrap.id);
                startActivity(intent);
            }, true);
            recyclerView.setAdapter(adapter);
        }
    }
}
