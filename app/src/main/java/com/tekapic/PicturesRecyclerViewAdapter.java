package com.tekapic;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Album;
import com.tekapic.model.Picture;

import java.util.ArrayList;

/**
 * Created by LEV on 23/08/2018.
 */
public class PicturesRecyclerViewAdapter extends RecyclerView.Adapter<PicturesRecyclerViewAdapter.MyViewHolder> {

    private int mNumberOfItems;
    private Context context;
    private ListItemClickListener mOnClickListener;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private ArrayList<Integer> positions=new ArrayList<Integer>() ;




    public PicturesRecyclerViewAdapter(ArrayList<Picture> picturesList, ListItemClickListener v1, Context v2) {

        this.picturesList=picturesList;
        this.mOnClickListener =  v1;
        this.context= v2;
    }



    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, Picture picture);
    }

    /*  public MyRecyclerViewAdapter(int numberOfItems, ListItemClickListener mOnClickListener){
          this.mNumberOfItems=numberOfItems;
          this.mOnClickListener = mOnClickListener;
      }*/
//    public AlbumsRecyclerViewAdapter(ArrayList<Album> list, Context mOnClickListener, Context context){
//        this.albumsList=list;
//        this.mOnClickListener =  mOnClickListener;
//        this.context=  context;
//
//    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      /*  Context context= parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.list_item;
        boolean attachToRoot =false;
        View view = layoutInflater.inflate(layoutForListItem,parent,attachToRoot);
        MyViewHolder myViewHolder= new MyViewHolder(view);

        */

        // View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pictures_row, parent, false);
        MyViewHolder myViewHolder= new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.versionImage.setImageResource(albumsList.get(position).getPicture());
//        Log.i("xxx", "blat");

        Glide.with(context)
                .load(picturesList.get(position).getPictureUrl())
                .apply(new RequestOptions().placeholder(R.drawable.b))
                .into(holder.imageView);

//        Glide
//                .with( context )
//                .load(picturesList.get(position).getPictureUrl())
//                .thumbnail( 0.1f )
//                .apply(new RequestOptions().placeholder(R.drawable.b))
//                .into(holder.imageView);

//        positions.add(position);


    }

    @Override
    public int getItemCount() {
        return picturesList.size(); //mNumberOfItems;
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }




    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView= itemView.findViewById(R.id.rowImageView);
            itemView.setOnClickListener(this);

        }
        void bind (int listIndex){
            //ItemNumberTv.setText(String.valueOf(listIndex));
            //itemNumberTextTv.setText("ViewHolder index :"+String.valueOf(listIndex));
//            imageView.setImageResource(pictureUrlList.get(listIndex).getPicture());
        }


        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            int x = 0;

            for(Picture picture : picturesList) {
                if(x++ == clickedPosition) {
                    mOnClickListener.onListItemClick(clickedPosition, picture);
                    break;
                }
            }


//            Log.i("getAdapterPosition()", Integer.toString(getAdapterPosition()));



//            int i = 0;
//            int j = 0;
//
//
//            for(int p: positions) {
//                if(p == clickedPosition) {
//                    break;
//                }
//                i++;
//            }
//
//            for(Picture picture: picturesList) {
//                if(j == i) {
//                    mOnClickListener.onListItemClick(clickedPosition, picture);
//                    break;
//                }
//                j++;
//            }


        }
    }

}
