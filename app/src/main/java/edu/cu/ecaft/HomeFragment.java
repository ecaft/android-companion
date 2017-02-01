package edu.cu.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Ashley on 11/7/2015.
 */
public class HomeFragment extends Fragment {

    private TextView textView;
    private ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(
                R.layout.home_fragment, container, false);
        Bundle args = getArguments();
        textView = (TextView) v.findViewById(R.id.home_fragment_text_view1);

        image = (ImageView) v.findViewById(R.id.logos);

       // getActivity().navi

        getActivity().setTitle("Home");

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.navigationView.setCheckedItem(R.id.nav_home);
    }
}
