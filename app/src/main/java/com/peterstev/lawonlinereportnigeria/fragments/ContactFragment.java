package com.peterstev.lawonlinereportnigeria.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.activities.MainActivity;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

/**
 * Created by Peterstev on 08/05/2018.
 * for LawOnlineReport
 */
public class ContactFragment extends Fragment {

    private Context context;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.contact_us, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        toolbar.setTitle("Contact Law Online");

        Button btSend = view.findViewById(R.id.contact_send);
        Button call1 = view.findViewById(R.id.contact_call_1);
        Button call2 = view.findViewById(R.id.contact_call_2);
        AppCompatSpinner spinner = view.findViewById(R.id.contact_spinner);
        AppCompatSpinner states = view.findViewById(R.id.contact_states);
        EditText etName = view.findViewById(R.id.contact_name);
        EditText etOccupation = view.findViewById(R.id.contact_occupation);
        EditText etTitle = view.findViewById(R.id.contact_title);
        EditText etDesc = view.findViewById(R.id.contact_description);

        call1.setOnClickListener(view1 -> makeCall("+2348069143664"));

        call2.setOnClickListener(view1 -> makeCall("+2348038466464"));

        btSend.setOnClickListener((onClickView) -> {
            String name = etName.getText().toString();
            String title = etTitle.getText().toString();
            String desc = etDesc.getText().toString();
            String occupation = etOccupation.getText().toString();
            String location = states.getSelectedItem().toString();
            String reason = spinner.getSelectedItem().toString();

            contactUs(spinner,name,title, desc, occupation,location, reason);

        });
    }

    private void makeCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestCallPermission();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    }

    private void requestCallPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

    private void contactUs(View spinner, String name, String title, String desc, String occupation, String location, String reason){
        if (reason.trim().equalsIgnoreCase("Select a Reason")) {
            Utils.snackBar(spinner, "Please select a reason");
        } else if (name.isEmpty() || title.isEmpty() || location.isEmpty() || desc.isEmpty()) {
            Utils.snackBar(spinner, "Please fill out all fields");
        } else {
            String email = "Hello i'm " + name + ",  a " + occupation + " mailing from " + location + "\n\n" + desc
                    + "\n\n" + "Sent from My " + Utils.getDeviceDetails();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            if (reason.trim().equalsIgnoreCase("Report An Issue")) {
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.lor_problem_email)});
            } else if (reason.trim().equalsIgnoreCase("Make A Request")) {
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.lor_feature_request_email)});
            }
            intent.putExtra(Intent.EXTRA_TEXT, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Utils.snackBar(spinner, "No Email Client Available");
            }
        }

    }
}
