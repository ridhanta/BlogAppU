package com.education.sirkel.blogappu.Data;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.education.sirkel.blogappu.Model.Blog;
import com.education.sirkel.blogappu.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 1/1/2018.
 */

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(BlogRecyclerAdapter.ViewHolder holder, int position) {

        Blog blog = blogList.get(position);
        String imageUrl = null;

        holder.titleBlog.setText(blog.getTitle());
        holder.descBlog.setText(blog.getDesc());


        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();

        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());
        holder.timeStampBlog.setText(formattedDate);

        imageUrl = blog.getImage();

        //TODO : Use Picasso to load image
        Picasso.with(context)
                .load(imageUrl)
                .into(holder.imageBlog);


    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleBlog;
        public TextView descBlog;
        public TextView timeStampBlog;
        public ImageView imageBlog;
        String userId;


        public ViewHolder(View itemView, Context ctx) {
            super(itemView);

            ctx = context;

            titleBlog = (TextView) itemView.findViewById(R.id.postTitleList);
            descBlog = (TextView)itemView.findViewById(R.id.postTextList);
            timeStampBlog = (TextView) itemView.findViewById(R.id.timeStampList);
            imageBlog = (ImageView) itemView.findViewById(R.id.postImageList);

            userId = null;


        }
    }
}
