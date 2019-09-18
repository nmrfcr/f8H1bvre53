package com.tekapic;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.AudioManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tekapic.model.Album;

import java.util.ArrayList;

/**
 * Created by LEV on 21/08/2018.
 */
public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Album> albumsList=new ArrayList<Album>() ;
    private ListItemClickListener mOnClickListener;


    public AlbumsRecyclerViewAdapter(ArrayList<Album> albumsList, ListItemClickListener v1, Context v2) {
        this.albumsList=albumsList;
        this.mOnClickListener =  v1;
        this.context= v2;
    }


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, String album);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums_row, parent, false);
        MyViewHolder myViewHolder= new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String cap = albumsList.get(position).getName().substring(0, 1).toUpperCase() + albumsList.get(position).getName().substring(1);
        holder.albumName.setText(cap);

        Glide.with(context)
                .load(albumsList.get(position).getPicture())
                .apply(new RequestOptions().placeholder(R.drawable.grad))
                .into(holder.albumImage);
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnTouchListener {

        ImageView albumImage;
        TextView albumName;

        public MyViewHolder(View itemView) {
            super(itemView);

            albumImage= itemView.findViewById(R.id.rowAlbum);
            albumName = itemView.findViewById(R.id.textViewAlbum);
            itemView.setOnTouchListener(this);

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            if(event.getAction() == MotionEvent.ACTION_CANCEL) {

                albumImage.setColorFilter(ContextCompat.getColor(context, R.color.noColor));

            }


            if(event.getAction() == MotionEvent.ACTION_UP) {

                AudioManager audioManager = (AudioManager) context.getSystemService(
                        Context.AUDIO_SERVICE);

                audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);

                albumImage.setColorFilter(ContextCompat.getColor(context, R.color.noColor));


                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Album album : albumsList) {
                    if(x++ == clickedPosition) {


                        mOnClickListener.onListItemClick(clickedPosition, album.getName());

                        break;
                    }
                }

            }


            if(event.getAction() == MotionEvent.ACTION_DOWN) {

                ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
                albumImage.setColorFilter(filter);



            }

            return true;
        }

    }
}
