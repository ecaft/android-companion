package edu.ashleyxue.ecaft;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ashley on 11/8/2015.
 */
public class InfoFragment extends Fragment implements SearchView.OnCloseListener {

    private static final String TAG = "ECaFT";
    private static final String SAVED_LAYOUT_MANAGER = "Layout Manager";
    private StorageReference storageRef = FirebaseApplication
            .getStorageRef();

    private RecyclerView.LayoutManager layoutManagerSavedState;
    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;
    private View v;
    private List<FirebaseCompany> companies;
    private List<FirebaseCompany> companiesFilter;
    private SearchView searchView;
    private ListView lv;

    String[] majorOptions = {
            "Aerospace Engineering",
            "Atmospheric Science",
            "Biological Engineering",
            "Biomedical Engineering",
            "Biological and Environmental",
            "Chemical Engineering",
            "Civil Engineering",
            "Computer Science",
            "Electrical and Computer Engineering",
            "Engineering Management",
            "Engineering Physics",
            "Environmental Engineering",
            "Information Science",
            "Materials Science and Engineering",
            "Mechanical Engineering",
            "Operations Research and Information Engineering",
            "Systems Engineering"};

    String [] jobOptions = { "Co-op", "Full-time", "Internship", "Other"};
    boolean sponsorship = false;

    boolean[] checkedStatus = new boolean[majorOptions.length];
    ArrayList<Integer> userChoices = new ArrayList<>();
    ArrayList<String> companiesChecked = new ArrayList<>();

    public InfoFragment() {
        companies = new ArrayList<>(FirebaseApplication.getCompanies());
        companiesFilter = new ArrayList<>();
        companiesFilter.addAll(companies);
        companyAdapter = new CompanyAdapter(companiesFilter);
        Log.d("final", "instantiation: filter size: " + companiesFilter.size
                () + ", total size: " + companies.size());
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.info_fragment, container, false);

        Log.d("final", "info fragment oncreateview");


        companyRecylerView = (RecyclerView) v.findViewById(R.id
                .info_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager
                (getActivity()));
        companyRecylerView.setHasFixedSize(true);
        companyRecylerView.setAdapter(companyAdapter);

        updateUI();

        getActivity().setTitle("List Of Companies");
        setHasOptionsMenu(true);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

            inflater.inflate(R.menu.menu_main, menu);
            inflater.inflate(R.menu.menu_search, menu);
            inflater.inflate(R.menu.menu_filter, menu);

