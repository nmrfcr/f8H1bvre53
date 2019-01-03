package com.tekapic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.flags.impl.DataUtils;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Album;
import com.tekapic.model.Picture;

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
    Bitmap bitmap = null;
    Bitmap lightBitmap = null;




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
                .apply(new RequestOptions().placeholder(R.drawable.grad))
                .into(holder.albumImage);


//
//        albums.add(albumsList.get(position).getName());
//        positions.add(position);


    }

    @Override
    public int getItemCount() {
        return albumsList.size(); //mNumberOfItems;
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
        void bind (int listIndex){
            //ItemNumberTv.setText(String.valueOf(listIndex));
            //itemNumberTextTv.setText("ViewHolder index :"+String.valueOf(listIndex));
            albumImage.setImageResource(albumsList.get(listIndex).getPicture());
            albumName.setText(albumsList.get(listIndex).getName());
        }

//        @Override
//        public void onClick(View view) {
//
//
//            int clickedPosition = getAdapterPosition();
//            int x = 0;
//
//            for(Album album : albumsList) {
//                if(x++ == clickedPosition) {
//                    mOnClickListener.onListItemClick(clickedPosition, album.getName());
//                    break;
//                }
//            }
//
//        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            AudioManager audioManager;

            if(event.getAction() == MotionEvent.ACTION_CANCEL) {
//                if(bitmap != null) {
//                    imageView.setImageBitmap(bitmap);
//                }

                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Album album : albumsList) {
                    if(x++ == clickedPosition) {
                        //glideeeeeeeee

//                        Glide.with(context)
//                                .load(picturesList.get(clickedPosition).getPictureUrl())
//                                .into(imageView);

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
//                Toast.makeText(context, "ACTION_DOWN", Toast.LENGTH_SHORT).show();

                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Album album : albumsList) {
                    if(x++ == clickedPosition) {
                        Glide.with(context)
                                .load(album.getPicture())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                        Log.i("onLoadFailed", "failed to get pictureeeeeeeeeeeeeeeeeeee");

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

                                        //here

                                        albumImage.setImageBitmap(lightBitmap);


                                        Log.i("bitmap", "bitmappppppppppppppp");


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
