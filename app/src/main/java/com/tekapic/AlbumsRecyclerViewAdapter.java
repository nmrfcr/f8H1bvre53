package com.tekapic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tekapic.model.Album;

import java.util.ArrayList;

/**
 * Created by LEV on 21/08/2018.
 */
public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Album> albumsList=new ArrayList<Album>() ;
    private ListItemClickListener mOnClickListener;
    private Bitmap bitmap = null;
    private Bitmap lightBitmap = null;


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

            AudioManager audioManager;

            if(event.getAction() == MotionEvent.ACTION_CANCEL) {


                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Album album : albumsList) {
                    if(x++ == clickedPosition) {

                        Glide.with(context)
                                .load(albumsList.get(clickedPosition).getPicture())
                                .apply(new RequestOptions().placeholder(R.drawable.grad))
                                .into(albumImage);

                        break;
                    }
                }
            }


            if(event.getAction() == MotionEvent.ACTION_UP) {

                audioManager = (AudioManager) context.getSystemService(
                        Context.AUDIO_SERVICE);

                audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);

                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Album album : albumsList) {
                    if(x++ == clickedPosition) {

                        Glide.with(context)
                                .load(albumsList.get(clickedPosition).getPicture())
                                .apply(new RequestOptions().placeholder(R.drawable.grad))
                                .into(albumImage);
                        mOnClickListener.onListItemClick(clickedPosition, album.getName());

                        break;
                    }
                }

            }


            if(event.getAction() == MotionEvent.ACTION_DOWN) {

                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Album album : albumsList) {
                    if(x++ == clickedPosition) {
                        Glide.with(context)
                                .load(album.getPicture())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                        Log.i("onLoadFailed", "failed to get picture");

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                        try {
                                            bitmap = ((BitmapDrawable)resource).getBitmap();
                                        }catch (Exception e) {
                                            Log.i("Error bitmpap", "errog convert resource to bitmap");
                                            return false;

                                        }

                                        lightBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                                        lightBitmap = darkenBitMap(lightBitmap);

                                        albumImage.setImageBitmap(lightBitmap);

                                        Log.i("bitmap", "image bitmap setted");

                                        return true;
                                    }
                                })
                                .into(albumImage);
                    }
                }

            }

            return true;
        }


        private Bitmap darkenBitMap(Bitmap bm) {

            Canvas canvas = new Canvas(bm);
            Paint p = new Paint(Color.RED);
            ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
            p.setColorFilter(filter);
            canvas.drawBitmap(bm, new Matrix(), p);

            return bm;
        }
    }
}
