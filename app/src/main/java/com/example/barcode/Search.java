package com.example.barcode;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class Search extends AppCompatActivity {

    TextView tv_Book, tv_Author, tv_Isbn;
    Button search;
    Books Book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tv_Book = findViewById(R.id.book_name);
        tv_Author = findViewById(R.id.writer);
        tv_Isbn = findViewById(R.id.isbn_code);
        search = findViewById(R.id.scan_again);
        Intent i=getIntent();
        Book= (Books) i.getSerializableExtra("list");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_Book.setText("BookName: " + Book.getBookName());
        tv_Author.setText("Author: " + Book.getAuthors());
        tv_Isbn.setText("Isbn: " + Book.getBsbn());



    }

}


