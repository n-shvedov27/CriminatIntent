package com.bignerdranch.android.criminatintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    private Crime crime;
    private File photoFile;
    private EditText textField;
    private Button dateButton;
    private CheckBox solvedCheckBox;
    private Button reportButton;
    private Button suspectButton;
    private ImageButton photoButton;
    private ImageView photoView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(crime);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                crime.getDate()).toString();
        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                crime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                crime.setSuspect(suspect);
                suspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        textField = (EditText) v.findViewById(R.id.crime_title);
        textField.setText(crime.getTitle());
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();

                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());

                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        solvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        reportButton = (Button) v.findViewById(R.id.crime_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        suspectButton = (Button) v.findViewById(R.id.crime_suspect);
        suspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (crime.getSuspect() != null) {
            suspectButton.setText(crime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.setEnabled(false);
        }

        photoButton = (ImageButton) v.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent
                (MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = photoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        photoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            captureImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri uri = FileProvider.getUriForFile(
                    getActivity(),
                    getActivity().getApplicationContext()
                            .getPackageName() + ".provider", photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        photoView = (ImageView) v.findViewById(R.id.crime_photo);

        updatePhotoView();

        return v;
    }

    private void updateDate() {
        dateButton.setText(crime.getDate().toString());
    }

    private void updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }
    }
}
