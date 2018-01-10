package edu.ashleyxue.ecaft;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        View v = inflater.inflate(R.layout.company_details_fragment, container, false);
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


//        Log.d("details", info);
        // logo = FirebaseApplication.getLogoByID(objectID);

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

        companyNotes = new EditText(inflater.getContext()){
            @Override
            public boolean onKeyPreIme(int keyCode, KeyEvent event) {
                Log.d("details", keyCode + "");
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    Log.d("details", "elaufhlsieuhfaelsuhgaeu");
                }
                return super.onKeyPreIme(keyCode, event);
            }
        };
        companyNotes = (EditText) v.findViewById(R.id
                .company_details_editText);


        if (showText) {
            notes_company_info.setVisibility(View.VISIBLE);
            notes_company_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (companyNotes.getVisibility() == View.GONE) {
                        companyNotes.setVisibility(View.VISIBLE);
                        companyNotesHeader.setVisibility(View.VISIBLE);
                        companyPhotosHeader.setVisibility(View.VISIBLE);
                        companyMajorsHeader.setVisibility(View.GONE);
                        companyMajors.setVisibility(View.GONE);
                        companySponsorHeader.setVisibility(View.GONE);
                        companyInfoHeader.setVisibility(View.GONE);
                        companyInfo.setVisibility(View.GONE);
                        companyOpeningsHeader.setVisibility(View.GONE);
                        companyOpenings.setVisibility(View.GONE);
                        companySponsorHardCode.setVisibility(View.GONE);
                    } else {
                        companyNotes.setVisibility(View.GONE);
                        companyNotesHeader.setVisibility(View.GONE);
                        companyPhotosHeader.setVisibility(View.GONE);
                        companyMajorsHeader.setVisibility(View.VISIBLE);
                        companyMajors.setVisibility(View.VISIBLE);
                        companySponsorHeader.setVisibility(View.VISIBLE);
                        companyInfoHeader.setVisibility(View.VISIBLE);
                        companyInfo.setVisibility(View.VISIBLE);
                        companyOpeningsHeader.setVisibility(View.VISIBLE);
                        companyOpenings.setVisibility(View.VISIBLE);
                        companySponsorHardCode.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        /*camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });*/

/*
        if(notes_company_info.isChecked()){
            companyNotes.setVisibility(View.VISIBLE);
            companyNotesHeader.setVisibility(View.VISIBLE);
            companyMajorsHeader.setVisibility(View.GONE);
            companyMajors.setVisibility(View.GONE);
            companySponsorHeader.setVisibility(View.GONE);
            companyInfoHeader.setVisibility(View.GONE);
            companyInfo.setVisibility(View.GONE);
            companyOpeningsHeader.setVisibility(View.GONE);
            companyOpenings.setVisibility(View.GONE);
            companySponsorHardCode.setVisibility(View.GONE);
            //companySponsor.setVisibility(View.GONE);
            //companyOptcpt.setVisibility(View.GONE);
        }
*/
//
//        companyNotes.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                companyNotes.setCursorVisible(true);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("details", s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.d("details", "done changing text: " + s.toString());
//            }
//        });
//
        companyNotes.setOnEditorActionListener(new EditText
                .OnEditorActionListener() {
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

//        companySponsor = (TextView) v.findViewById(R.id
//                .company_details_sponsor_info);
//        companySponsor.setText(sponsorText);

//        companyOptcpt = (TextView) v.findViewById(R.id
//                .company_details_opt_cpt_text);
//        companyOptcpt.setText(optcptText);

        companyLogo = (ImageView) v.findViewById(R.id.company_details_logo);

        StorageReference path = storageRef.child("logos/" +
                objectID + ".png");

        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(path)
                .into(companyLogo);

        getActivity().setTitle(name);

//        String majorText = "";
//        Object[] majorList = majors.toArray();
//
//        majorList = alphabetize(majorList);
//
//        for (Object s : majorList) {
//            majorText = majorText + s.toString() + "\n";
//        }
//        companyMajors.setText(majorText);

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
        MainActivity.saveNote(objectID, companyNotes.getText().toString());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
