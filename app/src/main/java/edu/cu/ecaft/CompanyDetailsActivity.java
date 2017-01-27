package edu.cu.ecaft;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
            // getSupportActionBar().setLogo(R.drawable.divider);
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
        if (MainActivity.isInDatabase(currentCompanyName))
            menu.getItem(0).setIcon(R.drawable.ic_unfavorite);
        else
            menu.getItem(0).setIcon(R.drawable.ic_star_border_white_36dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.save_company:
                if (MainActivity.isInDatabase(currentCompanyName)) { //Then you want to remove on click
                    Toast.makeText(this, R.string.unstar, Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_star_border_white_36dp);
                    MainActivity.deleteRow(currentCompanyUUID);
                } else { //Not in database yet so add it to db
                    Toast.makeText(this, R.string.star, Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_unfavorite);
                    MainActivity.addRow(currentCompanyUUID,
                            currentCompanyName);
                }
        }
        return super.onOptionsItemSelected(item);
    }
}

