package com.example.silvia.farmacia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FBeginAuth extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView photoImagenView;

    private TextView nameTextView;

    private TextView emailView;

    private TextView idTexView;

    private Button btn_salir;
    private Button btn_relogear;
    private Button btn_ingresar;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbegin_auth);

        photoImagenView= findViewById(R.id.photoImageView);
        nameTextView= findViewById(R.id.nameTextView);
        emailView=findViewById(R.id.emailTextView);
        idTexView=findViewById(R.id.idTextView);
        btn_relogear=findViewById(R.id.btnRelogear);
        btn_salir= findViewById(R.id.btnSalir);
        btn_ingresar=findViewById(R.id.btnIngresar);


        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();

                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                        if(status.isSuccess())
                        {
                            goLogInScreen();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "no se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


        btn_relogear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();

                Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                        if(status.isSuccess())
                        {
                            goLogInScreen();
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "no se pudo revocar el acceso", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);

            }
        });


        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleApiClient= new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this )
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user= firebaseAuth.getCurrentUser();

                if( user !=null)
                {
                    setUserData(user);

                }else
                {
                    goLogInScreen();
                }

            }
        };



    }


    private void goLogInScreen() {

        Intent intent= new Intent(this,firebaseAuth.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setUserData(FirebaseUser user) {

        nameTextView.setText(user.getDisplayName());
        emailView.setText(user.getEmail());
        //idTexView.setText(user.getUid());

        Glide.with(this).load(user.getPhotoUrl()).into(photoImagenView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opr.isDone())
        {
            GoogleSignInResult result= opr.get();
            handleSingInResult(result); //metodo


        }else
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSingInResult(googleSignInResult);
                }
            });*/

        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(firebaseAuthListener != null)
        {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}
