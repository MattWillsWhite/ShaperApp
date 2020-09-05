package com.example.aws.ShaperApp.Activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aws.ShaperApp.Adapters.CommentAdapter;
import com.example.aws.ShaperApp.Models.Comment;
import com.example.aws.ShaperApp.Models.Reaction;
import com.example.aws.ShaperApp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PitchDetailActivity extends AppCompatActivity {

    ImageView imgPost, imgUserPost, imgCurrentUser, imgFavouriteNull, imgFavouriteAdd, imgFavouriteRemove, imgLikeNull, imgLikeAdd, imgLikeRemove;
    TextView txtPostDesc, txtPostDateName, txtPostTitle, txtLikes, txtFavourites, txtPostProblem,
            txtPostAppetite, txtPostRabbitHoles, txtPostNoGoes, txtPostSucces, txtPoststatus;
    EditText editTextComment;
    Button btnAddComment;
    String PitchKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment";
    static String REACTION_KEY = "Reaction";
    public String  popPitchID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_detail);


        //set the statue bar to transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        // ini Views
        RvComment = findViewById(R.id.rv_comment);
        imgPost = findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);

        imgFavouriteNull = findViewById(R.id.post_detail_favourite_img_null);
        imgFavouriteAdd = findViewById(R.id.post_detail_favourite_img_add);
        imgFavouriteRemove = findViewById(R.id.post_detail_favourite_img_remove);
        imgLikeNull = findViewById(R.id.post_detail_like_img_null);
        imgLikeAdd = findViewById(R.id.post_detail_like_img_add);
        imgLikeRemove = findViewById(R.id.post_detail_like_img_remove);

        txtPostTitle = findViewById(R.id.post_detail_title);
        txtPostDesc = findViewById(R.id.post_detail_desc);
        txtPostDateName = findViewById(R.id.post_detail_date_name);
        txtLikes = findViewById(R.id.post_detail_like_number);
        txtFavourites = findViewById(R.id.post_detail_favourite_number);
        txtPostProblem = findViewById(R.id.post_detail_problem);
        txtPostAppetite = findViewById(R.id.post_detail_appetite);
        txtPostRabbitHoles = findViewById(R.id.post_detail_rabbitholes);
        txtPostNoGoes = findViewById(R.id.post_detail_nogoes);
        txtPostSucces = findViewById(R.id.post_detail_success);
        txtPoststatus = findViewById(R.id.post_detail_status);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final String uid;
        uid = firebaseUser.getUid();


        // add Comment button click listener
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PitchKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content, uid, uimg, uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment : " + e.getMessage());
                    }
                });


            }
        });


        //pitch details to views
        String postImage = getIntent().getExtras().getString("pitchImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String userpostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userpostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        String postProblem = getIntent().getExtras().getString("problem");
        txtPostProblem.setText(postProblem);

        String postAppetite = getIntent().getExtras().getString("appetite");
        txtPostAppetite.setText(postAppetite);

        String postRabbit = getIntent().getExtras().getString("rabbitholes");
        txtPostRabbitHoles.setText(postRabbit);

        String postNoGo = getIntent().getExtras().getString("nogoes");
        txtPostNoGoes.setText(postNoGo);

        String postSuccess = getIntent().getExtras().getString("success");
        txtPostSucces.setText(postSuccess);

        //setcomment user image
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        // get post id
        PitchKey = getIntent().getExtras().getString("pitchKey");

        String date = timestampToString(getIntent().getExtras().getLong("pitchDate"));
        txtPostDateName.setText(date);

        //ini Recyclerview Comment
        iniRvComment();

        //update reaction view
        reactionController();

        //Pitch status View controller
        statusViewController();





    }


    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        //get comments on pitch
        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PitchKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);

                }

                //display comment
                commentAdapter = new CommentAdapter(getApplicationContext(), listComment);
                RvComment.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }


    private String timestampToString(long time) {
        //convert timestamp to date string
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;


    }

    private void updateReactionDB(final String type, final String func){
        final String uid = firebaseUser.getUid();

        //get root of pitches db
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Pitches").child(PitchKey);
        Query query = rootRef;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //get current popularity
                Long currentPop = (Long) dataSnapshot.child("popularity").getValue();

                //Update Reaction DB
                if (func == "Add") {
                    //Add reaction to DB
                    DatabaseReference reactReference = firebaseDatabase.getReference(REACTION_KEY).child(PitchKey).child(uid).push();
                    final Reaction reaction = new Reaction(type);
                    reactReference.setValue(reaction);

                    //Alter currentPop based on reaction
                    if(type == "Favourite"){ currentPop = currentPop - 2; }
                    else if (type == "Like"){ currentPop = currentPop - 1; }

                    //set popularity
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Pitches").child(PitchKey).child("popularity");
                    myRef.setValue(currentPop);


                } else if (func == "Remove"){
                    //Remove reaction from DB
                    FirebaseDatabase.getInstance().getReference("Reaction").child(PitchKey).child(uid).setValue(null);

                    //Alter currentPop based on reaction
                    if(type == "Favourite"){ currentPop = currentPop + 2; }
                    else if (type == "Like"){ currentPop = currentPop + 1; }

                    //set popularity
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Pitches").child(PitchKey).child("popularity");
                    myRef.setValue(currentPop);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getReactionNumbers(){
        String uid = firebaseUser.getUid();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Reaction").child(PitchKey);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int like = 0;
                int fav =0;

                for(DataSnapshot postSnap: dataSnapshot.getChildren()){

                    for(DataSnapshot reactionSnap: postSnap.getChildren()){

                        String reaction = reactionSnap.child("reaction").getValue().toString();

                        if(reaction.equals("Favourite")){
                            ++fav;
                        } else if  (reaction.equals("Like")){
                            ++like;
                        }
                    }
                }

                //set viewable numbers

                txtLikes.setText(String.valueOf(like));
                txtFavourites.setText(String.valueOf(fav));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getReaction(){
        final String uid = firebaseUser.getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Reaction").child(PitchKey).child(uid);
        Query query = rootRef.orderByChild("reaction");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()){
                    //User has reacted before
                    //get reaction type
                    for(DataSnapshot r : dataSnapshot.getChildren()){
                        String value = r.child("reaction").getValue().toString();

                        //Update Reaction
                        updateReaction(value, "Update");

                    }

                } else {
                    //user has not reacted before
                    //Update Reaction if no reaction found
                    updateReaction("Null", "Update");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void updateReaction(String reactionType, String Func){
        //update reaction and post DBs
        updateReactionDB(reactionType, Func);

        //check function to perform
        if (Func == "Add") {

            //set Images
            if(reactionType == "Favourite"){
                //set inverse image to visable and others to gone
                imgFavouriteRemove.setVisibility(View.VISIBLE);
                imgFavouriteAdd.setVisibility(View.GONE);
                imgFavouriteNull.setVisibility(View.GONE);

                //Set null to visible and others to gone
                imgLikeNull.setVisibility(View.VISIBLE);
                imgLikeAdd.setVisibility(View.GONE);
                imgLikeRemove.setVisibility(View.GONE);


            } else if (reactionType == "Like"){
                //Set remove to visible and others to gone
                imgLikeNull.setVisibility(View.GONE);
                imgLikeAdd.setVisibility(View.GONE);
                imgLikeRemove.setVisibility(View.VISIBLE);

                //set inverse image to visable and others to gone
                imgFavouriteRemove.setVisibility(View.GONE);
                imgFavouriteAdd.setVisibility(View.GONE);
                imgFavouriteNull.setVisibility(View.VISIBLE);

            } else {
                //error
            }

        } else if(Func == "Remove"){

            if(reactionType == "Favourite"){
                //set inverse image to visable and others to gone
                imgFavouriteRemove.setVisibility(View.GONE);
                imgFavouriteAdd.setVisibility(View.VISIBLE);
                imgFavouriteNull.setVisibility(View.GONE);

                //Set add to visible and others to gone
                imgLikeNull.setVisibility(View.GONE);
                imgLikeAdd.setVisibility(View.VISIBLE);
                imgLikeRemove.setVisibility(View.GONE);

            } else if (reactionType == "Like"){
                //Set remove to visible and others to gone
                imgLikeNull.setVisibility(View.GONE);
                imgLikeAdd.setVisibility(View.VISIBLE);
                imgLikeRemove.setVisibility(View.GONE);

                //set inverse image to visable and others to gone
                imgFavouriteRemove.setVisibility(View.GONE);
                imgFavouriteAdd.setVisibility(View.VISIBLE);
                imgFavouriteNull.setVisibility(View.GONE);
            } else {
                //error
            }

        } else {
            //Update images no add or remove
            if(reactionType.equals("Favourite")){
                //set inverse image to visable and others to gone
                imgFavouriteRemove.setVisibility(View.VISIBLE);
                imgFavouriteAdd.setVisibility(View.GONE);
                imgFavouriteNull.setVisibility(View.GONE);

                //Set null to visible and others to gone
                imgLikeNull.setVisibility(View.VISIBLE);
                imgLikeAdd.setVisibility(View.GONE);
                imgLikeRemove.setVisibility(View.GONE);

            } else if (reactionType.equals("Like")){
                //Set remove to visible and others to gone
                imgLikeNull.setVisibility(View.GONE);
                imgLikeAdd.setVisibility(View.GONE);
                imgLikeRemove.setVisibility(View.VISIBLE);

                //set inverse image to visable and others to gone
                imgFavouriteRemove.setVisibility(View.GONE);
                imgFavouriteAdd.setVisibility(View.GONE);
                imgFavouriteNull.setVisibility(View.VISIBLE);

            } else {
                //no user reaction means both add images are displayed
                imgFavouriteRemove.setVisibility(View.GONE);
                imgFavouriteAdd.setVisibility(View.VISIBLE);
                imgFavouriteNull.setVisibility(View.GONE);

                //Set add to visible and others to gone
                imgLikeNull.setVisibility(View.GONE);
                imgLikeAdd.setVisibility(View.VISIBLE);
                imgLikeRemove.setVisibility(View.GONE);
            }

        }

        //Update reaction numbers
        getReactionNumbers();
    }


    private void reactionController(){

         //set all images to gone
        imgFavouriteAdd.setVisibility(View.GONE);
        imgFavouriteRemove.setVisibility(View.GONE);
        imgFavouriteNull.setVisibility(View.GONE);
        imgLikeAdd.setVisibility(View.GONE);
        imgLikeRemove.setVisibility(View.GONE);
        imgLikeNull.setVisibility(View.GONE);

        //Get reaction and update if applicable
        getReaction();

    }

    private void loadStatus(){
        //get root of posts db
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Pitches").child(PitchKey);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //pitch status of post
                final Long status = (Long) dataSnapshot.child("status").getValue();

                //if status is in up next section
                if(status < 5L){

                    String sString = status+"";
                    switch(sString){
                        case "1":
                        sString = "In Progress";
                        txtPoststatus.setTextColor(Color.parseColor("#6354E7"));
                        break;

                        case "2":
                            sString = "Up Next";
                            txtPoststatus.setTextColor(Color.parseColor("#4B0DCE"));
                            break;

                        case "3":
                            sString = "Queued";
                            txtPoststatus.setTextColor(Color.parseColor("#EF407B"));
                            break;

                        case "4":
                            sString = "Completed";
                            txtPoststatus.setTextColor(Color.parseColor("#2DE980"));
                            break;

                        default:
                            sString = "";

                    }
                    //Show status
                    txtPoststatus.setText(sString);


                } else {
                    //Pitch is in betting table stage

                    // make reactions clickable
                    //ADD images on click
                    imgFavouriteAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateReaction("Favourite", "Add");
                        }
                    });

                    imgLikeAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateReaction("Like", "Add");
                        }
                    });

                    //Coloured images on click
                    imgFavouriteRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateReaction("Favourite", "Remove");
                        }
                    });

                    imgLikeRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateReaction("Like", "Remove");
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeStatus(){
        txtPoststatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get root of posts db
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Pitches").child(PitchKey);
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       //find pitch status
                       final Long status = (Long) dataSnapshot.child("status").getValue();

                       Long newStatus = status - 1;
                       String sString = newStatus+"";
                       switch(sString){
                           case "0":
                               sString = "Completed";
                               txtPoststatus.setTextColor(Color.parseColor("#2DE980"));
                               //change status
                               rootRef.child("status").setValue(4L);
                               break;

                           case "1":
                               sString = "In Progress";
                               txtPoststatus.setTextColor(Color.parseColor("#6354E7"));
                               //change status
                               rootRef.child("status").setValue(newStatus);
                               break;
                           case "2":
                               sString = "Up Next";
                               txtPoststatus.setTextColor(Color.parseColor("#4B0DCE"));
                               //change status
                               rootRef.child("status").setValue(newStatus);
                               //take most popular post from betting table and add to up next
                               addPitchToUpNext();
                               break;
                           case "3":
                               sString = "Completed";
                               txtPoststatus.setTextColor(Color.parseColor("#2DE980"));
                               break;

                       }

                       txtPoststatus.setText(sString);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

            }
        });
    }

    private void addPitchToUpNext() {
        //find most popular pitch
        final DatabaseReference pitchRef = FirebaseDatabase.getInstance().getReference("Pitches");

        Query query = pitchRef.orderByChild("status").equalTo(5L);
        query.addValueEventListener(new ValueEventListener() {
            long initialPop = 99999999999L;
            int pitchAddCount = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot pitchSnap: dataSnapshot.getChildren()){

                    long pop = (Long) pitchSnap.child("popularity").getValue();

                    if(pop < initialPop){
                        //if pitch is more popular
                        initialPop = pop;
                        popPitchID = pitchSnap.getKey();
                    }

                }

                //check to see if most popular pitch has been added already
                if(pitchAddCount==0) {
                    //update most pop pitch with status and pop change
                    final DatabaseReference PopPitchRef = FirebaseDatabase.getInstance().getReference("Pitches").child(popPitchID);

                    //update popularity
                    PopPitchRef.child("popularity").setValue(0L);

                    //update status
                    PopPitchRef.child("status").setValue(3L);

                    //increment count so only one pitch added
                    ++pitchAddCount;
                    showMessage("New Pitch Queued");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void statusViewController(){

        //get and display existing status
        loadStatus();

        //change status on click + add new most popular pitch from betting table if applicable
        changeStatus();
    }


}

