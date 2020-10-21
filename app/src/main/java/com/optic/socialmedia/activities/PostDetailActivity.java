package com.optic.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialmedia.R;
import com.optic.socialmedia.adapters.CommentsAdapter;
import com.optic.socialmedia.adapters.SliderAdapter;
import com.optic.socialmedia.models.Comment;
import com.optic.socialmedia.models.FCMBody;
import com.optic.socialmedia.models.FCMResponse;
import com.optic.socialmedia.models.Like;
import com.optic.socialmedia.models.SliderModel;
import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.CommentDatabaseProvider;
import com.optic.socialmedia.providers.LikesDatabaseProvider;
import com.optic.socialmedia.providers.NotificationProvider;
import com.optic.socialmedia.providers.PostDatabaseProvider;
import com.optic.socialmedia.providers.TokenProvider;
import com.optic.socialmedia.providers.UserDatabaseProvider;
import com.optic.socialmedia.utils.MyAppCompactActivity;
import com.optic.socialmedia.utils.RelativeTime;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends MyAppCompactActivity {

    ScrollView barraScroll;
    private SliderView sliderView;
    private SliderAdapter adapter;
    private PostDatabaseProvider mPostProvider;
    String idPost;
    private List<SliderModel> listImagesSlider;
    AuthProviders mAuth;
    CommentDatabaseProvider mCommentProvider;
    private FloatingActionButton btnPutComment;
    private ImageView ivPhotoProfie;
    private Button btnSeeProfile;
    TextView textDescription;
    TextView textTitle;
    TextView textCategory;
    TextView textNameProfilePost;
    TextView textAgeProfilePost;
    TextView countLikes;
    TextView textTimeAgoPost;
    private  EditText textEditTextDialog;
    UserDatabaseProvider mUserProvider;
    private String idUserOwnPostSelected;
    MaterialAlertDialogBuilder dialogPutComment;
    RecyclerView commentsRecyclerView;
    private CommentsAdapter mCommentAdpter;
    private View mView;
    private LikesDatabaseProvider mLikeProvider;
    private Toolbar mToolbar;
    private TokenProvider mTokenProvier;
    private NotificationProvider mNotificationProvider;
    private ListenerRegistration escuchadorLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mTokenProvier= new TokenProvider();
        mNotificationProvider=new NotificationProvider();
        mLikeProvider= new LikesDatabaseProvider();
        mCommentProvider=new CommentDatabaseProvider();
        mAuth=new AuthProviders();
        mUserProvider = new UserDatabaseProvider();
        mPostProvider = new PostDatabaseProvider();
        idPost = getIntent().getStringExtra("idPost");

        mToolbar=findViewById(R.id.toolbarTransparent);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        commentsRecyclerView=findViewById(R.id.commentsRecyclerViewPostDetail);
        LinearLayoutManager linear= new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(linear);


        sliderView = findViewById(R.id.sliderPostDetail);
        btnPutComment = findViewById(R.id.btnCommentPostDetail);
        ivPhotoProfie = findViewById(R.id.ivPhotoProfilePostDetail);
        textDescription = findViewById(R.id.descriptionPostDetail);
        textTitle = findViewById(R.id.titlePostDetail);
        btnSeeProfile = findViewById(R.id.btnSeeProfilePostDetail);
        textCategory = findViewById(R.id.categoryPostDetail);
        textNameProfilePost = findViewById(R.id.nameUserPostDetail);
        textAgeProfilePost = findViewById(R.id.ageUserPostDetail);
        textTimeAgoPost = findViewById(R.id.timeAgoPostDetail);
        countLikes = findViewById(R.id.countLikes);

        listImagesSlider = new ArrayList<>();

        adapter = new SliderAdapter(this, listImagesSlider);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        // sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        // sliderView.setAutoCycle(true);
        //sliderView.startAutoCycle();


        btnSeeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(PostDetailActivity.this,ProfileDetailActivity.class);
                i.putExtra("idUserToSee",idUserOwnPostSelected);
                startActivity(i);
            }
        });
        btnPutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = PostDetailActivity.this.getLayoutInflater();
                mView = inflater.inflate(R.layout.edit_text_dialog, null);
                textEditTextDialog = (EditText)mView.findViewById(R.id.editTextDialog);
                dialogPutComment.setView(mView);
                dialogPutComment.show();
            }
        });



        dialogPutComment=new MaterialAlertDialogBuilder(this)

                .setTitle("Titulo")
                .setMessage("Mensaje")
                .setIcon(R.drawable.ic_chat)

                .setPositiveButton("Comentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createComment();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(PostDetailActivity.this, "No pusiste ningun comentario", Toast.LENGTH_SHORT).show();
                    }
                });



        cargarDatosPost();
        cargarLikesPost();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(escuchadorLikes!=null){
            escuchadorLikes.remove();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    private void cargarLikesPost() {
        escuchadorLikes =mLikeProvider.getLikesOfPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value==null){
                    Toast.makeText(PostDetailActivity.this, "Error al cargar el numero de likes", Toast.LENGTH_SHORT).show();
                }else{
                    countLikes.setText(String.valueOf(value.size())+" likes");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query=mCommentProvider.getAllCommentsByPost(idPost);///comprobar no sea nulo
        if(query!=null){
            FirestoreRecyclerOptions<Comment> options= new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query,Comment.class).build();
            System.out.println("tamañoooooo" +options.getSnapshots().size());
            mCommentAdpter= new CommentsAdapter(this,options);
            commentsRecyclerView.setAdapter(mCommentAdpter);
            mCommentAdpter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCommentAdpter!=null){
            mCommentAdpter.stopListening();
        }
    }
    public void createComment() {

        final Comment mComment=new Comment();
        mComment.setComment(textEditTextDialog.getText().toString());
        mComment.setIdUser(mAuth.getIdCurrentUser());
        mComment.setTimestamp(new Date().getTime());
        mComment.setIdPost(idPost);
        mCommentProvider.save(mComment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PostDetailActivity.this, "COMENTARIO SE REALIZO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                    sendNotification(mComment);
                }else{
                    Toast.makeText(PostDetailActivity.this, "FALLO AL PONER EL COMENTARIO", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendNotification(final Comment mComment) {
        if(idUserOwnPostSelected==null){
            return;
        }
        mTokenProvier.getToken(idUserOwnPostSelected).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    final String tokenUserOwnPost=documentSnapshot.getString("token");
                    if(tokenUserOwnPost!=null){
                        Map<String,String> data= new HashMap<>();
                        data.put("title","Nuevo comentario");
                        data.put("body",mComment.getComment());
                        FCMBody body=new FCMBody(tokenUserOwnPost,"high","4500s",data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body()!=null){
                                    System.out.println(response.body().getSuccess());
                                    if(response.body().getSuccess()==1){
                                        Toast.makeText(PostDetailActivity.this, "La Notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PostDetailActivity.this, "1 Fallo al enviar notificaciones "+response.message()+" - "+call.request().method(), Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(PostDetailActivity.this, " 2 Fallo al enviar notificaciones "+response.message()+" - "+response.errorBody(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }else{
                        Toast.makeText(PostDetailActivity.this, "El token del usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostDetailActivity.this, "Fallo el gete token", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cargarDatosPost() {
        mPostProvider.getPost(idPost).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String category = document.get("category").toString();
                    String description = document.get("description").toString();
                     idUserOwnPostSelected = document.get("idUser").toString();
                    String title = document.get("title").toString();

                    textCategory.setText(category);
                    textDescription.setText(description);
                    textTitle.setText(title);

                    textTimeAgoPost.setText(RelativeTime.getTimeAgo(document.getLong("timestamp")));

                    ///cargamos las imagenes en el slider
                    List<String> group = (List<String>) document.get("images");
                    for (String it : group) {
                        System.out.println("nombre " + it);
                        listImagesSlider.add(new SliderModel(it, new Date().getTime()));
                    }
                    adapter.notifyDataSetChanged();


                    mUserProvider.getUser(idUserOwnPostSelected).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot userPostSelected = task.getResult();
                                String age = userPostSelected.get("edad").toString();
                                String name = userPostSelected.get("nombre").toString();



                                Object urlImage = userPostSelected.get("imageProfile");
                                if (urlImage != null) {
                                    Picasso.with(PostDetailActivity.this).load(urlImage.toString()).into(ivPhotoProfie);

                                }

                                textAgeProfilePost.setText(age);
                                textNameProfilePost.setText(name);
                            } else {
                                Toast.makeText(PostDetailActivity.this, "Error al traer los datos del perfil que creo el posts", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(PostDetailActivity.this, "Error al recuoerar el post seleccionado", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}