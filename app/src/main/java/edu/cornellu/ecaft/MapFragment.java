package edu.cornellu.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)
                v.findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.career_map_final_3));

   //     imageView = (ImageView) v.findViewById(R.id.map_fragment_image_view);

        getActivity().setTitle("Map");

        return v;
    }
}