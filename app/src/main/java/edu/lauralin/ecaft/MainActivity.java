package edu.lauralin.ecaft;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.database.DatabaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.lauralin.ecaft.DatabaseSchema.CompanyTable;

public class MainActivity extends AppCompatActivity  implements SearchView
        .OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener   {

    private static final String TAG = "ECaFT";
    private TabLayout mTabLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuOptions;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private ListView mDrawerList;
    private RelativeLayout mDrawerListLayout;
    private boolean searching;
    private DrawerLayout drawer;

    public static ArrayList<String> pictures;

    //public static NavigationView navigationView;
    public static BottomNavigationView bottomNavigationView;
    public static NavigationView navigationView;
    public static List<String> allCompanies = new ArrayList<String>();
    public static List<String> allCompanyIds = new ArrayList<String>();
    /**
     * Database variables
     */
    public static Context mContext;
    public static SQLiteDatabase mDatabase;
    public static int currentUserList = 0;
    public static List<String> userListNames = new ArrayList<String>(){{
        add("companies");
    }};

    public static SQLiteDatabase picDatabase;
    /**
     * Fragments
     */
    private HomeFragment homeFragment;
    private MapFragment mapFragment;
    private InfoFragment infoFragment;
    private FilterFragment filterFragment;
    private ChecklistFragment checklistFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.d("testingtesting", FirebaseDatabase.getInstance().getApp().getName());
        mContext = getApplicationContext();
        mDatabase = new DatabaseHelper(mContext).getWritableDatabase();

        PicDatabaseHelper helper = new PicDatabaseHelper(mContext);
        picDatabase = helper.getWritableDatabase();
        Log.d("picDatabase", picDatabase.toString());

        homeFragment = new HomeFragment();
        mapFragment = new MapFragment();
        infoFragment = new InfoFragment();
        filterFragment = new FilterFragment();
        checklistFragment = new ChecklistFragment();

       // View drawer = findViewById(R.id.activity_main);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pictures = new ArrayList<String>();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        /*bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);*/

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        //          getSupportActionBar().setHomeButtonEnabled(true);
//
//        mMenuOptions = getResources().getStringArray(R.array.menu_options);
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
//        mDrawerListLayout = (RelativeLayout) findViewById(R.id.left_drawer);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
//        //  mDrawerListLayout = (RelativeLayout) findViewById(R.id.left_drawer);
//
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mMenuOptions));
//        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView parent, View view, int position, long id) {
//                selectItem(position);
//            }
//        });
////
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                R.string.drawer_open, R.string.drawer_close);
//        mDrawerLayout.setDrawerListener(mDrawerToggle);


        //      mDrawerList.setSelection(0);
        getSupportFragmentManager().beginTransaction().replace(R.id
                .content_frame, new HomeFragment()).commit();
        //bottomNavigationView.setSelectedItemId(R.id.nav_home);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = homeFragment;
        } else if (id == R.id.nav_map) {
            fragment = mapFragment;
        } else if (id == R.id.nav_companies){
            fragment = infoFragment;
        } else if (id == R.id.nav_checklist) {
            fragment = checklistFragment;
        } else {
            Log.d("ECaFT", "Cannot select existing menu option.");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                fragment).addToBackStack(null)
                .commit();

        //bottomNavigationView.setSelectedItemId(id);
        navigationView.setCheckedItem(id);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       // mDrawerToggle.onConfigurationChanged(newConfig);
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
                searching= true;
                break;
            case 3:
                fragment = checklistFragment;
                break;
            default:
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment)
        .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerListLayout);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
     //   mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }


    /**
     * Database Methods
     */
    public static void deleteRow(String id) {
        mDatabase.delete(CompanyTable.NAME, CompanyTable.Cols.ID + " = ?", new String[]{id});
    }


    public static void deleteUserListRow(String id, int i){
        //mDatabase.execSQL("DELETE FROM "+ MainActivity.userListNames.get(i)
          //      +" WHERE " + CompanyTable.Cols.COMPANY_NAME + " = "+name);
        mDatabase.delete(MainActivity.userListNames.get(i),
                CompanyTable.Cols.ID + " = ?",new String[]{id});
    }

    public static boolean isInUserList(String name, int i){
        //String s = DatabaseUtils.stringForQuery(mDatabase,"SELECT * FROM " + MainActivity.userListNames.get(i) + " WHERE "
         //   + CompanyTable.Cols.COMPANY_NAME + " = " + name,null);
        Cursor c = mDatabase.query(MainActivity.userListNames.get(i), null,
                null, null, null, null, null);
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

    public static void addRow(String currentCompanyUUID, String
            currentCompanyName) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.ID, currentCompanyUUID);
        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompanyName);
        values.put(CompanyTable.Cols.VISITED, 0);
        values.put(CompanyTable.Cols.NOTE, "");
        mDatabase.insert(CompanyTable.NAME, null, values);
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
                null, null, null, null, null, CompanyTable.Cols.ID + " ASC");
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String com = c.getString(c.getColumnIndex(CompanyTable.Cols
                        .ID));
                compiledList.add(com);
                c.moveToNext();
            }
        } finally {
            c.close();
        }
        return compiledList;
    }


    public static void setVisitStatus(FirebaseCompany po, int visited){
        String str = "update " + CompanyTable.NAME + " set " + CompanyTable
                .Cols.VISITED + " = " + visited + " where " + CompanyTable
                .Cols.ID + " = \"" + po.getId() + "\"";
        mDatabase.execSQL(str);
    }

    public static void saveNote(String id, String note) {
        String str = "update " + CompanyTable.NAME + " set " + CompanyTable
                .Cols.NOTE + " = \"" + note + "\" where " + CompanyTable
                .Cols.ID + " = \"" + id + "\"";
        mDatabase.execSQL(str);
    } 

    public static String getNote(String id) {
        Cursor c = mDatabase.query(CompanyTable.NAME,
                null, null, null, null, null, CompanyTable.Cols.ID + " ASC");

        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String com = c.getString(c.getColumnIndex(CompanyTable.Cols
                        .ID));
                if (com.equals(id))
                    return c.getString(c.getColumnIndex(CompanyTable.Cols
                            .NOTE));
                c.moveToNext();
            }
        } finally {
            c.close();
        }
        return "";
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }


    public static void addUserListRow(String currentCompanyUUID, String
            currentCompanyName) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.ID, currentCompanyUUID);
        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompanyName);
        values.put(CompanyTable.Cols.VISITED, 0);
        values.put(CompanyTable.Cols.NOTE, "");
        mDatabase.insert(MainActivity.userListNames.get(
                MainActivity.currentUserList), null, values);
    }

    public static void addUserListRowDetails(String currentCompanyUUID, String
            currentCompanyName, int i) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.ID, currentCompanyUUID);
        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompanyName);
        values.put(CompanyTable.Cols.VISITED, 0);
        values.put(CompanyTable.Cols.NOTE, "");
        mDatabase.insert(MainActivity.userListNames.get(
                i), null, values);
    }



    public static List<Integer> makeUserListIsVisited() {
        List<Integer> compiledList = new ArrayList<>();
        Cursor c = mDatabase.query(MainActivity.userListNames.get(
                MainActivity.currentUserList), null, null, null, null,
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


    public static List<String> makeUserListSavedList() {
        List<String> compiledList = new ArrayList<>();
        Cursor c = mDatabase.query(MainActivity.userListNames.get(
                MainActivity.currentUserList), null, null, null,
                null, null, CompanyTable.Cols.ID + " ASC");
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String com = c.getString(c.getColumnIndex(CompanyTable.Cols
                        .ID));
                compiledList.add(com);
                c.moveToNext();
            }
        } finally {
            c.close();
        }
        return compiledList;
    }


    public static void setUserListVisitStatus(FirebaseCompany po, int visited){
        String str = "update " + MainActivity.userListNames.get(
                MainActivity.currentUserList) + " set " + CompanyTable
                .Cols.VISITED + " = " + visited + " where " + CompanyTable
                .Cols.ID + " = \"" + po.getId() + "\"";
        mDatabase.execSQL(str);
    }

    public static List<String> getTables(){
        for(String s : userListNames) {
            mDatabase.delete(MainActivity.userListNames.get(0),
                    CompanyTable.Cols.ID + " = ?", new String[]{s});
        }
        List<String> tables = new ArrayList<String>();
         Cursor c = mDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table' " +
                 "AND name!='android_metadata' ",null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tables.add(c.getString(0));
                c.moveToNext();
            }
        }
        return tables;
    }


    public static void addPicRow(String companyName, String fileName){
        ContentValues values = new ContentValues();
        int key = (int)(System.currentTimeMillis()/1000)%1000000000;
        values.put(PicDatabaseSchema.CompanyTable._ID, key);
        values.put(PicDatabaseSchema.CompanyTable.COMPANY_NAME, companyName);
        values.put(PicDatabaseSchema.CompanyTable.PICFILES, fileName);
        picDatabase.insert(PicDatabaseSchema.CompanyTable.NAME, null, values);
        Log.d("picDatabase", ""+fileName+" added");
    }

    public static void deletePicRow(String file) {
        picDatabase.delete(PicDatabaseSchema.CompanyTable.NAME,
                PicDatabaseSchema.CompanyTable.PICFILES + " = ?", new String[]{file});
    }

    public static boolean isInPicDatabase(String name) {
        Cursor c = picDatabase.query(CompanyTable.NAME, null, null, null, null, null, null);
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

    public static void deletePicDatabase() {
        picDatabase.execSQL("DROP TABLE " + PicDatabaseSchema.CompanyTable.NAME);
    }

}
