package edu.cornell.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


        imageView = (ImageView) v.findViewById(R.id.map_fragment_image_view);

        getActivity().setTitle("Map");
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setLogo(R.drawable.black);
        return v;
    }
}
