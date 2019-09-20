package com.keiken.view.fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.keiken.R;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;


public class ReportUserDialogFragment extends DialogFragment {
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStatere)
    {

        final View view = inflater.inflate(R.layout.fragment_report_user, container, false);
        final String ID_PROFILE = getArguments().getString("id");



        final RadioGroup reportReasons = view.findViewById(R.id.report_reasons);
        final Button reportButton = view.findViewById(R.id.report_button);
        final EditText edit = view.findViewById(R.id.report_edit);



        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int  selectedId = reportReasons.getCheckedRadioButtonId();

                RadioButton reason = view.findViewById(selectedId);


                if (!edit.getText().toString().equals(""))  {


                    try {


                        //create report
                        final Map<String, Object> reportDb = new HashMap<>();
                        reportDb.put("ID_REPORTED_USER", ID_PROFILE);
                        reportDb.put("ID_REPORTING_USER", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reportDb.put("reason", reason.getText().toString());
                        reportDb.put("description", edit.getText().toString());


                            db.collection("report").add(reportDb).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Toast.makeText(getContext(),R.string.report_sent, Toast.LENGTH_LONG).show();
                                    getDialog().dismiss();

                                }
                            });
                    }
                    catch (Exception e) {Toast.makeText(getContext(),"Devi scegliere una motivazione.", Toast.LENGTH_LONG).show();}


                }
                else Toast.makeText(getContext(),"Devi inserire una descrizione.", Toast.LENGTH_LONG).show();

            }
        });

        return view;
    }

    public static ReportUserDialogFragment newInstance(String id) {
        ReportUserDialogFragment f = new ReportUserDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }
}