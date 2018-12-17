package com.tekapic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Album;
import com.tekapic.model.Picture;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by LEV on 23/08/2018.
 */
public class PicturesRecyclerViewAdapter extends RecyclerView.Adapter<PicturesRecyclerViewAdapter.MyViewHolder>    {

    private int mNumberOfItems;
    private Context context;
    private ListItemClickListener mOnClickListener;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private ArrayList<Integer> positions=new ArrayList<Integer>() ;
    MyViewHolder h;
    Bitmap bitmap = null;
    Bitmap darkBitmap = null;
    MotionEvent ev;
    String src;
    static boolean flag = false;


    public PicturesRecyclerViewAdapter(ArrayList<Picture> picturesList, ListItemClickListener v1, Context v2) {

        this.picturesList=picturesList;
        this.mOnClickListener =  v1;
        this.context= v2;
    }




    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList);
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

        Glide.with(context)
                .load(picturesList.get(position).getPictureUrl())
                .apply(new RequestOptions().placeholder(R.drawable.b))
                .into(holder.imageView);


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




    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView= itemView.findViewById(R.id.rowImageView);
//            itemView.setOnClickListener(this);

            itemView.setOnTouchListener(this);



        }
        void bind (int listIndex){
            //ItemNumberTv.setText(String.valueOf(listIndex));
            //itemNumberTextTv.setText("ViewHolder index :"+String.valueOf(listIndex));
//            imageView.setImageResource(pictureUrlList.get(listIndex).getPicture());
        }


//
//        @Override
//        public void onClick(View view) {
//
//            int clickedPosition = getAdapterPosition();
//            int x = 0;
//
//            for(Picture picture : picturesList) {
//                if(x++ == clickedPosition) {
//                    mOnClickListener.onListItemClick(clickedPosition, picture, getItemCount(), picturesList);
//                    break;
//                }
//            }
//
//        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {




            if(event.getAction() == MotionEvent.ACTION_CANCEL) {
//                if(bitmap != null) {
//                    imageView.setImageBitmap(bitmap);
//                }

                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Picture picture : picturesList) {
                    if(x++ == clickedPosition) {
                        //glideeeeeeeee

//                        Glide.with(context)
//                                .load(picturesList.get(clickedPosition).getPictureUrl())
//                                .into(imageView);

                        Glide.with(context)
                                .load(picturesList.get(clickedPosition).getPictureUrl())
                                .apply(new RequestOptions().placeholder(R.drawable.b))
                                .into(imageView);

                        break;
                    }
                }
            }



                if(event.getAction() == MotionEvent.ACTION_UP) {

//                    imageView.setImageBitmap(bitmap);


                    int clickedPosition = getAdapterPosition();
                    int x = 0;

                for(Picture picture : picturesList) {
                    if(x++ == clickedPosition) {

                        Glide.with(context)
                                .load(picturesList.get(clickedPosition).getPictureUrl())
                                .apply(new RequestOptions().placeholder(R.drawable.b))
                                .into(imageView);

                        mOnClickListener.onListItemClick(clickedPosition, picture, getItemCount(), picturesList);
                        break;
                    }
                }

            }
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                Toast.makeText(context, "ACTION_DOWN", Toast.LENGTH_SHORT).show();

                int clickedPosition = getAdapterPosition();
                int x = 0;

                for(Picture picture : picturesList) {
                    if(x++ == clickedPosition) {
                        Glide.with(context)
                                .load(picture.getPictureUrl())
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


                                        darkBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


                                        darkBitmap = darkenBitMap(darkBitmap);

                                        //here



                                        imageView.setImageBitmap(darkBitmap);
                                        flag = true;


                                        Log.i("bitmap", "bitmappppppppppppppp");


                                        return true;
                                    }
                                })
                                .into(imageView);
                    }
                }



            }


            return true;
        }

        private Bitmap darkenBitMap(Bitmap bm) {

            Canvas canvas = new Canvas(bm);
            Paint p = new Paint(Color.RED);
            //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
            ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
            p.setColorFilter(filter);
            canvas.drawBitmap(bm, new Matrix(), p);

            return bm;
        }






    }


}
