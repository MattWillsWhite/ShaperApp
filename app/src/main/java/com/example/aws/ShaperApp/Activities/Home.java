package com.example.aws.ShaperApp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aws.ShaperApp.Fragments.BettingFragment;
import com.example.aws.ShaperApp.Fragments.NextFragment;
import com.example.aws.ShaperApp.Fragments.SettingsFragment;
import com.example.aws.ShaperApp.Models.Pitch;
import com.example.aws.ShaperApp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 2 ;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    Dialog popAddPost ;
    ImageView popupUserImage,popupPostImage,popupAddBtn;
    TextView popupTitle,popupDescription,popupAppetite,popupProblem,popupRabbitHole, popupNoGoes, popupSuccess;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set view to activity_home2
        setContentView(R.layout.activity_home2);

        //create instance of toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add welcome messages to sections if no pitches present
        addWelcomeMessage();

        //ini
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //ini popup
        iniPopup();
        setupPopupImageClick();


        //create instace of fab and onclick listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        }); //user wishes to create pitch


        //draw navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //update navheader with user details
        updateNavHeader();


        //set the betting fragment as the default one
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new BettingFragment()).commit();



    }

    private void setupPopupImageClick() {


        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery when image clicked
                //check for permision code copied form register activity
                checkAndRequestForPermission();


            }
        });



    }


    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Home.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            //permission granted to access user gallery
            openGallery();

    }





    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }



    //when user has picked an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostImage.setImageURI(pickedImgUri);

        }


    }






    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //ini popup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAppetite = popAddPost.findViewById(R.id.popup_appetite);
        popupProblem = popAddPost.findViewById(R.id.popup_problem);
        popupRabbitHole = popAddPost.findViewById(R.id.popup_rabbitholes);
        popupNoGoes = popAddPost.findViewById(R.id.popup_nogoes);
        popupSuccess = popAddPost.findViewById(R.id.popup_success);



        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        //load Current user profile photo
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserImage);


        //add post click Listener
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                //I deceided this was the minimum amount of info needed to post a pitch
                if (!popupTitle.getText().toString().isEmpty()
                    && !popupDescription.getText().toString().isEmpty()
                    && pickedImgUri != null ) {

                    // TODO Create Pitch Object and add it to firebase database
                    //upload post Image
                    //access firebase storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pitch_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();
                                    // create pitch Object
                                    Pitch pitch = new Pitch(popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownlaodLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString(),
                                            popupAppetite.getText().toString(),
                                            popupProblem.getText().toString(),
                                            popupRabbitHole.getText().toString(),
                                            popupNoGoes.getText().toString(),
                                            popupSuccess.getText().toString(),
                                            9999999999L, //popularity set to large number that decreases as users react
                                            5L); //status set to 5 meaning it is in the bt section


                                    //add pitch to firebase database
                                    addPitch(pitch);



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //error uploading picture

                                    showMessage(e.getMessage());
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);

                                }
                            });

                        }
                    });



                }
                else {
                    showMessage("Please verify all input fields and choose Pitch Image") ;
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);

                }



            }
        });



    }

    private void addPitch(Pitch pitch) {
        //connect to pitches db
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Pitches").push();

        // get pitch unique ID and upadte pitch key
        String key = myRef.getKey();
        pitch.setPitchKey(key);

        // add pitch data to firebase database
        myRef.setValue(pitch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Pitch Added Successfully");
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }


    private void showMessage(String message) {

        Toast.makeText(Home.this,message,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Nothing happens in this section yet, future versions will include a settings fragment
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Betting Table");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new BettingFragment()).commit();

        } else if (id == R.id.nav_profile) {

            getSupportActionBar().setTitle("Up Next");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new NextFragment()).commit();

        } else if (id == R.id.nav_settings) {

            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();


        }
        else if (id == R.id.nav_signout) {

            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginActivity);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        // now we will use Glide to load user image
        // first we need to import the library

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);




    }

    public void addWelcomeMessage(){

        //define two pitches that explain how to use the betting table and up next sections
        final Pitch btWelcome = new Pitch("Betting Table Instructions",
                "To create a pitch use the back arrow to navigate to the betting table and click the pencil icon.",
                "https://firebasestorage.googleapis.com/v0/b/shaperapp-3a0ef.appspot.com/o/pitch_" +
                        "images%2Fprimary%3ADownload%2FMy%20Post%20(13).png?alt=media&token=8a39db0f-4141-4f5c-ba90-955f85ea74ce",
                "oS13vFfKJwT7gbqucmdhNA4wIst2",
                "https://firebasestorage.googleapis.com/v0/b/shaperapp-3a0ef.appspot.com/o/" +
                        "users_photos%2Fprimary%3ADownload%2Flogo.png?alt=media&token=ddf72df8-343c-4538-bb8e-19a4a825499b",
                "",
                "Welcome to the betting table, here you can view, react and leave comments on pitches created by your team.",
                "Like or Favourite Pitches using the reaction icons.",
                "",
                "",
                1L,
                5L);

        final Pitch unWelcome = new Pitch("Up Next Instructions",
                "Changing a Queued pitch automatically adds a new pitch to the Up Next Section.",
                "https://firebasestorage.googleapis.com/v0/b/shaperapp-3a0ef.appspot.com/o/pitch_" +
                        "images%2Fprimary%3ADownload%2FMy%20Post%20(13).png?alt=media&token=8a39db0f-4141-4f5c-ba90-955f85ea74ce",
                "oS13vFfKJwT7gbqucmdhNA4wIst2",
                "https://firebasestorage.googleapis.com/v0/b/shaperapp-3a0ef.appspot.com/o/" +
                        "users_photos%2Fprimary%3ADownload%2Flogo.png?alt=media&token=ddf72df8-343c-4538-bb8e-19a4a825499b",
                "",
                "Welcome to the Up Next section, here you can view pitches selected for the next development cycle.",
                "Update the status of the pitch by clicking the status text.",
                "",
                "",
                0L,
                3L);

        DatabaseReference pitchesRef = FirebaseDatabase.getInstance().getReference("Pitches");

        //add these welcome pitches if no pitches present in db
        pitchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                } else {
                    addPitch(btWelcome);
                    addPitch(unWelcome);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
