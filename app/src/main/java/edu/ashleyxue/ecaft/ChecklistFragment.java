package edu.ashleyxue.ecaft;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Ashley on 11/8/2015.
 */
public class ChecklistFragment extends Fragment {

    private static final String TAG = "ECaFT";
    private static final String STATE_KEY = "Recycler State";

    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;

    private View emptyView;

    private List<Integer> isVisitedList;
    private List<String> companies;
    private List<String> companyLocations;

    private Parcelable savedState;
    private Bundle savedBundle;

    private int swipedPosition;

    private boolean listButtonClicked = false;


    public ChecklistFragment() {
        isVisitedList = MainActivity.makeIsVisited();
        companies = MainActivity.makeSavedList();
        companyLocations = MainActivity.makeSavedList();
        companyAdapter = new CompanyAdapter(companies, companyLocations);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.checklist_fragment, container, false);
        final LinearLayout layout = (LinearLayout) v.findViewById(R.id.list_button);
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

        companyRecylerView = (RecyclerView) v.findViewById(R.id.checklist_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyRecylerView.setHasFixedSize(true);
        companyRecylerView.setAdapter(companyAdapter);

       // LinearLayout addCompanyLayout =
                //(LinearLayout) v.findViewById(R.id.checklist_addCompany_main);
        //RelativeLayout.LayoutParams addCompanyParams =
                //(RelativeLayout.LayoutParams) addCompanyLayout.getLayoutParams();
        //addCompanyParams.addRule(RelativeLayout.RIGHT_OF, R.id.checklist_recycler_view);
        //addCompanyLayout.setLayoutParams(addCompanyParams);

        emptyView = v.findViewById(R.id.list_fragment_empty_view);

        getActivity().setTitle("Your Favorites");

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
        isVisitedList = MainActivity.makeIsVisited();
    }

    private void updateSavedLists() {
        companies = MainActivity.makeSavedList();
        companyLocations = MainActivity.makeSavedList();
    }

    private void updateUI() {
        updateVisitedList();
        updateSavedLists();

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
