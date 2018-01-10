package edu.ashleyxue.ecaft;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.text.InputType;
import android.support.v4.app.FragmentManager;


import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashley on 11/8/2015.
 */
public class ChecklistFragment extends DialogFragment{

    private static final String TAG = "ECaFT";
    private static final String STATE_KEY = "Recycler State";

    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;

    private View emptyView;

    private List<Integer> isVisitedList;
    private List<String> companies;
    private List<String> companyLocations;

    private CharSequence[] items;
    private List<CharSequence> allCompanies;
    private List<CharSequence> unCheckedCompanies;
    private int checkedItem = -1;


    private Parcelable savedState;
    private Bundle savedBundle;

    private int swipedPosition;

    private boolean listButtonClicked = false;

    private ImageButton addCompany;




    public ChecklistFragment() {
        isVisitedList = MainActivity.makeIsVisited(MainActivity.currentUserList);
        companies = MainActivity.makeSavedList(MainActivity.currentUserList);
        companyLocations = MainActivity.makeSavedList(MainActivity.currentUserList);
        companyAdapter = new CompanyAdapter(companies, companyLocations);

        items = new CharSequence[1];
        allCompanies = new ArrayList<CharSequence>();
        unCheckedCompanies = new ArrayList<CharSequence>();

    }

    public List<CharSequence> filterLists(List<CharSequence> allCompanies, List<String> checkedCompanies){
        List<CharSequence> notStarred = new ArrayList<CharSequence>();
        for(CharSequence s: allCompanies){
            if(!checkedCompanies.contains(s)) {
                int i = MainActivity.allCompanyIds.indexOf(s);
                notStarred.add(MainActivity.allCompanies.get(i));
            }
        }
        return notStarred;
    }

