package edu.lauralin.ecaft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.content.res.Configuration;
import android.widget.Toast;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * Created by Ashley on 11/8/2015.
 */
public class MapFragment extends Fragment {

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(
                R.layout.map_fragment, container, false);
        Bundle args = getArguments();

        final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)
                v.findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable
                .career_fair_map_2_10));


        getActivity().setTitle("Map");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bbb", view.getTop() + ", " + view.getBottom());
            }
        });

/*
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("dddddd","Touch coordinates : " +
                            String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()));
                }
                //Log.d("dddddd", imageView.getMatrix().toString());
                //Intent i = new Intent(getActivity(), MainActivity.class);
                //i.putExtras(myBundle);
                //startActivity(i);

                return true;
            }

        });
*/
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //MainActivity.bottomNavigationView.setSelectedItemId(R.id.nav_map);
        MainActivity.navigationView.setCheckedItem(R.id.nav_map);
    }
/*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(getContext(), "landscape", Toast.LENGTH_SHORT).show();
            getView().findViewById(R.id.imageView).setRotation(0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getView().findViewById(R.id.imageView).setRotation(0);
        }
    }
    */

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final float x = event.getX();
                    //+ this.getLeft();
            final float y = event.getY();
                    //+ this.getTop();
            // check if (x,y) is on chair and do other staff
        }
        return super.onTouchEvent(event);
    }
*/


}
