package il.co.diamed.com.form.data_objects;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.calibration.res.DeviceDialogFragment;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class DeviceEditorFragment extends DialogFragment implements DeviceDialogFragment.OnLocationSelected {
    //private final String TAG = "DeviceDialogFragment";
    ClassApplication application;
    DatabaseProvider provider;
    Bundle bundle = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            application = (ClassApplication) getActivity().getApplication();
            provider = application.getDatabaseProvider(getContext());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        View v = inflater.inflate(R.layout.fragment_device_editor, container, false);
        AtomicLong ins = new AtomicLong();
        AtomicLong eow = new AtomicLong();
        this.setCancelable(false);

        bundle = getArguments();

        //get parts serial list


        Switch warranty_switch = v.findViewById(R.id.swEditorWarranty);
        TextView install_date = v.findViewById(R.id.etEditorInsDate);
        TextView endofwarranty_date = v.findViewById(R.id.etEditorEOWdate);
        ins.set(bundle.getLong("ins_date"));
        eow.set(bundle.getLong("eow_date"));
        Calendar sdf = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd / MM / YYYY", Locale.getDefault());
        sdf.setTimeInMillis(ins.get());
        install_date.setText(format.format(sdf.getTime()));
        sdf.setTimeInMillis(eow.get());
        endofwarranty_date.setText(format.format(sdf.getTime()));

        v.findViewById(R.id.btneditorCommentSave).setVisibility(View.GONE);
        v.findViewById(R.id.etEditorCommment).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.tvEditorCommment)).setText(bundle.getString("comments"));
        warranty_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                v.findViewById(R.id.btnEditor_changeEow).setVisibility(View.GONE);
                v.findViewById(R.id.etEditorEOWdate).setVisibility(View.GONE);
                v.findViewById(R.id.tvEditorEOWdate).setVisibility(View.GONE);
            } else {
                v.findViewById(R.id.btnEditor_changeEow).setVisibility(View.VISIBLE);
                v.findViewById(R.id.etEditorEOWdate).setVisibility(View.VISIBLE);
                v.findViewById(R.id.tvEditorEOWdate).setVisibility(View.VISIBLE);

            }
        });
        warranty_switch.setChecked(bundle.getBoolean("under_warranty"));

        DatePickerDialog.OnDateSetListener eowdate = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            sdf.set(Calendar.YEAR, year);
            sdf.set(Calendar.MONTH, monthOfYear);
            sdf.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            endofwarranty_date.setText(format.format(sdf.getTime()));
            eow.set(sdf.getTimeInMillis());
        };

        DatePickerDialog.OnDateSetListener insdate = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            sdf.set(Calendar.YEAR, year);
            sdf.set(Calendar.MONTH, monthOfYear);
            sdf.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            install_date.setText(format.format(sdf.getTime()));
            ins.set(sdf.getTimeInMillis());
        };

        v.findViewById(R.id.btnEditor_changeIns).setOnClickListener(v13 -> {
            if (getContext() != null)
                new DatePickerDialog(getContext(), insdate, sdf
                        .get(Calendar.YEAR), sdf.get(Calendar.MONTH),
                        sdf.get(Calendar.DAY_OF_MONTH)).show();
        });

        v.findViewById(R.id.btnEditor_changeEow).setOnClickListener(v12 -> {
            if (getContext() != null)
                new DatePickerDialog(getContext(), eowdate, sdf
                        .get(Calendar.YEAR), sdf.get(Calendar.MONTH),
                        sdf.get(Calendar.DAY_OF_MONTH)).show();
        });

        v.findViewById(R.id.insertSubmit).setOnClickListener(v1 -> {
            if (eow.get() - ins.get() < 0)
                Toast.makeText(getContext(), "גמר אחריות לא יכול להיות לפני תאריך ההתקנה", Toast.LENGTH_SHORT).show();
            else {
                showComment();
                String serial = bundle.getString("serial");
                String codeName = bundle.getString("type");
                String comments = ((TextView) v.findViewById(R.id.tvEditorCommment)).getText().toString();
                provider.updateDevice(serial, codeName, ins.get(), eow.get(), warranty_switch.isChecked(), comments);
                dismiss();
            }
        });


        v.findViewById(R.id.insertCancel).setOnClickListener(v1 -> dismiss());

        v.findViewById(R.id.editorMove).setOnClickListener(v1 -> {
            if (getFragmentManager() != null) {
                DeviceDialogFragment newFragment = new DeviceDialogFragment();
                newFragment.setTargetFragment(this, 0);
                newFragment.show(getFragmentManager(), "dialog");
            }
            dismiss();

        });

        v.findViewById(R.id.editorDelete).setOnClickListener(v1 -> {
            if (getContext() != null) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setMessage("מחק מכשיר?");
                alertBuilder.setPositiveButton("כן", (dialog, which) -> {
                    String serial = bundle.getString("serial");
                    String codeName = bundle.getString("type");
                    provider.deleteDevice(serial, codeName);
                    dismiss();
                });

                alertBuilder.setNegativeButton("בטל", (dialog, which) -> dismiss());
                alertBuilder.setCancelable(false);
                alertBuilder.create().show();
            }
        });

        v.findViewById(R.id.btneditorCommentEdit).setOnClickListener(v1 -> showComment());
        v.findViewById(R.id.btneditorCommentSave).setOnClickListener(v1 -> showEdit());


        return v;

    }

    private void showEdit() {
        View v = getView();
        if (v != null) {
            String comment = ((TextView) v.findViewById(R.id.etEditorCommment)).getText().toString();
            v.findViewById(R.id.etEditorCommment).setVisibility(View.GONE);
            v.findViewById(R.id.btneditorCommentSave).setVisibility(View.GONE);
            v.findViewById(R.id.btneditorCommentEdit).setVisibility(View.VISIBLE);
            v.findViewById(R.id.tvEditorCommment).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvEditorCommment)).setText(comment);
        }
    }

    private void showComment() {
        View v = getView();
        if (v != null) {
            String comment = ((TextView) v.findViewById(R.id.tvEditorCommment)).getText().toString();
            v.findViewById(R.id.tvEditorCommment).setVisibility(View.GONE);
            v.findViewById(R.id.btneditorCommentEdit).setVisibility(View.GONE);
            v.findViewById(R.id.btneditorCommentSave).setVisibility(View.VISIBLE);
            v.findViewById(R.id.etEditorCommment).setVisibility(View.VISIBLE);
            ((EditText) v.findViewById(R.id.etEditorCommment)).setText(comment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null && isAdded()) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void sendLocation(String location, String sublocation) {
        if (!location.equals("") && !sublocation.equals("")) {
            String serial = bundle.getString("serial");
            String codeName = bundle.getString("type");
            FieldDevice device = provider.getDevice(serial, codeName);
            if (device != null) {
                provider.updateLocation(location, sublocation, device);
            }
        }
    }
}

