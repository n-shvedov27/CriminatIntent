package com.bignerdranch.android.criminatintent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {
    private RecyclerView crimeRecyclerView;
    private CrimeAdapter adapter;

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView titleTextView;
        private TextView dateTextView;
        private CheckBox solvedCheckBox;
        private Crime crime;

        public CrimeHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            titleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_title_text_view);
            dateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_date_text_view);
            solvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        public void bindCrime(Crime crime) {
            this.crime = crime;

            titleTextView.setText(crime.getTitle());
            dateTextView.setText(crime.getDate().toString());
            solvedCheckBox.setChecked(crime.isSolved());
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
            startActivity(intent);
        }
    }


    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> crimes;

        public CrimeAdapter(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = crimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return crimes.size();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        crimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        List<Crime> crimes = crimeLab.getCrimes();

        if (adapter == null) {
            adapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
