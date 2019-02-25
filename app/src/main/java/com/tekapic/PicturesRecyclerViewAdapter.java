package com.tekapic;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tekapic.model.Picture;
import java.util.ArrayList;

/**
 * Created by LEV on 23/08/2018.
 */
public class PicturesRecyclerViewAdapter extends RecyclerView.Adapter<PicturesRecyclerViewAdapter.MyViewHolder>    {

    private Context context;
    private ListItemClickListener mOnClickListener;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;



    public PicturesRecyclerViewAdapter(ArrayList<Picture> picturesList, ListItemClickListener v1, Context v2) {
        this.picturesList=picturesList;
        this.mOnClickListener =  v1;
        this.context= v2;
    }


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pictures_row, parent, false);
        MyViewHolder myViewHolder= new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Glide.with(context)
                .load(picturesList.get(position).getPictureUrl())
                .apply(new RequestOptions().placeholder(R.drawable.b))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return picturesList.size(); //mNumberOfItems;
    }


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView= itemView.findViewById(R.id.rowImageView);

            itemView.setOnTouchListener(this);

        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(event.getAction() == MotionEvent.ACTION_CANCEL) {
                imageView.setColorFilter(ContextCompat.getColor(context, R.color.noColor));
            }


                if(event.getAction() == MotionEvent.ACTION_UP) {

                    imageView.setColorFilter(ContextCompat.getColor(context, R.color.noColor));

                    int clickedPosition = getAdapterPosition();
                    int x = 0;

                for(Picture picture : picturesList) {
                    if(x++ == clickedPosition) {


                        mOnClickListener.onListItemClick(clickedPosition, picture, getItemCount(), picturesList);
                        break;
                    }
                }

            }
            if(event.getAction() == MotionEvent.ACTION_DOWN) {

                imageView.setColorFilter(ContextCompat.getColor(context, R.color.colorTintt));


            }

            return true;
        }
    }
}
