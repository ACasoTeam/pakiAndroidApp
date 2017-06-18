package acasoteam.pakistapp.Adapter;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import acasoteam.pakistapp.FeedActivity;
import acasoteam.pakistapp.MainActivity;
import acasoteam.pakistapp.MapsActivity;
import acasoteam.pakistapp.R;
import acasoteam.pakistapp.Utility.SingleComment;

import static com.facebook.FacebookSdk.getApplicationContext;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.SingleCommentViewHolder>{



    public static class SingleCommentViewHolder extends RecyclerView.ViewHolder   {
        TextView name;
        RatingBar rb;
        RatingBar rb2; // rating bar per inserire il voto
        TextView comment;


        SingleCommentViewHolder(View itemView) {
            super(itemView);
            Log.v("CommentAdapter","SingleCommentViewHolder");
            name = (TextView)itemView.findViewById(R.id.username);
            rb = (RatingBar)itemView.findViewById(R.id.userRate);
            comment = (TextView) itemView.findViewById(R.id.comment);
            rb2 = (RatingBar)itemView.findViewById(R.id.ratingBar2);
            //  pakiname = (TextView)itemView.findViewById(R.id.address);

        }

    }

    public List<SingleComment> singlecomments;
    int dimStart = -1;
    int targetHeight;
    private int pakiID = -1;
    String loginId = null;
    String name = null;
    String email = null;
    AccessToken accessToken;
    static CallbackManager callbackManager;

    public static void onActivityResultCM(int requestCode, int resultCode, Intent data){
        if(callbackManager != null){
            callbackManager.onActivityResult(requestCode, resultCode,data);
            Log.v("CommentAdapter","onActivityResult:TRUE");
        }else{
            Log.v("CommentAdapter","onActivityResult:FALSE");
        }
    }

    private boolean showSpin;
    Context context;
    public CommentAdapter(List<SingleComment> singlecomments, boolean showSpin, Context context,int paki){

        this.singlecomments = singlecomments;
        this.context = context;
        this.pakiID = paki;
        this.showSpin = showSpin;
        Log.v("CommentAdapter","CommentAdapter");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.v("CommentAdapter","onAttachedToRecyclerView");
    }

    @Override
    public SingleCommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.v("CommentAdapter","onCreateViewHolder");
        Log.v("CommentAdapter","i:"+i);

        SingleCommentViewHolder pvh;
        if (i == 0){



            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_rating_comment, viewGroup, false);
            Log.v("RATINGBARR",v.toString());
            LinearLayout l = (LinearLayout) v;
            Log.v("RATINGBARR - viewid",l.getId()+" "+ l.getChildCount());
            for(int ii=0; ii< l.getChildCount();ii++){
                View child = l.getChildAt(ii);
                Log.v("RATINGBARR - childid",child.getId()+"");
            }
            pvh = new SingleCommentViewHolder(v);
        } else if (i == 2) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinbar_comment_row, viewGroup, false);
            pvh = new SingleCommentViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_comment_row, viewGroup, false);
            pvh = new SingleCommentViewHolder(v);
        }

        return pvh;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1 && showSpin) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(final SingleCommentViewHolder singleCommentViewHolder, int i) {
        Log.v("CommentAdapter","onBindViewHolder");
        Log.v("CommentAdapter","i:"+i);
        Log.v("CommentAdapter","showSpin:"+showSpin);
        final int itemType = getItemViewType(i);


        if (!(i == 0 || (i == 1 && showSpin))){
            singleCommentViewHolder.name.setText(singlecomments.get(i).getName());
            singleCommentViewHolder.rb.setRating(singlecomments.get(i).getRate());
            singleCommentViewHolder.comment.setText(singlecomments.get(i).getComment());
        } else if (i==0) {
            singleCommentViewHolder.rb2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        FacebookSdk.sdkInitialize(getApplicationContext());

                        callbackManager = CallbackManager.Factory.create();
                        accessToken = AccessToken.getCurrentAccessToken();

                        // TODO perform your action here
                        RatingBar ratingBar = (RatingBar) v;


                        //evicenzia le stelle
                        float touchPositionX = event.getX();
                        float width = ratingBar.getWidth();
                        float starsf = (touchPositionX / width) * 5.0f;
                        int stars = (int)starsf + 1;
                        ratingBar.setRating(stars);
                        v.setPressed(false);

                        Activity activity = (Activity) context;

                        accessToken= ((MapsActivity) activity).getAccessToken();
                        Log.v("CommentAdapter", "accesstoken:" + accessToken);
                        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));

                        // Callback registration

                        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.v("CommentAdapter", "onSuccess");
                                accessToken = loginResult.getAccessToken();

                                Log.v("CommentAdapter", "accesstoken2:" + accessToken);

                                if (accessToken != null) {

                                    GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                            loginId = user.optString("id");
                                            name = user.optString("name");
                                            email = user.optString("email");

                                            if (loginId != null) {
                                                Log.v("ComponentAdapter", "loginId != null, ed è:" + loginId);

                                                //ReportDao reportdao = new ReportDao();

                                                //todo: cambiare ste assegnazioni random
                                                // LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                                        Manifest.permission.ACCESS_FINE_LOCATION)
                                                        == PackageManager.PERMISSION_GRANTED) {
                                                    Log.v("CommentAdapter", "loginId == OKOK");
                                                    Log.v("CommentAdapter", loginId);

                                                    Activity a = (Activity) context;
                                                    Log.v("CommentAdapter", ((MapsActivity) a).getAddress().getText()+"");
                                                    Intent i=new Intent(a,FeedActivity.class);
                                                    i.putExtra("pakiID",pakiID);
                                                    i.putExtra("rate",singleCommentViewHolder.rb2.getRating());
                                                    i.putExtra("name",((MapsActivity) a).getAddress().getText()+"");
                                                    i.putExtra("userID",loginId);
                                                    a.startActivity(i);


                                                }
                                            } else {
                                                Log.v("CommentAdapter", "loginId == 0");

                                            }
                                        }
                                    }).executeAsync();
                                }

                            }

                            @Override
                            public void onCancel() {
                                Log.v("MapsActivity", "onCancel");
                                loginId = null;

                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Log.v("CommentAdapter", "onError");
                                Log.e("CommentAdapter", "ERROR: " + exception.getMessage());
                                loginId = null;
                                Activity activity = (Activity) context;
                                if (exception instanceof FacebookAuthorizationException) {
                                    if (AccessToken.getCurrentAccessToken() != null) {
                                        LoginManager.getInstance().logOut();
                                        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
                                    }
                                }


                            }
                        });




                        Log.v("RATINGBUTTON","Funziona");
                        Log.v("RATINGBUTTON",ratingBar.getRating()+"");

                    }
                    return true;
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return singlecomments.size();
    }





}
