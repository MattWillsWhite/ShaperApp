package com.example.aws.ShaperApp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aws.ShaperApp.Activities.PitchDetailActivity;
import com.example.aws.ShaperApp.Models.Pitch;
import com.example.aws.ShaperApp.R;


import java.util.List;

public class PitchAdapter extends RecyclerView.Adapter<PitchAdapter.MyViewHolder> {

    Context mContext;
    List<Pitch> mData ;


    public PitchAdapter(Context mContext, List<Pitch> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Get status and convert to readable string
        String status = mData.get(position).getStatus()+"";

        //if in up next section show status
        switch(status) {
            case "1":
                status = "In Progress";
                holder.txtStatus.setTextColor(Color.parseColor("#6354E7"));
                break;

            case "2":
                status = "Up Next";
                holder.txtStatus.setTextColor(Color.parseColor("#4B0DCE"));
                break;

            case "3":
                status = "Queued";
                holder.txtStatus.setTextColor(Color.parseColor("#EF407B"));
                break;

            case "4":
                status = "Completed";
                holder.txtStatus.setTextColor(Color.parseColor("#2DE980"));
                break;

                default:
                    //pitch in betting table
                    status = "";

        }

        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.txtStatus.setText(status);
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, txtStatus;
        ImageView imgPost;
        ImageView imgPostProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            txtStatus = itemView.findViewById(R.id.txt_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //start new intent view pitch in more detail, pass pitch values from getters
                    Intent pitchDetailActivity = new Intent(mContext, PitchDetailActivity.class);
                    int position = getAdapterPosition();

                    //get methods for pitch data
                    pitchDetailActivity.putExtra("title",mData.get(position).getTitle());
                    pitchDetailActivity.putExtra("pitchImage",mData.get(position).getPicture());
                    pitchDetailActivity.putExtra("description",mData.get(position).getDescription());
                    pitchDetailActivity.putExtra("pitchKey",mData.get(position).getPitchKey());
                    pitchDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
                    pitchDetailActivity.putExtra("popularity",mData.get(position).getPopularity());
                    pitchDetailActivity.putExtra("problem",mData.get(position).getProblem());
                    pitchDetailActivity.putExtra("appetite",mData.get(position).getAppetite());
                    pitchDetailActivity.putExtra("rabbitholes",mData.get(position).getRabbitHole());
                    pitchDetailActivity.putExtra("nogoes",mData.get(position).getNoGoes());
                    pitchDetailActivity.putExtra("success",mData.get(position).getSuccess());

                    long timestamp  = (long) mData.get(position).getTimeStamp();
                    pitchDetailActivity.putExtra("pitchDate",timestamp) ;

                    //start activity and pass current pitch vars
                    mContext.startActivity(pitchDetailActivity);


                }
            });

        }


    }
}
