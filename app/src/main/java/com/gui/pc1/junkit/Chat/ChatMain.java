package com.gui.pc1.junkit.Chat;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;
import com.gui.pc1.junkit.MainActivity;
import com.gui.pc1.junkit.R;

import java.io.InputStream;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;


public class ChatMain extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    // Android client
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;

    // Java V2
    private SessionsClient sessionsClient;
    private SessionName session;


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        chatLayout = findViewById(R.id.chatLayout);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);

        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            ChatMain.this.sendMessage(sendBtn);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        // Android client
        initChatbot();

        // Java V2
        initV2Chatbot();
        QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText("hello").setLanguageCode("en-US")).build();
        new RequestJavaV2Task(ChatMain.this, session, sessionsClient, queryInput).execute();
    }

    private void initChatbot() {
        final AIConfiguration config = new AIConfiguration("ClientAccessToken",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);// helps to create new session whenever app restarts
        aiRequest = new AIRequest();
    }

    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.junk_it);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(ChatMain.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        }
        else {
            showTextView(msg, USER);
            queryEditText.setText("");
            // Android client
//            aiRequest.setQuery(msg);
//            RequestTask requestTask = new RequestTask(MainActivity.this, aiDataService, customAIServiceContext);
//            requestTask.execute(aiRequest);

            // Java V2
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
            new RequestJavaV2Task(ChatMain.this, session, sessionsClient, queryInput).execute();
        }
    }

  /*  public void callback(AIResponse aiResponse) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            String finalReply = "", pass="";
            char[] cont = botReply.toCharArray();
            Log.d(TAG, "Bot Reply: " + botReply);
            if(botReply=="")
            {
                finalReply = "Sorry I didn't get that. Kindly choose from the choices.";
            }
            else
            {
                for(int x=0;x<botReply.length();x++)
                {
                    if(cont[x]=='>')
                    {
                        finalReply = finalReply + "\n";

                        *//*if answer is 3
                        int pesosPerKilo = 5
                        total=pesosPerKilo*answer; --------based sa junk*//*

                    }
                    else
                    {
                        pass = String.valueOf(cont[x]);
                        finalReply = finalReply + pass;

                    }
                }
            }
            showTextView(finalReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }
*/
    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            String finalReply = "",pass="";
            String idcalc = "";
            String idchar;
            String calcnum="";
            double result;
            char[] cont = botReply.toCharArray();
            Log.d(TAG, "V2 Bot Reply: " + botReply);
            if(botReply=="")
            {
                finalReply = "Sorry I didn't get that. Kindly enter a valid code from the list.";
            }
            else
            {
                if(botReply.length()<8)
                {
                    for(int x=0;x<botReply.length();x++)
                    {
                        idchar = String.valueOf(cont[x]);
                        if(x>2)
                        {
                            calcnum = calcnum + idchar;
                        }
                        else
                        {
                            idcalc = idcalc + idchar;
                        }
                    }
                    idcalc.trim();
                    calcnum.trim();
                    Log.d(TAG, "ID: " + idcalc);
                    int num = Integer.parseInt(calcnum);
                    Log.d(TAG, "Result: " + num);
                    switch (idcalc)
                    {
                        case "cbs":
                        {
                            result = num*100.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "wpp":
                        {
                            result = num*8.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "ode":
                        {
                            result = num*8.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "cap":
                        {
                            result = num*2.50;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "olp":
                        {
                            result = num*4.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "asp":
                        {
                            result = num*1.50;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "eng":
                        {
                            result = num*1.50;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "ccp":
                        {
                            result = num*16.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "usp":
                        {
                            result = num*12.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "fbp":
                        {
                            result = num*10.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "cop":
                        {
                            result = num*10.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "imp":
                        {
                            result = num*5.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "zpp":
                        {
                            result = num*0.20;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "acm":
                        {
                            result = num*50.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "cac":
                        {
                            result = num*300.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "cbc":
                        {
                            result = num*250.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "ccc":
                        {
                            result = num*150.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "irs":
                        {
                            result = num*9.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "sts":
                        {
                            result = num*60.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "gis":
                        {
                            result = num*7.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "tcm":
                        {
                            result = num*3.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "elg":
                        {
                            result = num*0.75;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "gig":
                        {
                            result = num*0.65;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "keg":
                        {
                            result = num*0.25;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "sog":
                        {
                            result = num*2.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "glg":
                        {
                            result = num*1.00;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                        case "ije":
                        {
                            result = num*100.00;
                            double result2 = num*300;
                            finalReply = "Your income in selling your scrap will be P"+Double.toString(result)+" to "+Double.toString(result2)+"\n\nDo you want to ask more? (yes/no)";
                            break;
                        }
                    }
                    Log.d(TAG, "Final: " + finalReply);
                }
                else
                {
                    for(int x=0;x<botReply.length();x++)
                    {
                        if(cont[x]=='>')
                        {
                            finalReply = finalReply + "\n";
                        }
                        else
                        {
                            pass = String.valueOf(cont[x]);
                            finalReply = finalReply + pass;
                        }
                    }
                }
            }
            showTextView(finalReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }

    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatMain.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatMain.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }
}