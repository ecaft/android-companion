package edu.lauralin.ecaft;

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
import java.util.HashMap;
import java.util.List;

import android.util.LruCache;


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
    static HashMap<Integer, List<String>> userChoices = new HashMap<Integer, List<String>>();
    static HashMap<Integer, boolean []> prevFilterOptions = new HashMap<Integer, boolean[]>();

    private LruCache<String, ImageView> cache;

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
    ArrayList<String> companiesChecked = new ArrayList<>();

    public InfoFragment() {
        companies = new ArrayList<>(FirebaseApplication.getCompanies());
        companiesFilter = new ArrayList<>();
        companiesFilter.addAll(companies);

        companyAdapter = new CompanyAdapter(companiesFilter);

        cache = new LruCache<>(200);

        //Log.d("final", "instantiation: filter size: " + companiesFilter.size
          //      () + ", total size: " + companies.size());
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
            filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem menuItem){
                    FilterFragment frag = new FilterFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(((ViewGroup)(getView().getParent())).getId(), frag);
                    transaction.addToBackStack(null);
                    Log.d("Filter Page", "CALLING NEW FRAGMENT");
                    transaction.commit();

                    return true;
                }
            });
            final MenuItem searchItem = menu.findItem(R.id.search);
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

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
        userChoices = new HashMap<Integer, List<String>> (FilterFragment.filterOptions);
        prevFilterOptions = FilterFragment.mChildCheckStates;
        Log.d("FILTER PREP", userChoices.toString());
        companyAdapter.filter(userChoices);
        Log.d("final", "info fragment onresume");
        updateUI();
        //MainActivity.bottomNavigationView.setSelectedItemId(R.id.nav_companies);
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

        //Log.d("zxcv", companies.toString());
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

        public ImageView mCompanyBackground;


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
                    /*
                    myBundle.putBoolean(FirebaseApplication.COMPANY_OPTCPT,
                            currentCompany.optcpt);
                    myBundle.putBoolean(FirebaseApplication.COMPANY_SPONSOR,
                            currentCompany.sponsor);
                    */

                    myBundle.putString(FirebaseApplication.COMPANY_OPTCPT,
                            currentCompany.optcpt);
                    myBundle.putString(FirebaseApplication.COMPANY_SPONSOR,
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

            mCompanyBackground = (ImageView) itemView.findViewById(R.id.company_background);

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

            StorageReference background_image_path = storageRef.child("logos/" +
                    currentCompany.getId() + "Background.png");

            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(path)
                    .into(holder.mCompanyLogo);
            if(cache.get(holder.mCompanyName.toString()) == null) {
                Glide.with(getContext())
                        .using(new FirebaseImageLoader())
                        .load(background_image_path)
                        .into(holder.mCompanyBackground);
                cache.put(holder.mCompanyName.toString(), holder.mCompanyBackground);
            } else {
                holder.mCompanyBackground = cache.get(holder.mCompanyName.toString());
            }

            if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                holder.mCompanySave.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.mCompanySave.setImageResource(R.drawable.ic_unfavorite);
            }


            holder.mCompanyRL.getBackground().setAlpha(170);
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
        public void filter(HashMap<Integer, List<String>> filterChoices){
            companiesFilter.clear();
            List<FirebaseCompany> majorFilters = new ArrayList<FirebaseCompany>();
            List<FirebaseCompany> jobFilters = new ArrayList<FirebaseCompany>();
            List<FirebaseCompany> sponsorshipFilters = new ArrayList<FirebaseCompany>();

            List<FirebaseCompany> defaultAll = new ArrayList<>(FirebaseApplication.getCompanies());
            for (Integer i : filterChoices.keySet()) {
                if (i.compareTo(new Integer(0)) == 0) {
                    List<String> majors = filterChoices.get(i);
                    if (majors.size() != 0) {
                        if (majors.get(0).compareTo("All Majors") == 0){
                            majorFilters.addAll(companies);
                            break;
                        }
                        for (FirebaseCompany comp : companies) {
                            for (String major : majors) {
                                if (comp.getMajors().contains(major) && !majorFilters.contains(comp)) {
                                    majorFilters.add(comp);
                                }
                            }
                            if (comp.getMajors() == "") {
                                majorFilters.add(comp);
                            }
                        }
                    } else {
                        majorFilters.addAll(companies);
                    }
                }
                if (i.compareTo(new Integer(1)) == 0) {
                    List<String> jobTypes = filterChoices.get(i);
                    if (jobTypes.size() != 0) {
                        if (jobTypes.get(0).compareTo("All Types") == 0){
                            jobFilters.addAll(companies);
                            break;
                        }
                        for (FirebaseCompany comp : companies) {
                            for (String type : jobTypes) {
                                if (comp.getJobtypes().contains(type) && !jobFilters.contains(comp)) {
                                    jobFilters.add(comp);
                                }
                            }
                            if (comp.getJobtypes() == "") {
                                jobFilters.add(comp);
                            }
                        }
                    } else {
                        jobFilters.addAll(companies);
                    }
                }
                /*if (i.compareTo(new Integer(1)) == 0) {
                    List<String> jobTypes = filterChoices.get(i);
                    if (jobTypes.size() != 0) {
                        for (FirebaseCompany comp : companies) {
                            for (String type : jobTypes) {
                                if (comp.getJobtypes().contains(type) && !sponsorshipFilters.contains(comp)) {
                                    sponsorshipFilters.add(comp);
                                }
                            }
                            if (comp.getJobtypes() == "") {
                                sponsorshipFilters.add(comp);
                            }
                        }
                    } else {
                        sponsorshipFilters.addAll(companies);
                    }
                } */

            }
            if (majorFilters.size() > 0){
                defaultAll.retainAll(majorFilters) ;
                Log.d("MAJOR FILTERS SIZE", Integer.toString(majorFilters.size()));
            }
            if (jobFilters.size() > 0 ){
                defaultAll.retainAll(jobFilters);
                Log.d("jobFilters SIZE", Integer.toString(jobFilters.size()));
            }
            if (sponsorshipFilters.size()> 0){
                defaultAll.retainAll(sponsorshipFilters);
                Log.d("sponsorshipFilters SIZE", Integer.toString(sponsorshipFilters.size()));
            }
            companiesFilter = defaultAll;
            notifyDataSetChanged();
        }

    }

}
