package edu.cu.ecaft;

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

    private Parcelable savedState;
    private Bundle savedBundle;

    private int swipedPosition;

    public ChecklistFragment() {
        isVisitedList = MainActivity.makeIsVisited();
        companies = MainActivity.makeSavedList();
        companyAdapter = new CompanyAdapter(companies);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.checklist_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id.checklist_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyRecylerView.setHasFixedSize(true);
        companyRecylerView.setAdapter(companyAdapter);

        emptyView = v.findViewById(R.id.list_fragment_empty_view);

        //updateUI();

        //  if (companies.size() == 0) {
        //     emptyView.setVisibility(View.VISIBLE);
        //    companyRecylerView.setVisibility(View.INVISIBLE);
        // }

        getActivity().setTitle("Your Checklist");

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
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
    }

    private void updateUI() {
        updateVisitedList();
        updateSavedLists();

        companyAdapter = new CompanyAdapter(companies);
        companyRecylerView.setAdapter(companyAdapter);


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        savedState = companyRecylerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_KEY, savedState);
    }


    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mCompanyRL;
        public TextView mCompanyName;
        public int currentPosition;
        public CheckBox mCompanyVisited;
        public FirebaseCompany currentPOCompany;
        //    public SwipeLayout swipeLayout;
        public LinearLayout delete;
        public String currentCompanyName;

        public CompanyHolder(View itemView) {
            super(itemView);
            //swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);

            //      swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //     swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.
            //            findViewById(R.id.trash));

            //      swipeLayout.getSurfaceView().setOnClickListener(new View
            //             .OnClickListener() {
            //         @Override
            //        public void onClick(View v) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Bundle myBundle = new Bundle();

                    myBundle.putString(FirebaseApplication.COMPANY_ID,
                            currentPOCompany.id);
                    myBundle.putString(FirebaseApplication.COMPANY_NAME,
                            currentPOCompany.name);
                    myBundle.putString(FirebaseApplication.COMPANY_MAJORS,
                            currentPOCompany.majors);
                    myBundle.putString(FirebaseApplication.COMPANY_TABLE,
                            currentPOCompany.location);

               /*     myBundle.putString(FirebaseApplication.COMPANY_ID,
                            currentPOCompany.getObjectId());
                    myBundle.putString(FirebaseApplication.COMPANY_NAME,
                            currentPOCompany.getString(FirebaseApplication
                                    .COMPANY_NAME));
                    myBundle.putStringArrayList(FirebaseApplication
                                    .COMPANY_MAJORS,
                            (ArrayList<String>) currentPOCompany.get
                                    (FirebaseApplication
                                    .COMPANY_MAJORS));
                    myBundle.putString(FirebaseApplication.COMPANY_TABLE,
                            currentPOCompany.getString(FirebaseApplication
                                    .COMPANY_TABLE)); */

                    Intent i = new Intent(getActivity(),
                            CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);
                }
            });


            //TODO: only one swipe layout at a time...
/*
            delete = (LinearLayout) itemView.findViewById(R.id.trash);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), R.string.unstar,
                            Toast.LENGTH_SHORT).show();
                    MainActivity.deleteRow(currentPOCompany.getString
                            (FirebaseApplication.COMPANY_ID));
                    companyAdapter.delete(currentPosition);
                    updateSavedLists();
                    updateVisitedList();
                }
            });
*/
            mCompanyName = (TextView) itemView.findViewById(
                    R.id.company_checklist_name);

            mCompanyVisited = (CheckBox) itemView.findViewById(
                    R.id.check_company);
            mCompanyVisited.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCompanyVisited.isChecked()) {
                                Toast.makeText(getContext(), R.string.visited,
                                        Toast.LENGTH_SHORT).show();
                                MainActivity.setVisitStatus(currentPOCompany, 1);
                               // updateVisitedList();
                            } else {
                                MainActivity.setVisitStatus(currentPOCompany, 0);
                                isVisitedList.set(currentPosition, 0);

                                //   updateVisitedList();
                            }
                        }
                    }

            );
        }
    }

    private class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
        public List<String> companies;
        public FirebaseCompany fc;
        public CompanyAdapter(List<String> companies) {
            this.companies = companies;
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

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("companies").child(id);

            databaseReference.orderByKey().addValueEventListener(new ValueEventListener
                    () {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    fc = dataSnapshot.getValue(FirebaseCompany
                            .class);
                    holder.currentPOCompany = fc;

                    holder.currentCompanyName = fc.name;
                    holder.mCompanyName.setText(fc.name);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("test", "oncancel");
                }
            });

            holder.currentPosition = position;
 //           holder.currentCompanyName = currentCompany;
   //         holder.mCompanyName.setText(currentCompany);
//            holder.currentPOCompany = po;
//            holder.currentPosition = position;
//            holder.currentCompanyName = po.getString(FirebaseApplication
//                    .COMPANY_NAME);
//            holder.mCompanyName.setText(po.getString(FirebaseApplication
//                    .COMPANY_NAME));
            holder.mCompanyVisited.setChecked(isVisitedList.get(position) == 1);
           // holder.swipeLayout.close();

            Log.d(TAG, "Recycler made for position " + position);
        }

        //     public void onC

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
