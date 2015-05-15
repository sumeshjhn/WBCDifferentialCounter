package com.bohn.boomesh.wbcdifferentialcounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class HomeActivity extends AppCompatActivity {

    public interface OptionsItemSelectedListener {
        void onOptionsItemSelected(HomeActivityFragment.WBC pCellType);
    }

    private OptionsItemSelectedListener mOptionsItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState != null) {
            // be restored, no need to commit the fragment again
            return;
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new HomeActivityFragment()).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        HomeActivityFragment.WBC cellType;

        switch (id) {
            case R.id.action_reset_all:
                cellType = HomeActivityFragment.WBC.ALL;
                break;
            case R.id.action_reset_baso:
                cellType = HomeActivityFragment.WBC.BASO;
                break;
            case R.id.action_reset_eosino:
                cellType = HomeActivityFragment.WBC.EOSINE;
                break;
            case R.id.action_reset_mono:
                cellType = HomeActivityFragment.WBC.MONO;
                break;
            case R.id.action_reset_lympho:
                cellType = HomeActivityFragment.WBC.LYMPHO;
                break;
            case R.id.action_reset_neutro:
                cellType = HomeActivityFragment.WBC.NEUTRO;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        if (mOptionsItemSelectedListener != null) {
            mOptionsItemSelectedListener.onOptionsItemSelected(cellType);
        }

        return true;
    }

    public void setOptionsMenuListener(OptionsItemSelectedListener pOptionsItemSelectedListener) {
        mOptionsItemSelectedListener = pOptionsItemSelectedListener;
    }
}
