package com.example.barcode;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    TextView tv_Book,tv_Author,tv_Isbn;
    String RawValue=null,Name,Author;
    Button search;
    int count=0,length=0;
    Books Book;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getInstance().getReference("Books");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tv_Book = findViewById(R.id.book_name);
        tv_Author=findViewById(R.id.writer);
        tv_Isbn=findViewById(R.id.isbn_code);
        search =findViewById(R.id.scan_again);
        Bundle raw = getIntent().getExtras();
        RawValue = raw.getString("raw");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }});

        if(RawValue == null){
            finish();
            Toast.makeText(Search.this, "Barcode Not Found. Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        length=RawValue.length()-1;
        Book = new Books();
        BookSearch bk = new BookSearch();
        bk.execute();
    }

class BookSearch extends AsyncTask<Void, Void, Void>{


     protected Void doInBackground(Void... params) {
            // Stop if cancelled
            if(isCancelled()){
                return null;
            }
    while(count<2) {
     String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=" + RawValue;
        try {
        HttpURLConnection connection = null;
        // Build Connection.
        try {
            URL url = new URL(apiUrlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000); // 5 seconds
            connection.setConnectTimeout(5000); // 5 seconds
        } catch (MalformedURLException e) {
            // Impossible: The only two URLs used in the app are taken from string resources.
            e.printStackTrace();
        } catch (ProtocolException e) {
            // Impossible: "GET" is a perfectly valid request method.
            e.printStackTrace();
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
            connection.disconnect();
            return null;
        }

        // Read data from response.
        StringBuilder builder = new StringBuilder();
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = responseReader.readLine();
        while (line != null) {
            builder.append(line);
            line = responseReader.readLine();
        }
        String responseString = builder.toString();
        Log.d(getClass().getName(), "Response String: " + responseString);
        JSONObject responseJson = new JSONObject(responseString);
        // Close connection and return response code.
        connection.disconnect();
        JSONArray listArray = responseJson.getJSONArray("items");
        JSONObject firstObj = listArray.getJSONObject(0);
        JSONObject main = firstObj.getJSONObject("volumeInfo");

        Name = main.getString("title");
        Name = Name.replaceAll("[\\/:*?\"<>|&']","");
        Author= main.getString("authors");
        Author = Author.replaceAll("[;\\/:*?\"<>|&']","");
        if(Name.isEmpty()==true){
            count++;
            RawValue=RawValue.substring(3,length);
        }
        else
            count=2;
    } catch (SocketTimeoutException e) {
        Log.w(getClass().getName(), "Connection timed out. Returning null");
        return null;
    } catch (IOException e) {
        Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
        e.printStackTrace();
        return null;
    } catch (JSONException e) {
        Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
        e.printStackTrace();
        return null;
    }
}
            return null;
        }

     protected void onPostExecute(Void result) {
            if(Name == null){
                Toast.makeText(Search.this, "Book Not Found", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                tv_Book.setText("Name: "+ Name);
                tv_Author.setText("Author: "+ Author);
                tv_Isbn.setText("Isbn: "+ RawValue);
                Book.setBookName(Name);
                Book.setAuthors(Author);
                Book.setIsbn(RawValue);
                myRef.push().setValue(Book);
                Toast.makeText(Search.this, "data inserted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



