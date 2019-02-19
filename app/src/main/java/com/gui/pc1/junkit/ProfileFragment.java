package com.gui.pc1.junkit;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;

public class ProfileFragment extends Fragment {


    EditText enterUsername, userFeedback;
    Button send, details;
    Firebase firebase;


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        enterUsername = (EditText) view.findViewById(R.id.enterUsername);
        userFeedback = (EditText) view.findViewById(R.id.userFeedback);
        send =(Button) view.findViewById(R.id.btnSend);
        details= (Button) view.findViewById(R.id.btnDetails);
        Firebase.setAndroidContext(this.getActivity());

        String UniqueID =
        Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
         Settings.Secure.ANDROID_ID);

        firebase = new Firebase("https://junkitapp.firebaseio.com");

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                details.setEnabled(true);
                final String username = enterUsername.getText().toString();
                final String feedback = userFeedback.getText().toString();


                 Firebase child_name = firebase.child("Username");
                child_name.setValue(username);
                if(username.isEmpty()){
                    enterUsername.setError("Please Enter your Username");
                    send.setEnabled(false);
                }

                else{
                    enterUsername.setError(null);
                    send.setEnabled(true);
                }

                Firebase child_message = firebase.child("Feedback");
                child_message.setValue(feedback);
                if(feedback.isEmpty()){
                    userFeedback.setError("Please Enter your Feedback");
                    send.setEnabled(false);
                }

                else{
                    userFeedback.setError(null);
                    send.setEnabled(true);
                }

                Toast.makeText(getActivity(), "Feedback Sent!", Toast.LENGTH_SHORT).show();

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


        return view;
    }
}