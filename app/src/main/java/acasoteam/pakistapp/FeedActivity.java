package acasoteam.pakistapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

public class FeedActivity extends AppCompatActivity {

    TextView title;
    RatingBar rating;
    private int pakiID;
    private String loginId;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Intent i=getIntent();
        this.loginId= i.getStringExtra("loginId");
        this.name= i.getStringExtra("name");
        this.email= i.getStringExtra("email");
        String name=i.getStringExtra("name");
        float rate=i.getFloatExtra("rate",0);
        this.pakiID = i.getIntExtra("pakiID",-1);

        Log.v("FeedActivity", name);
        Log.v("FeedActivity", String.valueOf(rate));
        Log.v("FeedActivity", String.valueOf(pakiID));

        if(pakiID==-1){

            finish();
        }

        title = (TextView) findViewById(R.id.title);
        title.setText(name);

        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setNumStars(5);
        rating.setRating(rate);





    }

    public void sendFeedback(View view){


        Log.v("SEND FEEDBACK","starting...");

        TextView text = (TextView) findViewById(R.id.fname);
        String feedback = text.getText().toString();
        int rate = (int)rating.getRating();

        Log.v("SEND FEEDBACK: loginId",""+loginId);
        Log.v("SEND FEEDBACK: feed",feedback);
        Log.v("SEND FEEDBACK: rate",rate+"");

        FeedbackDao fd = new FeedbackDao();

        if(fd.sendFeedback(pakiID,feedback,rate,loginId,name,email,this)){
            Log.v("SEND FEEDBACK:","SUCCESS");
        }else{
            Log.v("SEND FEEDBACK:","FAIL");
        }

        finish();


    }



    public void close(View view){
        finish();
    }
}
