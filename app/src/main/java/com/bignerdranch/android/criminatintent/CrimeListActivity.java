package com.bignerdranch.android.criminatintent;



import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {
    private static final String TAG = "CrimeListActivity";
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
