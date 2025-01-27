package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RewardClaimPage extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    private ImageView costabtn, starbucksBtn, greggsBtn;
    private Button mapBtn, generateCodeBtn, closeBtn;

    private ImageView iv_qr;
    private String selected = "";
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_claim_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        costabtn = findViewById(R.id.costabtn);
        starbucksBtn = findViewById(R.id.starbucksBtn);
        greggsBtn = findViewById(R.id.greggsBtn);
        mapBtn = findViewById(R.id.mapBtn);
        generateCodeBtn = findViewById(R.id.generateCodeBtn);
        closeBtn = findViewById(R.id.closeBtn);
        iv_qr = findViewById(R.id.iv_qr);

        //When the button is clicked the background changes and the selected variable is set as the name of the shop
        costabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                costabtn.setBackgroundResource(R.color.comment_user);
                starbucksBtn.setBackgroundResource(R.color.white);
                greggsBtn.setBackgroundResource(R.color.white);
                selected = "Costa";
            }
        });

        starbucksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                costabtn.setBackgroundResource(R.color.white);
                starbucksBtn.setBackgroundResource(R.color.comment_user);
                greggsBtn.setBackgroundResource(R.color.white);
                selected = "Starbucks";
            }
        });

        greggsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                costabtn.setBackgroundResource(R.color.white);
                starbucksBtn.setBackgroundResource(R.color.white);
                greggsBtn.setBackgroundResource(R.color.comment_user);
                selected = "Greggs";
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this checks if a store was selected
                if (selected.isEmpty()){
                    Toast.makeText(RewardClaimPage.this, "Select a store", Toast.LENGTH_SHORT).show();
                    return;
                }
                //google maps is opened and shows the locations of the store selecte
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+selected);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        generateCodeBtn.setOnClickListener(view -> {
            //checks if store is selected
            if (selected.isEmpty()){
                Toast.makeText(RewardClaimPage.this, "Select a store", Toast.LENGTH_SHORT).show();
                return;
            }
            disableInput();
            generateQR();
        });

        //this closes the current activity
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void disableInput(){
        //this disables input, so that the user cant change the store or create new QR code
        mapBtn.setClickable(false);
        generateCodeBtn.setClickable(false);
        costabtn.setClickable(false);
        starbucksBtn.setClickable(false);
        greggsBtn.setClickable(false);
    }

    private void generateQR() {
        //this creates the strings that will be used for the QR code
        String qrtext = selected + " " + user.getUid() + " " + user.getDisplayName() + " " + user.getPhoneNumber() + "Reward: 1 Free Drink";

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            //this creates the QR code and shows it in the image view
            BitMatrix matrix = writer.encode(qrtext, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);
            iv_qr.setImageBitmap(bitmap);
            resetPoints();

            //this checks if there is write permission for the external storage
            if(ContextCompat.checkSelfPermission(RewardClaimPage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                saveQRcode();
            }else{
                ActivityCompat.requestPermissions(RewardClaimPage.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
            }


        } catch (WriterException e) {
            throw new RuntimeException(e);
        }


    }

    private void resetPoints() {
        //this sets the reward points to 0
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference r = reference.child(user.getUid());
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Map<String, Object> map = new HashMap<>();
                map.put("drinkPoints", 0);

                r.updateChildren(map);
                return;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("myTag", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this requests permission to save the QR code to the gallery
        if(requestCode==1){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                saveQRcode();
            }else{
                Toast.makeText(RewardClaimPage.this, "Please provide required permission to save the QR", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void saveQRcode() {
        //this saves the QR code to the gallery
        Uri images;
        ContentResolver contentResolver = getContentResolver();
        images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis()+".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
        Uri uri = contentResolver.insert(images, contentValues);

        try {
            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            Toast.makeText(RewardClaimPage.this, "QR code has been saved to your Gallery", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Toast.makeText(RewardClaimPage.this, "Could not save to Gallery", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}