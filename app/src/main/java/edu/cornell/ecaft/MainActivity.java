package edu.cornell.ecaft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.ecaft.DatabaseSchema.CompanyTable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ECaFT";
    private TabLayout mTabLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuOptions;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private ListView mDrawerList;
    private Fragment frag;

    /**
     * Database variables
     */
    private Context mContext;
    public static SQLiteDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        mDatabase = new DatabaseHelper(mContext).getWritableDatabase();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();

        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        TabLayout.Tab homeTab = mTabLayout.newTab();
        homeTab.setIcon(R.mipmap.ic_home_white_36dp);

        TabLayout.Tab mapTab = mTabLayout.newTab();
        mapTab.setIcon(R.mipmap.ic_map_white_36dp);

        TabLayout.Tab infoTab = mTabLayout.newTab();
        infoTab.setIcon(R.mipmap.ic_info_white_36dp);

        TabLayout.Tab checklistTab = mTabLayout.newTab();
        checklistTab.setIcon(R.mipmap.ic_list_white_36dp);

        mTabLayout.addTab(homeTab, 0);
        mTabLayout.addTab(mapTab, 1);
        mTabLayout.addTab(infoTab, 2);
        mTabLayout.addTab(checklistTab, 3);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                Log.d(TAG, "Current tab position is " + position);
                switch (position) {
                    case 0:
                        frag = new HomeFragment();
                        Log.d(TAG, "Home fragment made upon clicking position " + position);
                        break;
                    case 1:
                        frag = new MapFragment();
                        Log.d(TAG, "Map fragment made upon clicking position " + position);
                        break;
                    case 2:
                        frag = new InfoFragment();
                        Log.d(TAG, "Info fragment made upon clicking position " + position);
                        break;
                    case 3:
                        frag = new ChecklistFragment();
                        Log.d(TAG, "Checklist fragment made upon clicking position " + position);
                        break;

                }
                FragmentManager manager = getSupportFragmentManager();
                if (manager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                    manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, frag).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //TODO: replace the fragment with a newly started one upon clicking the same tab
            }
        });

        //TODO: make it so that pressing android back changes the selected tab accordingly

        mMenuOptions = getResources().getStringArray(R.array.menu_options);
        /** mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
         mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
         mDrawerList = (ListView) findViewById(R.id.left_drawer);

         mDrawerList.setAdapter(new ArrayAdapter<String>(this,
         R.layout.drawer_list_item, mMenuOptions));
         mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

         mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
         R.string.drawer_open, R.string.drawer_close) {
        @Override public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        invalidateOptionsMenu();
        }

        @Override public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);

        }
        };
         mDrawerLayout.setDrawerListener(mDrawerToggle);
         */

        /**     FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();

        }
        }); */
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

    public static void addRow(Company currentCompany) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.UUID, currentCompany.objectID);
        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompany.name);
        values.put(CompanyTable.Cols.VISITED, 0);
        mDatabase.insert(CompanyTable.NAME, null, values);
    }

    public static boolean isSaved(Company currentCompany) {

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

    public static int isVisited(Company currentCompany) {
        Cursor c = mDatabase.query(CompanyTable.NAME, null, null, null, null, null, null);
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (currentCompany.objectID.equals(c.getString(c.getColumnIndex(CompanyTable.Cols.UUID)))) {
                    if (c.getInt(c.getColumnIndex(CompanyTable.Cols.VISITED)) == 1) {
                        return 1;
                    }
                }
                c.moveToNext();
            }

        } finally {
            c.close();
        }

        return 0;
    }

    public static List<Company> makeSavedList() {
        List<Company> compiledList = new ArrayList<>();

        Cursor c = mDatabase.query(CompanyTable.NAME,
                null, null, null, null, null, null);

        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {

                ParseObject po = ParseApplication.getPOByID(c.getString(c.getColumnIndex(CompanyTable.Cols.UUID)));

                Company com = new Company(po.getObjectId(),
                        po.getString(ParseApplication.COMPANY_NAME),
                        (ArrayList<String>) po.get(ParseApplication.COMPANY_MAJORS),
                        po.getParseFile(ParseApplication.COMPANY_LOGO)
                );
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

    public static void setVisitStatus(Company c, int visited) {
        ContentValues values = getContentValues(c, visited);
        mDatabase.update(CompanyTable.NAME, values, CompanyTable.Cols.UUID + " = ?", new String[]{c.objectID});
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
        switch (item.getItemId()) {
            case android.R.id.home:
                //     if (mDrawerToggle.onOptionsItemSelected(item))
                //         return true;
                //     else
                onBackPressed();

                //noinspection SimplifiableIfStatement
                // case R.id.action_settings:
                //   return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {

    }

    public static SQLiteDatabase getDb() {
        return mDatabase;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //       mDrawerToggle.syncState();
    }

    /*
    private classes
     */


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            //view.setBackgroundColor(getResources().getColor(R.color.hot_color));
            selectItem(position);
        }
    }


}
