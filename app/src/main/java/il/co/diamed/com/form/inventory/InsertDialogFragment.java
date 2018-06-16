package il.co.diamed.com.form.inventory;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class InsertDialogFragment extends DialogFragment {
    private final String TAG = "InsertDialogFragment";

    static InsertDialogFragment newInstance() {

        return new InsertDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }
        if (getContext() != null) {
            View v = inflater.inflate(R.layout.fragment_inventory_insert, container, false);

            this.setCancelable(false);


            //get parts serial list
            ClassApplication application = (ClassApplication) getActivity().getApplication();
            DatabaseProvider provider = application.getDatabaseProvider();
            String[] PARTS = provider.getLabParts();

            ArrayAdapter<String> arrayadapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_dropdown_item, PARTS);
            AutoCompleteTextView textView = v.findViewById(R.id.etItem_serial);
            textView.setAdapter(arrayadapter);
            textView.setOnItemClickListener((parent, view, position, id) ->
                    verifyPart(provider.getPartInfo(arrayadapter.getItem(position))));

            v.findViewById(R.id.insertSubmit).setOnClickListener(v1 -> {
                String desc = provider.getPartInfo(textView.getText().toString());
                if (verifyPart(desc)) {

                    String inStock = ((EditText) v.findViewById(R.id.etItem_inStock)).getText().toString();
                    if (validInStock(inStock)) {
                        ((EditText) v.findViewById(R.id.etItem_inStock)).setError(null);
                        //add part from lab
                        if (!provider.addToMyLocalInventory(textView.getText().toString(), Integer.valueOf(inStock))) {
                            Log.e(TAG, "Error on updating inStock");
                        }
                        dismiss();
                    } else {
                        ((EditText) v.findViewById(R.id.etItem_inStock)).setError("כמות לא תקינה", getActivity().getDrawable(R.drawable.ic_error_black_24dp));
                    }


                } else {
                    Log.e(TAG, "View == null");
                    dismiss();
                }
            });


            v.findViewById(R.id.insertCancel).setOnClickListener(v1 ->
                    this.dismiss());

            return v;
        } else
            return null;
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

    private boolean verifyPart(String description) {
        if (getView() != null) {
            if (description.equals("")) {
                ((EditText) getView().findViewById(R.id.etItem_serial)).setError("חלק לא קיים במערכת", getActivity().getDrawable(R.drawable.ic_warning_black_24dp));
                ((TextView) getView().findViewById(R.id.etItem_description)).setText("-");
                getView().findViewById(R.id.etItem_inStock).setVisibility(View.INVISIBLE);
                return true;
            } else {
                ((EditText) getView().findViewById(R.id.etItem_serial)).setError(null);
                ((TextView) getView().findViewById(R.id.etItem_description)).setText(description);
                getView().findViewById(R.id.etItem_inStock).setVisibility(View.VISIBLE);
                return true;
            }
        }
        return false;
    }

    private boolean validInStock(String inStock) {

        if (inStock.equals(""))
            return false;
        else {
            try {
                int num = Integer.valueOf(inStock);
                return num >= 0;

            } catch (Exception e) {
                return false;
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }
}

