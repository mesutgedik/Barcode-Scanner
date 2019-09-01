package com.example.barcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

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
import java.util.List;

public class Scan extends AppCompatActivity {

   private int count = 0, length = 0;
    private String RawValue = null, Name, Author;
    private Books Book;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Books");
    private final int Barcode_scan=200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Book = new Books();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Barcode_scan){
            if(resultCode==RESULT_OK) {
                Bitmap photo = (Bitmap)data.getExtras().get("data");
                BarcodeRecognition(photo);
            }
        }
    }

    private void BarcodeRecognition(Bitmap photo) {
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_EAN_13,
                                FirebaseVisionBarcode.FORMAT_EAN_8,
                                FirebaseVisionBarcode.FORMAT_UPC_E,
                                FirebaseVisionBarcode.FORMAT_UPC_A)
                        .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);

       detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List <FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            RawValue=rawValue;
                            Toast.makeText(Scan.this, "Scan success", Toast.LENGTH_SHORT).show();


                        }
                        Toast.makeText(Scan.this, RawValue, Toast.LENGTH_SHORT).show();
                        if (RawValue == null) {
                            Toast.makeText(Scan.this, "Barcode Not Found. Try Again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            length = RawValue.length() - 1;
                            new BookSearch().execute();

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Scan.this, "Scan Failure", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    class BookSearch extends AsyncTask<Void, Void, Void> {


        protected Void doInBackground(Void... params) {
            // Stop if cancelled
            if (isCancelled()) {
                return null;
            }
            while (count < 2) {
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
                    Author = main.getString("authors");
                    if (Name.isEmpty() == true) {
                        count++;
                        RawValue = RawValue.substring(3, length);
                    } else
                        count = 2;

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
            Name = Name.replaceAll("\\:*?\"<>|&'", "");
            Author = Author.replaceAll("\\[*?\"<>|&'", "").replaceAll("\\[\"|\"\\]", "");


            return null;
        }

        protected void onPostExecute(Void result) {
            if (Name == null) {
                Toast.makeText(Scan.this, "Book Not Found", Toast.LENGTH_SHORT).show();
            } else {
                pushDataFirebase(new FirebaseCallBack() {
                    @Override
                    public void onCallBack(String Value) {
                        Book.setBookName(Name);
                        Book.setAuthors(Author);
                        Book.setBsbn(RawValue);
                        myRef.push().setValue(Book);
                        Toast.makeText(Scan.this, "data inserted", Toast.LENGTH_SHORT).show();
                        Intent GoToSearchIntent = new Intent(getApplicationContext(),Search.class);
                        GoToSearchIntent.putExtra("list",Book);
                        startActivity(GoToSearchIntent);
                    }
                });
            }


        }



    }
    private void pushDataFirebase(final FirebaseCallBack firebaseCallBack) {
        Query query = myRef.orderByChild("bsbn").equalTo(RawValue);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(Scan.this, "Data zaten firebasede var", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseCallBack.onCallBack(RawValue);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private interface FirebaseCallBack {
        void onCallBack(String Value);
    }

    public void barcodeScan(View view) {
    Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent,Barcode_scan);
    }

}
