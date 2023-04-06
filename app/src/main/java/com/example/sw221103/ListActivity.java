package com.example.sw221103;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListActivity extends AppCompatActivity {
    private SearchView searchView;
    private UserAdapter adapter;

    private String title;
    private String content;
    private long timestamp;

    DAOUser dao;

    String key = "";

    ArrayList<User1> list = new ArrayList<>();

    private ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = findViewById(R.id.rv);
        searchView = findViewById(R.id.sv);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                submitList();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                submitList();
                return false;
            }
        });


        adapter = new UserAdapter(this);

        recyclerView.setAdapter(adapter);

        dao = new DAOUser();

//        mOptions = new FirebaseRecyclerOptions.Builder<User1>()
//                .setQuery(DatabaseReference, list.class)
//                .build();
        loadData();


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        String key = list.get(position).getUser_key();
                        DAOUser dao = new DAOUser();
                        dao.remove(key).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ListActivity.this, "삭제 성공", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ListActivity.this, "삭제 실패:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }

            //@Override
            public void onChildDraw(@NonNull Canvas c, RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.RED)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadData() {
        listener = dao.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    User1 user1 = data.getValue(User1.class);
                    user1.setId(data.getKey());
                    key = data.getKey(); // 키값 가져오기
                    user1.setUser_key(key); // 키값 담기
                    list.add(user1); //리스트에 담기
                }

                submitList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void submitList() {
        String query = searchView.getQuery().toString();
        ArrayList<User1> filteredList = new ArrayList<>();

        for (User1 user1 : list) {
            if (user1.getUser_name().contains(query)) {
                filteredList.add(user1);
            }
        }

        adapter.submitList(new ArrayList<>(filteredList));
    }

    @Override
    protected void onDestroy() {
        if (listener != null) {
            dao.get().removeEventListener(listener);
        }

        super.onDestroy();
    }
}