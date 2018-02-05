package edu.ashleyxue.ecaft;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by pdarb on 1/31/2018.
 */

public class EnlargedImage extends Activity {
    private ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enlarged_image);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        String file = "";
        if (!bundle.isEmpty()) {
            file = bundle.getString("file", "");
        }

        image = (ImageView) this.findViewById(R.id.img);
        image.setImageDrawable(Drawable.createFromPath(file));
    }
}
