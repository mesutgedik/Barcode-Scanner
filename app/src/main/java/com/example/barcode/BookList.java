package com.example.barcode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookList extends AppCompatActivity {

    private List<Books> lstBooks;
    private Books books;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mProgressBar = findViewById(R.id.progress_bar);
        books = new Books();
        lstBooks = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerview);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Books");
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        readData(new FirebaseCallBack() {
            @Override
            public void onCallBack(List<Books> bookList) {
                mRecyclerView.setAdapter(new BookRecyclerViewAdapter(BookList.this, lstBooks));
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void readData(final FirebaseCallBack firebaseCallBack){
            mProgressBar.setVisibility(View.VISIBLE);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        books = postSnapshot.getValue(Books.class);
                        lstBooks.add(books);
                    }
                    firebaseCallBack.onCallBack(lstBooks);

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(BookList.this, "The data didn't take", Toast.LENGTH_SHORT).show();
                }
            });
    }
    private interface FirebaseCallBack{
        void onCallBack(List<Books> bookList);
    }
}