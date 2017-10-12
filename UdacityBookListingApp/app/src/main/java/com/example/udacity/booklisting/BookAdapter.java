package com.example.udacity.booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


class BookAdapter extends ArrayAdapter<Book>{


    private static final String TAG = BookAdapter.class.getName();
    private Context mContext;

    public BookAdapter(@NonNull Context context, @NonNull List objects){
        super(context, 0, objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        Book data = (Book) getItem(position);

        ImageView bookCover = (ImageView) view.findViewById(R.id.book_cover);
        String thumbnailId = data.getThumbnail();
        if(thumbnailId != null && !thumbnailId.isEmpty()){
            new LoadThumbnailTask(bookCover).execute(thumbnailId);
        }else{
            bookCover.setImageResource(R.drawable.no_image);
        }

        TextView title = (TextView) view.findViewById(R.id.tv_book_title);
        title.setText(data.getTitle());

        TextView author = (TextView) view.findViewById(R.id.tv_book_author);
        author.setText(data.getAuthor());

        return view;
    }


    private class LoadThumbnailTask extends AsyncTask<String, Void, Bitmap>{
        ImageView mThumbnail;

        public LoadThumbnailTask(ImageView bmImage){
            this.mThumbnail = bmImage;
        }

        protected Bitmap doInBackground(String... urls){
            String mThumbnailLink = urls[0];
            Bitmap bitmap = null;
            try{
                InputStream in = new URL(mThumbnailLink).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            }catch(Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result){
            mThumbnail.setImageBitmap(result);
        }
    }
}
