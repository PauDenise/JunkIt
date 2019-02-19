package com.gui.pc1.junkit;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {


    EditText userFeedback;
    Button send, details;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    TextView usernameView;
    ImageView imageView;
    Button logout;
    final FirebaseUser user = firebaseAuth.getCurrentUser();


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userFeedback = view.findViewById(R.id.userFeedback);
        send = view.findViewById(R.id.btnSend);
        details = view.findViewById(R.id.btnDetails);
        Firebase.setAndroidContext(this.getActivity());

        usernameView = view.findViewById(R.id.usernameView);
        imageView = view.findViewById(R.id.imageView);
        logout = view.findViewById(R.id.btnLogout);


        DatabaseReference databaseFeedback = FirebaseDatabase.getInstance().getReference();
        loadUserInformation();


        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {

                final String username = user.getDisplayName();
                final String feedback = userFeedback.getText().toString();



                if(feedback.isEmpty()){
                    userFeedback.setError("Please Enter your Feedback");
                    send.setEnabled(false);
                }

                else{
                    String id = databaseFeedback.push().getKey();
                    databaseFeedback.child("Feedbacks").child(username).child(id).child("UserFeedback").setValue(feedback);
                    details.setEnabled(true);
                    userFeedback.setError(null);
                    send.setEnabled(true);

                    Toast.makeText(getActivity(), "Feedback Sent!", Toast.LENGTH_SHORT).show();

                }


                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        ad.setTitle("Feedback Details:");
                        ad.setMessage("Username - " + username + " \n\nMessage - " + feedback );
                        ad.show();
                    }
                });

            }

        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });


        return view;
    }

    private void loadUserInformation() {

        if (user != null) {

            if (user.getPhotoUrl() != null) {
                Log.d("forphoto", "loadUserInformation: photourl: "+ user.getPhotoUrl().toString());
                RequestOptions options = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_round);
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
            if (user.getDisplayName() != null) {
                usernameView.setText("Hello, "+user.getDisplayName());
            }

        }
    }

}
