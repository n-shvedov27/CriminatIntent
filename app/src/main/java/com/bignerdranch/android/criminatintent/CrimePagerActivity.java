package com.bignerdranch.android.criminatintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends FragmentActivity {
    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    private ViewPager viewPager;
    private List<Crime> crimes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        viewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        crimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();

        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = crimes.get(position);

                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });

        for (int i=0; i<crimes.size(); i++) {
            if (crimes.get(i).getId().equals(crimeId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }


    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
}
