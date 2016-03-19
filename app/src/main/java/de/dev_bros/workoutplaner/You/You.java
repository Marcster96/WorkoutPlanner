package de.dev_bros.workoutplaner.You;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.dev_bros.workoutplaner.R;
import de.dev_bros.workoutplaner.Tools.UtilityBox;

/**
 * Created by Marc on 05.03.2016.
 */
public class You extends AppCompatActivity implements View.OnClickListener {

    private Bitmap currPic;
    private ImageView imgYou;
    private RelativeLayout relativeLayout_you;
    private LinearLayout content_layout_you;
    private Toolbar toolbar;


    private static final int REQUEST_CAMERA = 123;
    private static final int SELECT_FILE = 124;
    private String takeAPhotoLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgYou = (ImageView) findViewById(R.id.imgSuccess_YouMain);
        imgYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        setupRemoveTastaure();

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.relativ_YouMain || v.getId() == R.id.verticalLay_YouMain){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

        for(int i = 0; i < content_layout_you.getChildCount();i++){
            LinearLayout linearLayout = (LinearLayout) content_layout_you.getChildAt(i);
            if(linearLayout.getId() == v.getId()){
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        }
    }


    private void setupRemoveTastaure() {
        relativeLayout_you = (RelativeLayout) findViewById(R.id.relativ_YouMain);
        relativeLayout_you.setOnClickListener(this);
        content_layout_you = (LinearLayout) findViewById(R.id.verticalLay_YouMain);
        content_layout_you.setOnClickListener(this);
        for(int i = 0; i < content_layout_you.getChildCount();i++){
            LinearLayout linearLayout = (LinearLayout) content_layout_you.getChildAt(i);
            linearLayout.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_you_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.check_YouMain){

            EditText tbxArm = (EditText) findViewById(R.id.tbxArmCircumference_YouMain);
            EditText tbxLeg = (EditText) findViewById(R.id.tbxLegCircumference_YouMain);
            EditText tbxBreast = (EditText) findViewById(R.id.tbxBreastCircumference_YouMain);

            if(UtilityBox.isEmty(tbxArm.getText().toString()) || UtilityBox.isEmty(tbxLeg.getText().toString()) || UtilityBox.isEmty(tbxBreast.getText().toString()) || currPic == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.check_title_YouMain))
                        .setMessage(getString(R.string.check_message_YouMain))
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveAllValuesTooParse();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = builder.create();

                // show it
                alertDialog.show();
            }else{
                saveAllValuesTooParse();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAllValuesTooParse(){
        final EditText tbxArm = (EditText) findViewById(R.id.tbxArmCircumference_YouMain);
        final EditText tbxLeg = (EditText) findViewById(R.id.tbxLegCircumference_YouMain);
        final EditText tbxBreast = (EditText) findViewById(R.id.tbxBreastCircumference_YouMain);

        ParseObject personalSuccess = new ParseObject("Success");
        personalSuccess.put("Arm",tbxArm.getText().toString());
        personalSuccess.put("Breast",tbxBreast.getText().toString());
        personalSuccess.put("Leg", tbxLeg.getText().toString());
        personalSuccess.put("User", ParseUser.getCurrentUser().getUsername());

        if(currPic != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            currPic.compress(Bitmap.CompressFormat.JPEG, 80, stream);

            byte[] bytes = stream.toByteArray();

            ParseFile file = new ParseFile("image.jpg", bytes);
            personalSuccess.put("Picture",file);
        }


        personalSuccess.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    You.this.finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = { getString(R.string.take_photo_YouMain), getString(R.string.select_photo_YouMain), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(You.this);
        builder.setTitle(getString(R.string.add_photo_YouMain));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo_YouMain))) {
                    takeAPhoto();
                } else if (items[item].equals(getString(R.string.select_photo_YouMain))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                reduceImageSize(takeAPhotoLocation);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                imgYou.setImageBitmap(bm);
                currPic = bm;
            }

            imgYou.setPadding(0,0,0,0);
        }
    }

    private void reduceImageSize(String imagePath){
        int targetImageViewWidth = 480;
        int targetImageViewHeight = 640;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outWidth;

        int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoWithReducedSize = BitmapFactory.decodeFile(imagePath,bmOptions);
        photoWithReducedSize = Bitmap.createScaledBitmap(photoWithReducedSize,720,1280,true);
        currPic = photoWithReducedSize;
        imgYou.setImageBitmap(currPic);

    }

    private void takeAPhoto() {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;


        try{
            photoFile = createImageFile();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(callCameraApplicationIntent, REQUEST_CAMERA);
    }

    private File createImageFile() throws IOException {
        //Um einen einzigartigen Namen zu generieren
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Image_" + timeStamp;

        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageFileName,".jpg",storageDirectory);
        takeAPhotoLocation = imageFile.getAbsolutePath();

        return imageFile;
    }
}
