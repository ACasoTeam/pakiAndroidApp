package acasoteam.pakistapp.Adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
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
        TextView comment;

        SingleCommentViewHolder(View itemView) {
            super(itemView);
            Log.v("CommentAdapter","SingleCommentViewHolder");
            name = (TextView)itemView.findViewById(R.id.username);
            rb = (RatingBar)itemView.findViewById(R.id.userRate);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }

    }

    public List<SingleComment> singlecomments;
    int dimStart = -1;
    int targetHeight;

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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_comment_row, viewGroup, false);
        SingleCommentViewHolder pvh = new SingleCommentViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(final SingleCommentViewHolder singleCommentViewHolder, int i) {
        Log.v("CommentAdapter","onBindViewHolder");
        singleCommentViewHolder.name.setText(singlecomments.get(i).getName());
        singleCommentViewHolder.rb.setRating(singlecomments.get(i).getRate());
        singleCommentViewHolder.comment.setText(singlecomments.get(i).getComment());

    }

    @Override
    public int getItemCount() {
        return singlecomments.size();
    }




}
