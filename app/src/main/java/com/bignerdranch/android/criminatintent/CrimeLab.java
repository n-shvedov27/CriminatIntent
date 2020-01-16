package com.bignerdranch.android.criminatintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab crimeLab;
    private static Context appContext;

    public List<Crime> getCrimes() {
        return crimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : crimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }

        return null;
    }

    private List<Crime> crimes;

    public static CrimeLab get(Context context) {
        if (crimeLab == null) {
            crimeLab = new CrimeLab(context);
        }

        return crimeLab;
    }

    private CrimeLab(Context context) {
        crimes = new ArrayList<>();
        appContext = context;

        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();

            crime.setTitle("crime: " + i);
            crime.setSolved(i % 2 == 0);
            crimes.add(crime);
        }
    }
}
