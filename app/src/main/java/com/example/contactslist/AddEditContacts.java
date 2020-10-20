package com.example.contactslist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddEditContacts extends AppCompatActivity {
    private CircularImageView profileIv;
    private EditText nameEt,phoneEt,emailEt;
    // whatsapp & caregiver/child
    private FloatingActionButton saveBtn;
    // permission
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    // iamge pick
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    // arrays of permission
    private String []cameraPermission; // for camera & gallery
    private String []storagePermission; // fro gallery only

    // var contains data to save
   private Uri imageUri;
   private String name,phone,email;

   //db helper
    private MyDbHelper dbHelper;


    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contacts);
        //init
        actionBar = getSupportActionBar();
        //title of the activity
        actionBar.setTitle("Add contact");
        // back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //initinalize
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        // whatsapp & caregiver/child
        saveBtn =  findViewById(R.id.saveBtn);

        // ini db helper
        dbHelper = new MyDbHelper(this);


        // init arrays permissions
        cameraPermission =  new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission =  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // dialog for image
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show image pick
                imagePickDialog();
            }


        });

        // save button
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }

        });

    } // end onCreate method

    private void inputData() {
        //get data
        name  = ""+ nameEt.getText().toString().trim();
        phone = ""+phoneEt.getText().toString().trim();
        email = ""+emailEt.getText().toString().trim();

        // save to db
        String timestamp = ""+ System.currentTimeMillis();
        long id = dbHelper.insertRecord(
                ""+ name,
                ""+ imageUri,
                ""+ phone,
                ""+ email);

        Toast.makeText(this,"contact added successfully against ID"+id, Toast.LENGTH_SHORT).show();
    }

    private void imagePickDialog() {
        String [] options = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        //set options
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {// the user pick camera

                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else { // permission already generated
                        pickFromCamera();
                    }
                }
                else if(i == 1) {// the user pick gallery
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {// permission already generated
                        pickFromGallery();
                    }
                }

            }

        });
        builder.create().show();
    }



    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE );
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues () ;
        values.put(MediaStore.Images.Media.TITLE,"Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        // int to op camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_PICK_GALLERY_CODE);

    }

 // check /request for storage (gallery)
    private boolean checkStoragePermission() {
        // check if storage permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private  void requestStoragePermission(){
        // request the storage permission
        ActivityCompat.requestPermissions(this,storagePermission ,STORAGE_REQUEST_CODE);
    }

    //check /request for camera
    private boolean checkCameraPermission() {
        // check if camera permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private  void requestCameraPermission(){
        // request the camera permission
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // allowed/ denied
        switch(requestCode){
            case CAMERA_REQUEST_CODE : {
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean galleryAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && galleryAccepted)
                    {
                        // both permissions allowed
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this, "camera and gallery permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE :{
                if(grantResults.length>0){
                    boolean galleryAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(galleryAccepted){
                        // permission allowed
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this, "gallery permissions is required", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            // image is picked
            if(requestCode == IMAGE_PICK_GALLERY_CODE){

                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK){
                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    profileIv.setImageURI(resultUri);
                }

                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error  = result.getError();
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();

                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}