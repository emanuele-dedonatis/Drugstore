package it.dedonatis.emanuele.drugstore.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.adapters.DialogAdapter;
import it.dedonatis.emanuele.drugstore.models.DrugPackage;
import it.dedonatis.emanuele.drugstore.models.DrugSubpackage;

public class Dialogs {

    public static DialogPlus setupBottomDialog(Context context, BaseAdapter adapter, OnItemClickListener onItemClickListener) {

        return DialogPlus.newDialog(context).setContentHolder(new ListHolder())
                .setCancelable(true)
                .setAdapter(adapter)
                .setFooter(R.layout.item_dialog_empty_space)
                .setHeader(R.layout.item_dialog_empty_space)
                .setGravity(Gravity.BOTTOM)
                .setOnItemClickListener(onItemClickListener)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();
    }
}
