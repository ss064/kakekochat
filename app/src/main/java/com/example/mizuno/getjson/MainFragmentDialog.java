package com.example.mizuno.getjson;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by SuzukiSusumu-SIST on 2016/10/16.
 */
public class MainFragmentDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_json, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("タイトル");
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cancel", null);
            builder.setMessage("表示テスト");
            //.setView(view);
            return builder.create();
        }
}
