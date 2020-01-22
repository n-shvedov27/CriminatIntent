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

    public void addCrime(Crime c) {
        crimes.add(c);
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

    }
}
