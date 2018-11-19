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
import com.google.android.gms.flags.impl.DataUtils;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Album;

import java.util.ArrayList;

/**
 * Created by LEV on 21/08/2018.
 */
public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.MyViewHolder> {

    int mNumberOfItems;
    Context context;
    private ArrayList<Album> albumsList=new ArrayList<Album>() ;
    private ListItemClickListener mOnClickListener;

    private ArrayList<String> albums=new ArrayList<String>() ;
    private ArrayList<Integer> positions=new ArrayList<Integer>() ;




    public AlbumsRecyclerViewAdapter(ArrayList<Album> albumsList, ListItemClickListener v1, Context v2) {

        this.albumsList=albumsList;
        this.mOnClickListener =  v1;
        this.context= v2;
    }


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, String album);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums_row, parent, false);
        MyViewHolder myViewHolder= new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.versionImage.setImageResource(albumsList.get(position).getPicture());
//        Log.i("xxx", "blat");

        String cap = albumsList.get(position).getName().substring(0, 1).toUpperCase() + albumsList.get(position).getName().substring(1);
        holder.albumName.setText(cap);


//        holder.albumName.setText(albumsList.get(position).getName());


        Glide.with(context)
                .load(albumsList.get(position).getPicture())
                .apply(new RequestOptions().placeholder(R.drawable.b))
                .into(holder.versionImage);



        albums.add(albumsList.get(position).getName());
        positions.add(position);


    }

    @Override
    public int getItemCount() {
        return albumsList.size(); //mNumberOfItems;
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        ImageView versionImage;
        TextView albumName;

        public MyViewHolder(View itemView) {
            super(itemView);

            versionImage= itemView.findViewById(R.id.rowAlbum);
            albumName = itemView.findViewById(R.id.textViewAlbum);
            itemView.setOnClickListener(this);

        }
        void bind (int listIndex){
            //ItemNumberTv.setText(String.valueOf(listIndex));
            //itemNumberTextTv.setText("ViewHolder index :"+String.valueOf(listIndex));
            versionImage.setImageResource(albumsList.get(listIndex).getPicture());
            albumName.setText(albumsList.get(listIndex).getName());
        }

        @Override
        public void onClick(View view) {



            int clickedPosition = getAdapterPosition();
            String album = "";
            int i = 0;
            int j = 0;


            for(int p: positions) {
                if(p == clickedPosition) {
                    break;
                }
                i++;
            }

            for(String a: albums) {
                if(j == i) {
                   album = a;
                   break;
                }
                j++;
            }

            mOnClickListener.onListItemClick(clickedPosition, album);
        }
    }
}
