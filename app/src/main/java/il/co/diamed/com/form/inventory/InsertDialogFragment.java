package il.co.diamed.com.form.inventory;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class InsertDialogFragment extends DialogFragment {
    static InsertDialogFragment newInstance() {

        return new InsertDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory_insert, container, false);

        this.setCancelable(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        //get parts serial list
        ClassApplication application = (ClassApplication) getActivity().getApplication();
        DatabaseProvider provider = application.getDatabaseProvider();
        String[] PARTS = provider.getLabParts();

        ArrayAdapter<String> arrayadapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, PARTS);

        AutoCompleteTextView textView = v.findViewById(R.id.etItem_serial);

        //textView.setDropDownWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setAdapter(arrayadapter);


        textView.setOnItemClickListener((parent, view, position, id) -> {

            verifyPart(provider.getPartInfo(arrayadapter.getItem(position)));

        });

        v.findViewById(R.id.insertSubmit).setOnClickListener(v1 -> {
            if(verifyPart(provider.getPartInfo(textView.getText().toString()))){
                String inStock = ((EditText)v.findViewById(R.id.etItem_inStock)).getText().toString();
                if(!inStock.equals("")){
                    try{
                        //add part from lab
                        provider.addToMyInventory(textView.getText().toString(),Integer.valueOf(inStock));
                        dismiss();


                    }catch (Exception e){

                    }

                }else{

                }

            }else{

            }

        });


        v.findViewById(R.id.insertCancel).setOnClickListener(v1 -> {
            this.dismiss();
        });

        return v;
    }

    private boolean verifyPart(String desc) {
        if (desc.equals("")) {
            ((TextView) getView().findViewById(R.id.etItem_description)).setText("חלק לא קיים במערכת");
            getView().findViewById(R.id.etItem_inStock).setVisibility(View.INVISIBLE);
            return false;
        } else {
            ((TextView) getView().findViewById(R.id.etItem_description)).setText(desc);
            getView().findViewById(R.id.etItem_inStock).setVisibility(View.VISIBLE);
            return true;
        }
    }
}

