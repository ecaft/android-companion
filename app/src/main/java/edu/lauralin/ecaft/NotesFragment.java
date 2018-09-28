package edu.lauralin.ecaft;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    private List<String> myList;  // String list that contains file paths to images
    private GridView gridview;
    private String mCurrentPhotoPath;

    protected AbsListView mListView;
    protected TextView mEmptyTextView;
    protected ProgressDialog mLoadingProgressDialog;

    public NotesFragment() {
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
        Button cameraButton = (Button) v.findViewById(R.id.cameraButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), CameraActivity.class);
                getActivity().startActivity(myIntent);
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
