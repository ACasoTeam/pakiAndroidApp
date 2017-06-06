package acasoteam.pakistapp.Adapter;

import android.animation.ValueAnimator;
import android.content.Context;
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


import java.util.List;

import acasoteam.pakistapp.R;
import acasoteam.pakistapp.Utility.SingleComment;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.SingleCommentViewHolder>  {



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

        }

    }

    public List<SingleComment> singlecomments;
    int dimStart = -1;
    int targetHeight;
    private int pakiID;
    private int userID;

    Context context;
    public CommentAdapter(List<SingleComment> singlecomments, Context context){

        this.singlecomments = singlecomments;
        this.context = context;
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
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(final SingleCommentViewHolder singleCommentViewHolder, int i) {
        Log.v("CommentAdapter","onBindViewHolder");
        Log.v("CommentAdapter","i:"+i);
        final int itemType = getItemViewType(i);


        if (i != 0){
            singleCommentViewHolder.name.setText(singlecomments.get(i).getName());
            singleCommentViewHolder.rb.setRating(singlecomments.get(i).getRate());
            singleCommentViewHolder.comment.setText(singlecomments.get(i).getComment());
        }else{
            /*
            singleCommentViewHolder.rb2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // TODO perform your action here

                    }
                    return true;
                }

            });*/

        }
    }

    @Override
    public int getItemCount() {
        return singlecomments.size();
    }





}
