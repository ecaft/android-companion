package edu.ashleyxue.ecaft;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import android.content.Intent;
import android.provider.MediaStore;
import android.content.Context;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ashley on 1/16/2016.
 */
public class CompanyDetailsFragment extends Fragment {

    private TextView companyName;
    private TextView companyMajors;
    private TextView companyMajorsHeader;
    private TextView companyLocation;
    private TextView companyOpenings;
    private TextView companyOpeningsHeader;
    private ImageView companyLogo;
    private TextView companyInfo;
    private TextView companyInfoHeader;
    private TextView companyWebsite;
    private TextView companyNotesHeader;
    private static EditText companyNotes;
    private TextView companySponsor;
    private TextView companyOptcpt;
    private TextView companySponsorHeader;
    private TextView companySponsorHardCode;
    private TextView companyPhotosHeader;

    private ToggleButton notes_company_info;

    private Button camera_button;
    static final int REQUEST_IMAGE_CAPTURE = 1; //for picture taking

    private String companyTable;
    private String objectID;
    private String name;
    private String majors;
    private String info;
    private String jobtitles;
    private String jobtypes;
    private String website;
    private String sponsorText = "This company cannot sponsor the " +
            "candidate.";
    private String optcptText = "This company does not accept opt/cpt.";
    private String notesText = "";
    private boolean optcpt;
    private boolean sponsor;
    private boolean showText;
    private StorageReference storageRef = FirebaseApplication
            .getStorageRef();
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.company_details_fragment, container, false);
        Bundle args = getArguments();
        objectID = args.getString(FirebaseApplication.COMPANY_ID);
        name = args.getString(FirebaseApplication.COMPANY_NAME);
        companyTable = args.getString(FirebaseApplication.COMPANY_TABLE);
        majors = args.getString(FirebaseApplication.COMPANY_MAJORS);
        jobtitles = args.getString(FirebaseApplication.COMPANY_JOBTITLES);
        info = args.getString(FirebaseApplication.COMPANY_INFO);
        jobtypes = args.getString(FirebaseApplication.COMPANY_JOBTYPES);
        website = args.getString(FirebaseApplication.COMPANY_WEBSITE);
        optcpt = args.getBoolean(FirebaseApplication.COMPANY_OPTCPT);
        sponsor = args.getBoolean(FirebaseApplication.COMPANY_SPONSOR);
        showText = args.getBoolean(FirebaseApplication.SHOW_NOTES);

        if (jobtitles.isEmpty())
            jobtitles = "Check the company's career website or Handshake to " +
                    "learn more and apply online.";
        if (majors.isEmpty())
            majors = "Check the company's career website to learn more.";
        if (info.isEmpty())
            info = "Check the company's career website to learn more.";
        if (sponsor)
            sponsorText = "This company can sponsor the candidate.";
        if (optcpt)
            optcptText = "This company accepts opt/cpt.";


        companyName = (TextView) v.findViewById(R.id.company_details_name);
        companyName.setText(name);

        companyMajors = (TextView) v.findViewById(R.id.company_details_majors);
        companyMajors.setText(majors);

        companyMajorsHeader = (TextView) v.findViewById(R.id.company_majors_text);

        companyLocation = (TextView) v.findViewById(R.id
                .company_details_location);
        companyLocation.setText("Table " + companyTable);

        companyOpenings = (TextView) v.findViewById(R.id.company_details_positions);
        companyOpenings.setText(jobtitles);

        companyOpeningsHeader = (TextView) v.findViewById(R.id.company_positions_text);

        companyInfo = (TextView) v.findViewById(R.id.company_details_information);
        companyInfo.setText(info);

        companyInfoHeader = (TextView) v.findViewById(R.id.company_information_text);

        companyWebsite = (TextView) v.findViewById(R.id
                .company_details_website);
        companyWebsite.setText(website);

        companyNotesHeader = (TextView) v.findViewById(R.id
                .company_details_notes_header);

        companySponsorHeader = (TextView) v.findViewById(R.id.company_details_sponsor_header);
        companySponsorHardCode = (TextView) v.findViewById(R.id.company_details_sponsor_info);

        companyPhotosHeader = (TextView) v.findViewById(R.id.company_details_photos_header);

        notes_company_info = (ToggleButton) v.findViewById(R.id.notes_or_info);

        companyNotes = (EditText) v.findViewById(R.id.company_details_editText);

        //companyNotes = new android.support.v7.widget.AppCompatEditText(inflater.getContext()){

        //    Button notesButton = (Button) v.findViewById(R.id.notesButton);

            /*notesButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("Notes Page", "Button Being Clicked");
                        NotesFragment noteFrag = new NotesFragment();
                        Intent i = getActivity().getIntent();
                        Bundle x = i.getExtras();
                        noteFrag.setArguments(i.getExtras());
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(((ViewGroup)(getView().getParent())).getId(), noteFrag);
                        transaction.addToBackStack(null);
                        Log.d("Notes Page", "CALLING NEW FRAGMENT");
                        transaction.commit();
                    }
                });*/

        companyNotes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent
                    event) {
                Log.d("details", "action: " + actionId + " event: " + event);
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction
                        () == KeyEvent.ACTION_DOWN || event.getKeyCode() ==
                        KeyEvent.KEYCODE_BACK ||
                        event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d("details", "save button pressed");
                    companyNotes.clearFocus();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(companyNotes.getWindowToken(), 0);
                    MainActivity.saveNote(objectID, companyNotes.getText().toString());
                    return true; // consume
                }
                return false; // pass on to other listeners
            }
        });
        companyNotes.setHorizontallyScrolling(false);
        companyNotes.setMaxLines(8);


        Log.d("details", "id of the company for notes: " + objectID);
        notesText = MainActivity.getNote(objectID);
        if (!notesText.isEmpty())
            companyNotes.setText(notesText);
        else
            companyNotes.setHint("Add a note for this company");


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

    public static  void clearFocus() {
        companyNotes.clearFocus();
    }

    public Object[] alphabetize(Object[] array) {
        for (int x = 0; x < array.length - 1; x++) {
            int smallest = findSmallest(array, x);
            Object temp = array[x];
            array[x] = array[smallest];
            array[smallest] = temp;
        }
        return array;
    }

    public int findSmallest(Object[] array, int i) {
        int temp = i;
        for (int x = i; x < array.length - 1; x++) {
            if (array[temp].toString().compareTo(array[x + 1].toString()) > 0)
                temp = x + 1;
        }
        return temp;
    }

    @Override
    public void onStop() {
        super.onStop();
        //MainActivity.saveNote(objectID, companyNotes.getText().toString());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
