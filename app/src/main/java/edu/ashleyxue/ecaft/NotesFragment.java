package edu.ashleyxue.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

/**
 * Created by robotbf on 12/28/17.
 */

public class NotesFragment extends Fragment{
    private TextView companyName;
    private String name;
    private TextView companyLocation;
    private ImageView companyLogo;
    private String objectID;
    private String companyTable;
    private StorageReference storageRef = FirebaseApplication
            .getStorageRef();


    public NotesFragment() {
        // Empty constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notes_fragment, container, false);
        Bundle args = getArguments();
        objectID = args.getString(FirebaseApplication.COMPANY_ID);
        name = args.getString(FirebaseApplication.COMPANY_NAME);
        companyTable = args.getString(FirebaseApplication.COMPANY_TABLE);
        companyName = (TextView) v.findViewById(R.id.company_details_name);
        companyName.setText(name);
        companyLocation = (TextView) v.findViewById(R.id
                .company_details_location);
        companyLocation.setText("Table " + companyTable);

        Button notesButton = (Button) v.findViewById(R.id.notesButton);

        notesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Notes Page", "Button Being Clicked");
                NotesFragment noteFrag = new NotesFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup)(getView().getParent())).getId(), noteFrag);
                transaction.addToBackStack(null);
                Log.d("Notes Page", "CALLING NEW FRAGMENT");
                transaction.commit();
            }
        });
        companyLogo = (ImageView) v.findViewById(R.id.company_details_logo);

        StorageReference path = storageRef.child("logos/" +
                objectID + ".png");

        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(path)
                .into(companyLogo);

        getActivity().setTitle(name);

        return v;
    }

}
