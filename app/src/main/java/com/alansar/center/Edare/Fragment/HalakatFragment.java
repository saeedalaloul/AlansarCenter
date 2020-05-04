package com.alansar.center.Edare.Fragment;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Adapter.HalakaAdapter;
import com.alansar.center.Models.Group;
import com.alansar.center.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 */
public class HalakatFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Group> groups;
    private RecyclerView rv;
    private HalakaAdapter halakaAdapter;
    private ListenerRegistration registration;

    public HalakatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edare_halakat, container, false);
        initialized(view);
        halakaAdapter = new HalakaAdapter(groups);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        rv.setAdapter(halakaAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.currentSTAGE != null) {
            LoadData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void initialized(View view) {
        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
        groups = new ArrayList<>();
        rv = view.findViewById(R.id.halakat_rv);
    }

    private void LoadData() {
        db.collection("Group").whereEqualTo("stage", Common.currentSTAGE).limit(10)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        groups.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            groups.add(snapshots.toObject(Group.class));
                            halakaAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void SearchData(String newText) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        CollectionReference Ref = db.collection("Group");
        Query query = Ref.whereEqualTo("stage", Common.currentSTAGE);
        if (newText.isEmpty()) {
            groups.clear();
            halakaAdapter.notifyDataSetChanged();
            LoadData();
        } else {
            groups.clear();
            query = query.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        isFound.set(true);
                        groups.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            groups.add(snapshots.toObject(Group.class));
                            halakaAdapter.notifyDataSetChanged();
                        }
                    } else {
                        groups.clear();
                        halakaAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    halakaAdapter.notifyDataSetChanged();
                    if (!isFound.get()) {
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fg_menu, menu);
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // halakaAdapter.getFilter().filter(query);
                SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //halakaAdapter.getFilter().filter(newText);
                SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }
}