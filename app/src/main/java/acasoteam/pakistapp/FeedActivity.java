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

    private int oid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Intent i=getIntent();
        int paki= i.getIntExtra("oid",-1);
        String name=i.getStringExtra("name");
        float rate=i.getFloatExtra("rate",0);

        Log.v("LOG INTENT: oid", String.valueOf(oid));
        Log.v("LOG INTENT: name", name);
        Log.v("LOG INTENT: star", String.valueOf(rate));

        if(paki==-1){

            finish();
        }

        this.oid=paki;

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
        float rate = rating.getRating();

        Log.v("SEND FEEDBACK: oid",""+oid);
        Log.v("SEND FEEDBACK: feed",feedback);
        Log.v("SEND FEEDBACK: rate",rate+"");

        FeedbackDao fd = new FeedbackDao();
        int idUser =19;

        if(fd.sendFeedback(oid,feedback,rate,idUser,this)){
            Log.v("SEND FEEDBACK:","SUCCESS");
        }else{
            Log.v("SEND FEEDBACK:","FAIL");
        }


    }



    public void close(View view){
        finish();
    }
}