            MenuItem filterItem= menu.findItem(R.id.filterButton);
            Log.d("Filter Page", "Creating this fragment");
            final ArrayList<String> userChoices = new ArrayList<String> (FilterFragment.filterOptions);
            filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem menuItem){
                    FilterFragment frag = new FilterFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(((ViewGroup)(getView().getParent())).getId(), frag);
                    //transaction.replace(R.id.info_cardview, frag);
                    transaction.addToBackStack(null);
                    Log.d("Filter Page", "CALLING NEW FRAGMENT");
                    transaction.commit();

                    Log.d("FILTER PREP", userChoices.toString());
                    companyAdapter.filter(userChoices);
                    return true;
                }

              /*  @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AlertDialog.Builder opt = new AlertDialog.Builder(getContext());
                    opt.setTitle("Please Choose Major Filters");
                    opt.setMultiChoiceItems(majorOptions, checkedStatus, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                            if(isChecked){
                                if(!userChoices.contains(position)){
                                    userChoices.add(position);
                                    checkedStatus[position]= true;
                                }
                            }
                            else{
                                userChoices.remove(Integer.valueOf(position));
                            }
                        }
                    });
                    opt.setCancelable(false);
                    opt.setPositiveButton(getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            companiesChecked.clear();
                            for (int j = 0; j< userChoices.size(); j++){
                                if (!companiesChecked.contains(majorOptions[userChoices.get(j)]))
                                    companiesChecked.add(majorOptions[userChoices.get(j)]);

                            }
                            Log.d("PRINTING ITEM", companiesChecked.toString());

                            companyAdapter.filter(companiesChecked);

                        }
                    });
                    opt.setNegativeButton(getString(R.string.dismiss_label), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    });
                    opt.setNeutralButton(getString(R.string.clear_all_label), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            for (int i = 0; i< checkedStatus.length; i++){
                                checkedStatus[i] = false;
                                userChoices.clear();
                                companiesChecked.clear();
                            }
                            companyAdapter.filter(companiesChecked);
                        }
                    });
                    AlertDialog merp = opt.create();
                    merp.show();
                    Log.d("FILTER TEST", userChoices.toString());
                    return true;
                }; */
            });
            final MenuItem searchItem = menu.findItem(R.id.search);
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    Log.d("final", "search view attached");
                    for (int i = 0; i< checkedStatus.length; i++){
                        checkedStatus[i] = false;
                        userChoices.clear();
                        companiesChecked.clear();
                    }
                    companyAdapter.filter(companiesChecked);
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    Log.d("final", "search view detached");
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    companyAdapter.filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    companyAdapter.filter(newText);
                    return false;
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.search:
                //companyAdapter.doSearch();
                return true;
            case R.id.filterButton:
                return true;
            default:
                return false;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("final", "info fragment onresume");
        updateUI();
        MainActivity.navigationView.setCheckedItem(R.id.nav_companies);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (companyRecylerView != null) {
            outState.putParcelable(SAVED_LAYOUT_MANAGER, companyRecylerView.getLayoutManager()
                    .onSaveInstanceState());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState
                    .getParcelable(SAVED_LAYOUT_MANAGER);
            companyRecylerView.getLayoutManager().onRestoreInstanceState
                    (savedRecyclerLayoutState);
        }
    }

    private void updateUI() {
        Log.d("final", "info fragment updateUI");
        if (companies.size() == 0) {
            // companies list size should be 110 total
            companies = new ArrayList<>(FirebaseApplication.getCompanies());
            companiesFilter.clear();
            companiesFilter.addAll(companies);
        }

        companyAdapter.notifyDataSetChanged();

        Log.d("final", "filter size: " + companiesFilter.size() + ", total " +
                "size:" + companies.size());
    }

    @Override
    public boolean onClose() {
        Log.d("final", "info fragment onclose");

        return true;
    }

    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mCompanyRL;
        public TextView mCompanyName;
        public TextView mCompanyLocation;
        public ImageView mCompanyLogo;
        public ImageButton mCompanySave;
        public FirebaseCompany currentCompany;


        public CompanyHolder(View itemView) {
            super(itemView);
            mCompanyRL = (RelativeLayout) itemView.findViewById(R.id.info_cardview);
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
                    myBundle.putBoolean(FirebaseApplication.SHOW_NOTES, false);

                    Intent i = new Intent(getActivity(), CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);
                }
            });

            mCompanyName = (TextView) itemView.findViewById(R.id.company_name);

            mCompanyLocation = (TextView) itemView.findViewById(R.id
                    .company_table);

            mCompanyLogo = (ImageView) itemView.findViewById(R.id.company_logo);

            mCompanySave = (ImageButton) itemView.findViewById(R.id.save_company);
            mCompanySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                        // TODO: Snackbar instead of toast?
                        Toast.makeText(getContext(), R.string.star, Toast.LENGTH_SHORT).show();
                        // TODO: lol ic unfav and fav are swapped names
                        mCompanySave.setImageResource(R.drawable.ic_unfavorite);
                        MainActivity.addRow(currentCompany.id,
                                currentCompany.name);
                    } else {
                        Toast.makeText(getContext(), R.string.unstar, Toast.LENGTH_SHORT).show();
                        mCompanySave.setImageResource(R.drawable.ic_favorite);
                        MainActivity.deleteRow(currentCompany.id);
                    }
                }
            });

        }

    }

    private class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
        public List<FirebaseCompany> companiesFilter;

        public CompanyAdapter(List<FirebaseCompany> companies) {
            companiesFilter = companies;
        }

        @Override
        public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.info_fragment_cardview,
                    parent, false);
            return new CompanyHolder(view);
        }

        @Override
        public void onBindViewHolder(CompanyHolder holder, int position) {
            FirebaseCompany currentCompany = companiesFilter.get(position);
            holder.currentCompany = currentCompany;
            holder.mCompanyName.setText(currentCompany.name);
            holder.mCompanyLocation.setText("Table " + currentCompany.location);

            StorageReference path = storageRef.child("logos/" +
                    currentCompany.getId() + ".png");

            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(path)
                    .into(holder.mCompanyLogo);

            if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                holder.mCompanySave.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.mCompanySave.setImageResource(R.drawable.ic_unfavorite);
            }

            Log.d(TAG, "Recycler made for position " + position);
        }

        @Override
        public int getItemCount() {
            return companiesFilter.size();
        }

        public void filter(String text){
            companiesFilter.clear();
            if (!text.isEmpty()) {
                for (FirebaseCompany comp: companies){
                    if (comp.getName().toLowerCase().contains(text.toLowerCase()))
                        companiesFilter.add(comp);
                }
            } else {
                companiesFilter.addAll(companies);
            }
            Log.d("final", "SEARCH CALL: filter size: " + companiesFilter.size
                    () + ", total " +
                    "size:" + companies.size());
            notifyDataSetChanged();
        }
        public void filter(ArrayList<String> majors){
            Log.d("majors", majors.toString());
            companiesFilter.clear();
            if(majors.size()!=0){
                for (FirebaseCompany comp: companies){
                    for(String major: majors){
                        Log.d("PRINTING MAJOR", major);
                        if (comp.getMajors().contains(major) && !companiesFilter.contains(comp)) {
                            companiesFilter.add(comp);
                        }
                    }
                    if (comp.getMajors()== ""){
                        companiesFilter.add(comp);
                    }
                }
            }
            else{
                companiesFilter.addAll(companies);
            }
            Log.d("final", "FILTER CALL: filter size: " + companiesFilter.size
                    () + ", total " +
                    "size:" + companies.size());
            notifyDataSetChanged();
        }

    }

}
