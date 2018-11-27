package edu.ecaft;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Ashley on 1/20/2016.
 */
public class CompanyDetailsActivity extends AppCompatActivity {

    public String currentCompanyName;
    public String currentCompanyUUID;
    private Menu menu;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    protected int getLayoutResId() {
        return R.layout.company_details_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getActionBar();
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#800000")));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            Fragment fragment = new CompanyDetailsFragment();
            Intent i = getIntent();
            Bundle x = i.getExtras();

            currentCompanyName = x.getString(FirebaseApplication.COMPANY_NAME);
            currentCompanyUUID = x.getString(FirebaseApplication
                    .COMPANY_ID);


            fragment.setArguments(i.getExtras());
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_company_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString(FirebaseApplication.COMPANY_NAME,
                currentCompanyName);
        savedInstanceState.putString(FirebaseApplication.COMPANY_ID, currentCompanyUUID);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        Fragment fragment = new CompanyDetailsFragment();
        Intent i = getIntent();
        Bundle x = i.getExtras();

        currentCompanyName = x.getString(FirebaseApplication.COMPANY_NAME);
        currentCompanyUUID = x.getString(FirebaseApplication
                .COMPANY_ID);



        fragment.setArguments(i.getExtras());
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}

