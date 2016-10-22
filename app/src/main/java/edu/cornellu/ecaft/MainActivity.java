package edu.cornellu.ecaft;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import edu.cornellu.ecaft.DatabaseSchema.CompanyTable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ECaFT";
    private TabLayout mTabLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuOptions;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private ListView mDrawerList;
    private RelativeLayout mDrawerListLayout;
    /**
     * Database variables
     */
    private Context mContext;
    public static SQLiteDatabase mDatabase;
    /**
     * Fragments
     */
    private HomeFragment homeFragment;
    private MapFragment mapFragment;
    private InfoFragment infoFragment;
    private ChecklistFragment checklistFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        mDatabase = new DatabaseHelper(mContext).getWritableDatabase();

        homeFragment = new HomeFragment();
        mapFragment = new MapFragment();
        infoFragment = new InfoFragment();
        checklistFragment = new ChecklistFragment();


        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //          getSupportActionBar().setHomeButtonEnabled(true);

        mMenuOptions = getResources().getStringArray(R.array.menu_options);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mDrawerListLayout = (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        //  mDrawerListLayout = (RelativeLayout) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuOptions));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        //      mDrawerList.setSelection(0);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        mDrawerList.setItemChecked(0, true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;
        switch (position) {
            case 0: //Home fragment
                fragment = homeFragment;
                break;
            case 1:
                fragment = mapFragment;
                break;
            case 2:
                fragment = infoFragment;
                break;
            case 3:
                fragment = checklistFragment;
                break;
            default:
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerListLayout);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    /**
     * Database Methods
     */
    public static void deleteRow(String id) {
        mDatabase.delete(CompanyTable.NAME, CompanyTable.Cols.UUID + " = ?", new String[]{id});
    }


    public static boolean isInDatabase(String name) {
        Cursor c = mDatabase.query(CompanyTable.NAME, null, null, null, null, null, null);
        boolean inside = false;
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (name.equals(c.getString(c.getColumnIndex(CompanyTable.Cols.COMPANY_NAME)))) {
                    inside = true;
                    break;
                }
                c.moveToNext();
            }

        } finally {
            c.close();
        }
        return inside;
    }


    public static List<Integer> makeIsInDatabaseList(List<Company>
                                                             companies) {

        Cursor c = mDatabase.query(CompanyTable.NAME, null, null, null, null, null, null);
        List<Integer> compiledList = new ArrayList<>();
        int pos = 0;
        boolean inside = false;
        try {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                compiledList.add((companies.get(pos).name.equals(c.getString
                        (c.getColumnIndex(CompanyTable.Cols.COMPANY_NAME)))) ? 1 :
                        0);
                pos++;
                c.moveToNext();
            }
        } finally {
            c.close();
        }
        return compiledList;
    }


    public static void addRow(String currentCompanyUUID, String
            currentCompanyName) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.UUID, currentCompanyUUID);
        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompanyName);
        values.put(CompanyTable.Cols.VISITED, 0);
        mDatabase.insert(CompanyTable.NAME, null, values);
    }

    public static boolean isSaved(Company currentCompany) {

        mDatabase.execSQL("select " + CompanyTable.Cols.VISITED + " from " +
                CompanyTable.NAME + " where " + CompanyTable.Cols
                .COMPANY_NAME + "='" + currentCompany.name + "'");
        Cursor c = mDatabase.query(CompanyTable.NAME, null, null, null, null, null, null);
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (currentCompany.objectID.equals(c.getString(c.getColumnIndex(CompanyTable.Cols.UUID)))) {
                    return true;
                }
                c.moveToNext();
            }

        } finally {
            c.close();
        }

        return false;
    }

    public static boolean isVisited(String name) {
        Cursor c = mDatabase.query(CompanyTable.NAME, null, CompanyTable.Cols
                .UUID+ " = ?", new String[]{name}, null, null, null);
        try {
            c.moveToFirst();
            return c.getInt(c.getColumnIndex(CompanyTable.Cols.VISITED)) == 1;
        } finally {
            c.close();
        }
    }

    public static List<Integer> makeIsVisited() {
        List<Integer> compiledList = new ArrayList<>();

        Cursor c = mDatabase.query(CompanyTable.NAME, null, null, null, null,
                null, CompanyTable.Cols.COMPANY_NAME + " ASC");
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {

                compiledList.add(c.getInt(c.getColumnIndex(CompanyTable.Cols
                        .VISITED)));
                c.moveToNext();
            }
        } finally {
            c.close();
        }

        return compiledList;
    }


    public static List<String> makeSavedList() {
        List<String> compiledList = new ArrayList<>();

        Cursor c = mDatabase.query(CompanyTable.NAME,
                null, null, null, null, null, CompanyTable.Cols.COMPANY_NAME + " ASC");

        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {

              /*  ParseObject po = ParseApplication.getPOByID(c.getString(c
                        .getColumnIndex(CompanyTable.Cols.UUID)));

                Company com = new Company(po.getObjectId(),
                        po.getString(ParseApplication.COMPANY_NAME),
                        (ArrayList<String>) po.get(ParseApplication.COMPANY_MAJORS),
                        po.getParseFile(ParseApplication.COMPANY_LOGO)
                ); */
                String com = c.getString(c.getColumnIndex(CompanyTable.Cols
                        .UUID));
                compiledList.add(com);
                c.moveToNext();
            }

        } finally {
            c.close();
        }

        return compiledList;
    }

    private static ContentValues getContentValues(Company c, int visited) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.UUID, c.objectID);
        values.put(CompanyTable.Cols.COMPANY_NAME, c.name);
        values.put(CompanyTable.Cols.VISITED, visited);

        return values;
    }

    public static void setVisitStatus(ParseObject po, int visited) {
       // ParseObject po = ParseApplication.getPOByID(s);
        Company com = new Company(po.getObjectId(),
                po.getString(ParseApplication.COMPANY_NAME),
                po.getString(ParseApplication.COMPANY_TABLE),
                (ArrayList<String>) po.get(ParseApplication.COMPANY_MAJORS),
                po.getParseFile(ParseApplication.COMPANY_LOGO)
        );
        ContentValues values = getContentValues(com, visited);
        mDatabase.update(CompanyTable.NAME, values, CompanyTable.Cols.UUID +
                " = ?", new String[]{po.getObjectId()});
    }
}
