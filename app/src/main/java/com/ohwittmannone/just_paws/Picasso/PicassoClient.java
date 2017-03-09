package com.ohwittmannone.just_paws.Picasso;

import android.content.Context;
import android.widget.ImageView;

import com.ohwittmannone.just_paws.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Courtney on 2017-01-08.
 */

public class PicassoClient {

    public static void downloadImage(Context context, String url, ImageView img){
        if(url != null && url.length()>0){
            Picasso.with(context).load(url).fit().centerCrop().placeholder(R.drawable.placeholder).into(img);
        }
        else {
            Picasso.with(context).load(R.drawable.placeholder).into(img);
        }
    }
}
