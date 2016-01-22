package edu.cornell.ecaft;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.List;

/**
 * Created by Ashley on 11/8/2015.
 */
public class ChecklistFragment extends Fragment {

    private static final String TAG = "ECaFT";

    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;

    private SQLiteDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        database = MainActivity.mDatabase;



        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.checklist_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id.checklist_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateUI();

        getActivity().setTitle("Your Checklist");
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setLogo(R.drawable.black);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        List<Company> companies = MainActivity.makeSavedList();
        companyAdapter = new CompanyAdapter(companies);
        companyRecylerView.setAdapter(companyAdapter);
    }


    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mCompanyRL;
        public TextView mCompanyName;
        public ParseImageView mCompanyLogo;
        public CheckBox mCompanyVisited;
        public Company currentCompany;
        public SwipeLayout swipeLayout;
        public LinearLayout delete;

        public CompanyHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);

           swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
//add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.trash));

            swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle myBundle = new Bundle();
                    myBundle.putString(ParseApplication.COMPANY_OBJECT_ID, currentCompany.objectID);
                    myBundle.putString(ParseApplication.COMPANY_NAME, currentCompany.name);
                    myBundle.putStringArrayList(ParseApplication.COMPANY_MAJORS, currentCompany.majors);

                    Intent i = new Intent(getActivity(), CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);
                }
            });

            delete = (LinearLayout) itemView.findViewById(R.id.trash);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), R.string.unstar, Toast.LENGTH_SHORT).show();
                    MainActivity.deleteRow(currentCompany.objectID);
                    updateUI();
                }
            });


        /**    mCompanyRL = (RelativeLayout) itemView.findViewById(R.id.checklist_cardview);
            mCompanyRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle myBundle = new Bundle();
                    myBundle.putString(ParseApplication.COMPANY_OBJECT_ID, currentCompany.objectID);
                    myBundle.putString(ParseApplication.COMPANY_NAME, currentCompany.name);
                    myBundle.putStringArrayList(ParseApplication.COMPANY_MAJORS, currentCompany.majors);

                    Intent i = new Intent(getActivity(), CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);
                }
            });

         */

            mCompanyName = (TextView) itemView.findViewById(R.id.company_checklist_name);

        //    mCompanyLogo = (ParseImageView) itemView.findViewById(R.id.company_checklist_logo);

            mCompanyVisited = (CheckBox) itemView.findViewById(R.id.check_company);
            mCompanyVisited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCompanyVisited.isChecked()) {
                        Toast.makeText(getContext(), R.string.visited, Toast.LENGTH_SHORT).show();
                        MainActivity.setVisitStatus(currentCompany, 1);
                    } else {
                        MainActivity.setVisitStatus(currentCompany, 0);
                    }
                }
            });
        }
    }

    private class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
        public List<Company> companies;

        public CompanyAdapter(List<Company> companies) {
            this.companies = companies;
        }

        @Override
        public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.checklist_fragment_cardview, parent, false);
            return new CompanyHolder(view);
        }

        @Override
        public void onBindViewHolder(CompanyHolder holder, int position) {
            Company currentCompany = companies.get(position);
            holder.currentCompany = currentCompany;
            holder.mCompanyName.setText(currentCompany.name);
        //    holder.mCompanyLogo.setParseFile(currentCompany.logo);
        //    holder.mCompanyLogo.loadInBackground();

            int visitStatus = MainActivity.isVisited(currentCompany);
            holder.mCompanyVisited.setChecked(visitStatus == 1);

            Log.d(TAG, "Recycler made for position " + position);
        }

        @Override
        public int getItemCount() {
            return companies.size();
        }
    }
}
