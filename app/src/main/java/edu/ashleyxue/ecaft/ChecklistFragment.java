package edu.ashleyxue.ecaft;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
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
import android.widget.HorizontalScrollView;
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
import android.graphics.drawable.GradientDrawable;


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

    private List<Button> userListButtons;

    private Parcelable savedState;
    private Bundle savedBundle;

    private int swipedPosition;

    private boolean listButtonClicked = false;

    private ImageButton addCompany;




    public ChecklistFragment() {
        isVisitedList = MainActivity.makeUserListIsVisited();
        companies = MainActivity.makeUserListSavedList();
        companyLocations = MainActivity.makeUserListSavedList();

        companyAdapter = new CompanyAdapter(companies, companyLocations);

        items = new CharSequence[1];
        allCompanies = new ArrayList<CharSequence>();
        unCheckedCompanies = new ArrayList<CharSequence>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        userListButtons = new ArrayList<Button>();

        MainActivity.currentUserList = 0;
        View v = inflater.inflate(R.layout.checklist_fragment, container, false);
        final RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.user_list);

        final RelativeLayout userListLayout = (RelativeLayout)
                v.findViewById(R.id.user_list_button_layout);

        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) layout.getLayoutParams();
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
                createAddCompanyDialog().show();
            }
        });

        final Button favoriteList = (Button) v.findViewById((R.id.select_list));
        favoriteList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                userListClick(favoriteList);
            }

        });


        final ImageButton addListButton = (ImageButton) v.findViewById(R.id.add_list_button);
        addListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                createNewListDialog(favoriteList);
            }

        });
        userListButtons.add(favoriteList);

        for(String s : MainActivity.getTables()){
            if(!s.equals("companies")) {
                if(!MainActivity.userListNames.contains(s))
                    MainActivity.userListNames.add(s);
                final Button userList = createUserListButton(s, favoriteList);
                userList.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        userListClick(userList);
                    }

                });
                userList.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        deleteUserListDialog(userList);
                        return true;
                    }
                });
                userListButtons.add(userList);
                userListLayout.addView(userList);
            }
        }

        companyRecylerView = (RecyclerView) v.findViewById(R.id.checklist_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyRecylerView.setHasFixedSize(true);
        companyRecylerView.setAdapter(companyAdapter);

        emptyView = v.findViewById(R.id.list_fragment_empty_view);

        getActivity().setTitle(MainActivity.userListNames.get(MainActivity.currentUserList));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
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
        isVisitedList = MainActivity.makeUserListIsVisited();
    }

    private void updateSavedLists() {
        companies = MainActivity.makeUserListSavedList();
        companyLocations = MainActivity.makeUserListSavedList();
    }

    @TargetApi(21)
    public void updateUI() {
        updateVisitedList();
        updateSavedLists();
        unCheckedCompanies = filterLists(allCompanies,companies);
        items = unCheckedCompanies.toArray(new CharSequence[0]);

        companyAdapter = new CompanyAdapter(companies, companyLocations);
        companyRecylerView.setAdapter(companyAdapter);

        //userListButtons.get(MainActivity.currentUserList).setBackgroundTintList
          //      (getContext().getResources().getColorStateList(R.color.green));
        GradientDrawable drawable = (GradientDrawable)
                userListButtons.get(MainActivity.currentUserList).getBackground();
        drawable.setStroke(20, getResources().getColor(R.color.green));

        if(MainActivity.currentUserList == 0)
            getActivity().setTitle("Favorites");
        else {
            String s = MainActivity.userListNames.get(MainActivity.currentUserList);
            s = s.substring(0,1).toUpperCase() + s.substring(1);
            getActivity().setTitle(s);
        }
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

    @TargetApi(21)
    public void userListClick(Button b){
        Log.d("testtest", userListButtons.indexOf(b) + " " + b.getText());
        Log.d("testtest", userListButtons.get(2).getText() + "");
        if(!b.equals(userListButtons.get(MainActivity.currentUserList))) {
            //b.setBackgroundTintList
              //      (getContext().getResources().getColorStateList(R.color.green));
            GradientDrawable drawable = (GradientDrawable) b.getBackground();
            drawable.setStroke(20, getResources().getColor(R.color.green));

            GradientDrawable drawable1 = (GradientDrawable)
                    userListButtons.get(MainActivity.currentUserList).getBackground();
            drawable1.setStroke(20, getResources().getColor(R.color.slightly_dark_red));
            //userListButtons.get(MainActivity.currentUserList).setBackgroundTintList
              //      (getContext().getResources().getColorStateList(R.color.slightly_dark_red));

            if (b.getText().equals("Favorites"))
                MainActivity.currentUserList = 0;
            else {
                MainActivity.currentUserList = MainActivity.userListNames.indexOf(b.getText());
            }
            updateUI();
        }
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
    public Button createUserListButton(String listName, Button favoriteList){
        Button newList = new Button(getContext());
        newList.setText(listName);
        RelativeLayout.LayoutParams params1 =
                (RelativeLayout.LayoutParams) favoriteList.getLayoutParams();
        int top = params1.topMargin;
        int left = params1.leftMargin;
        int right = params1.rightMargin;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (240, 300);
        params.topMargin = top;
        params.leftMargin = (MainActivity.userListNames.indexOf(listName) * 280) + left;
        params.rightMargin = right;
        newList.setLayoutParams(params);
        Log.d("testtest", params.leftMargin + "");
        //newList.setBackgroundTintList
          //      (getContext().getResources().getColorStateList(R.color.slightly_dark_red));
        //newList.setBackgroundResource(R.drawable.ic_userlist_unselected);
        newList.setBackgroundResource(R.drawable.userlist_button);
        GradientDrawable drawable = (GradientDrawable)newList.getBackground();
        drawable.setStroke(20, getResources().getColor(R.color.slightly_dark_red));

        return newList;
    }

    @TargetApi(21)
    public void addUserListButton(String listName, Button favoriteList){
        final Button newList = new Button(getContext());
        newList.setText(listName);
        RelativeLayout.LayoutParams params1 =
                (RelativeLayout.LayoutParams) favoriteList.getLayoutParams();
        int top = params1.topMargin;
        int left = params1.leftMargin;
        int right = params1.rightMargin;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (240, 300);
        Log.d("testtest", favoriteList.getWidth() + "");
        params.topMargin = top;
        params.leftMargin = ((MainActivity.userListNames.size() - 1) * 280) + left;
        params.rightMargin = right;
        newList.setLayoutParams(params);
        //newList.setBackgroundTintList
          //      (getContext().getResources().getColorStateList(R.color.green));
        //newList.setBackgroundResource(R.drawable.ic_userlist_unselected);
        newList.setBackgroundResource(R.drawable.userlist_button);
        //GradientDrawable drawable = (GradientDrawable)newList.getBackground();
        //drawable.setStroke(20, getResources().getColor(R.color.red));

        GradientDrawable drawable = (GradientDrawable)userListButtons.
                get(MainActivity.currentUserList).getBackground();
        drawable.setStroke(20, getResources().getColor(R.color.slightly_dark_red));
        //userListButtons.get(MainActivity.currentUserList).setBackgroundTintList
          //      (getContext().getResources().getColorStateList(R.color.slightly_dark_red));

        userListButtons.add(newList);
        newList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                userListClick(newList);
            }

        });
        newList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteUserListDialog(newList);
                return true;
            }
        });
        final RelativeLayout layout = (RelativeLayout)
                getView().findViewById(R.id.user_list_button_layout);
        layout.addView(newList);
        MainActivity.currentUserList = MainActivity.userListNames.size() - 1;
        try {
            MainActivity.mDatabase.execSQL("create table " + MainActivity.userListNames.get(
                    MainActivity.currentUserList) + "(" +
                    DatabaseSchema.CompanyTable.Cols.ID + ", " +
                    DatabaseSchema.CompanyTable.Cols.COMPANY_NAME + ", " +
                    DatabaseSchema.CompanyTable.Cols.VISITED + "," +
                    DatabaseSchema.CompanyTable.Cols.NOTE + ")"
            );
        } catch (SQLiteException e){
            MainActivity.mDatabase.execSQL("DROP TABLE IF EXISTS '" + MainActivity.userListNames.get(
                    MainActivity.currentUserList) + "'");
            MainActivity.mDatabase.execSQL("create table " + MainActivity.userListNames.get(
                    MainActivity.currentUserList).trim() + "(" +
                    DatabaseSchema.CompanyTable.Cols.ID + ", " +
                    DatabaseSchema.CompanyTable.Cols.COMPANY_NAME + ", " +
                    DatabaseSchema.CompanyTable.Cols.VISITED + "," +
                    DatabaseSchema.CompanyTable.Cols.NOTE + ")"
            );
        }
        updateUI();
    }

    @TargetApi(21)
    public AlertDialog deleteUserListDialog(final Button b){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure you want to delete the list, " + "\"" + b.getText() + "\"" + "?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.mDatabase.execSQL("DROP TABLE IF EXISTS " + b.getText());
                if(MainActivity.currentUserList >= MainActivity.userListNames.size() - 1)
                    MainActivity.currentUserList--;

                userListButtons.remove(b);
                MainActivity.userListNames.remove(b.getText());

                final RelativeLayout layout = (RelativeLayout)
                        getView().findViewById(R.id.user_list_button_layout);
                layout.removeView(b);
                Button favoriteList = userListButtons.get(0);
                for(Button b : userListButtons){
                    if(!b.getText().equals("Favorites")){
                        RelativeLayout.LayoutParams params1 =
                                (RelativeLayout.LayoutParams) favoriteList.getLayoutParams();
                        int top = params1.topMargin;
                        int left = params1.leftMargin;
                        int right = params1.rightMargin;
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                (favoriteList.getWidth(), favoriteList.getHeight());
                        params.topMargin = top;
                        params.leftMargin = (MainActivity.userListNames.indexOf(b.getText()) * 280) + left;
                        params.rightMargin = right;
                        b.setLayoutParams(params);
                    }
                }
                updateUI();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.show();
    }

    public AlertDialog createNewListDialog(final Button b){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create a New List");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newListName = input.getText().toString().toLowerCase().replaceAll("\\s+","");
                if(newListName.trim().equals(""))
                    newListName = "untitledlist";
                MainActivity.userListNames.add(newListName);
                addUserListButton(newListName, b);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.show();
    }

    public AlertDialog createAddCompanyDialog() {
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
                    int i = MainActivity.allCompanies.indexOf(items[checkedItem].toString());
                    MainActivity.addUserListRow(
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
                                //MainActivity.setVisitStatus(currentCompany, 1);
                                MainActivity.setUserListVisitStatus(currentCompany, 1);
                            } else {
                                //MainActivity.setVisitStatus(currentCompany, 0);
                                MainActivity.setUserListVisitStatus(currentCompany, 0);
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