    @TargetApi(21)
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        MainActivity.currentUserList = 0;
        View v = inflater.inflate(R.layout.checklist_fragment, container, false);
        final RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.list_button);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                if(listButtonClicked)
                    params.topMargin = 1000;
                else{
                    params.topMargin = 1400;
                }
                listButtonClicked = !listButtonClicked;
                layout.setLayoutParams(params);
            }

        });

        addCompany = (ImageButton) v.findViewById(R.id.checklist_addCompany);
        addCompany.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                createDialog().show();
            }
        });
        //final RelativeLayout addListLayout = (RelativeLayout) v.findViewById(R.id.add_list_button);
        final Button favoriteList = (Button) v.findViewById((R.id.select_list));
        final ImageButton addListButton = (ImageButton) v.findViewById(R.id.add_list_button);
        addListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Button newList = new Button(getContext());
                createNewListDialog(newList);

                RelativeLayout.LayoutParams params1 =
                        (RelativeLayout.LayoutParams) favoriteList.getLayoutParams();
                int top = params1.topMargin;
                int left = params1.leftMargin;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        (favoriteList.getWidth(),favoriteList.getHeight());
                params.topMargin = top;
                params.leftMargin = (MainActivity.mDatabases.size() * favoriteList.getWidth()) + left;
                newList.setLayoutParams(params);
                //newList.setBackgroundResource(R.drawable.ic_list_unselected);
                newList.setBackgroundTintList
                        (getContext().getResources().getColorStateList(R.color.green));
                newList.setBackgroundResource(R.drawable.ic_userlist_unselected);
                //newList.setBackgroundResource(R.color.black);


                layout.addView(newList);
                MainActivity.currentUserList = MainActivity.mDatabases.size();
                //DatabaseHelper newDB = new DatabaseHelper(getContext());
                //newDB.onCreate(new DatabaseHelper(MainActivity.mContext).getWritableDatabase());
                MainActivity.mDatabases.add(
                        new DatabaseHelper(MainActivity.mContext).getWritableDatabase());
                //MainActivity.mDatabases.add(newDB.getWritableDatabase());
                //MainActivity.mDatabases.get(MainActivity.currentUserList).
                  //      execSQL("delete from "+ DatabaseSchema.CompanyTable.NAME);
                Log.d("testtest", MainActivity.mDatabases.size() + "");
                //Log.d("testtest", favoriteList.getHeight() + "");
                //Log.d("testtest", favoriteList.getWidth() + "");
                updateUI();
                /*
                ChecklistFragment fragment = (ChecklistFragment)
                        getFragmentManager().findFragmentById(R.id.checklist_fragment);

                getFragmentManager().beginTransaction()
                        .detach(fragment)
                        .attach(fragment)
                        .commit();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.checklist_fragment,
                        new ChecklistFragment()).addToBackStack(null)
                        .commit();
                        */
            }

        });

        companyRecylerView = (RecyclerView) v.findViewById(R.id.checklist_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyRecylerView.setHasFixedSize(true);
        companyRecylerView.setAdapter(companyAdapter);

        emptyView = v.findViewById(R.id.list_fragment_empty_view);

        getActivity().setTitle("Your Favorites");

        //return inflater.inflate(R.layout.checklist_fragment, container, false);
        return v;
    }


    public AlertDialog createNewListDialog(final Button b){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create a New List");
        // Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newListName = input.getText().toString();
                Log.d("testtest", newListName);
                MainActivity.userListNames.add(newListName);
                b.setText(newListName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                b.setText("Untitled List");
                dialog.cancel();
            }
        });

        return builder.show();
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a Company");
        updateSavedLists();
        CharSequence[] temp = MainActivity.allCompanyIds.toArray(new CharSequence[0]);
        allCompanies = Arrays.asList(temp);
        unCheckedCompanies = filterLists(allCompanies,companies);
        items = unCheckedCompanies.toArray(new CharSequence[0]);

        checkedItem = -1;

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                checkedItem = which;
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                if(checkedItem != -1) {
                    Log.d("qwert",checkedItem + "");
                    int i = MainActivity.allCompanies.indexOf(items[checkedItem].toString());
                    MainActivity.addRow(
                            MainActivity.allCompanyIds.get(i), items[checkedItem].toString());
                    updateUI();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        return builder.create();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        //MainActivity.navigationView.setSelectedItemId(R.id.nav_checklist);
        MainActivity.navigationView.setCheckedItem(R.id.nav_checklist);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState
                    .getParcelable(STATE_KEY);
            companyRecylerView.getLayoutManager().onRestoreInstanceState
                    (savedRecyclerLayoutState);
        }
    }

    private void updateVisitedList() {
        isVisitedList = MainActivity.makeIsVisited(MainActivity.currentUserList);
    }

    private void updateSavedLists() {
        companies = MainActivity.makeSavedList(MainActivity.currentUserList);
        companyLocations = MainActivity.makeSavedList(MainActivity.currentUserList);
    }

    public void updateUI() {
        updateVisitedList();
        updateSavedLists();
        unCheckedCompanies = filterLists(allCompanies,companies);
        items = unCheckedCompanies.toArray(new CharSequence[0]);

        companyAdapter = new CompanyAdapter(companies, companyLocations);
        companyRecylerView.setAdapter(companyAdapter);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (companyRecylerView != null) {
            savedState = companyRecylerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(STATE_KEY, savedState);
        }
        updateUI();
    }


    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mCompanyRL;
        public TextView mCompanyName;
        public TextView mCompanyLocation;
        public int currentPosition;
        public CheckBox mCompanyVisited;
        public FirebaseCompany currentCompany;
        public LinearLayout delete;
        public String currentCompanyName;

        public CompanyHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle myBundle = new Bundle();
                    myBundle.putString(FirebaseApplication.COMPANY_ID,
                            currentCompany.id);
                    myBundle.putString(FirebaseApplication.COMPANY_NAME,
                            currentCompany.name);
                    myBundle.putString(FirebaseApplication.COMPANY_MAJORS,
                            currentCompany.majors);
                    myBundle.putString(FirebaseApplication.COMPANY_TABLE,
                            currentCompany.location);
                    myBundle.putString(FirebaseApplication.COMPANY_JOBTITLES,
                            currentCompany.jobtitles);
                    myBundle.putString(FirebaseApplication.COMPANY_JOBTYPES,
                            currentCompany.jobtypes);
                    myBundle.putString(FirebaseApplication.COMPANY_INFO,
                            currentCompany.information);
                    myBundle.putString(FirebaseApplication.COMPANY_WEBSITE,
                            currentCompany.website);
                    myBundle.putBoolean(FirebaseApplication.COMPANY_OPTCPT,
                            currentCompany.optcpt);
                    myBundle.putBoolean(FirebaseApplication.COMPANY_SPONSOR,
                            currentCompany.sponsor);
                    myBundle.putBoolean(FirebaseApplication.SHOW_NOTES, true);

                    Intent i = new Intent(getActivity(),
                            CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);
                }
            });

            mCompanyName = (TextView) itemView.findViewById(
                    R.id.company_checklist_name);

            mCompanyLocation = (TextView) itemView.findViewById(R.id
                    .checklist_company_table);

            mCompanyVisited = (CheckBox) itemView.findViewById(
                    R.id.check_company);
            mCompanyVisited.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCompanyVisited.isChecked()) {
                                Toast.makeText(getContext(), R.string.visited,
                                        Toast.LENGTH_SHORT).show();
                                MainActivity.setVisitStatus(currentCompany, 1);
                            } else {
                                MainActivity.setVisitStatus(currentCompany, 0);
                                isVisitedList.set(currentPosition, 0);

                            }
                        }
                    }

            );
        }
    }

    private class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
        public List<String> companies;
        public List<String> companyLocations;
        public FirebaseCompany fc;
        public CompanyAdapter(List<String> companies, List<String> companyLocations) {
            this.companies = companies;
            this.companyLocations = companyLocations;
        }



        @Override
        public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(
                    R.layout.checklist_fragment_cardview, parent, false);
            return new CompanyHolder(view);
        }

        @Override
        public void onBindViewHolder(final CompanyHolder holder, int position) {

            String id = companies.get(position);

            Log.d("checklist", id);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("companies").child(id);

            Log.d("checklsit", databaseReference.toString());

            databaseReference.addValueEventListener(new ValueEventListener
                    () {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    fc = dataSnapshot.getValue(FirebaseCompany
                            .class);
                    holder.currentCompany = fc;
                    holder.currentCompanyName = fc.name;
                    holder.mCompanyName.setText(fc.name);
                    //holder.currentPosition = fc.location
                    holder.mCompanyLocation.setText(fc.location);

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("test", "oncancel");
                }
            });

            holder.currentPosition = position;
            holder.mCompanyVisited.setChecked(isVisitedList.get(position) == 1);

            Log.d(TAG, "Recycler made for position " + position);
        }


        public void delete(int position) { //removes the row
            companies.remove(position);
            notifyItemRemoved(position);

        }

        @Override
        public int getItemCount() {
            return companies.size();
        }

    }


}
