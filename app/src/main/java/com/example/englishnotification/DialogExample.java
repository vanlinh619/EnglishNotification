package com.example.englishnotification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishnotification.handle.CustomList.ItemListExampleAdapter;
import com.example.englishnotification.model.ItemDataExample;

import java.util.ArrayList;

public class DialogExample extends DialogFragment {
    private RecyclerView rcListExample;
    private TextView txTitle;
    private MainActivity mainActivity;
    private ArrayList<ItemDataExample> listWord;

    public static DialogExample newInstance(MainActivity mainActivity) {
        DialogExample frag = new DialogExample();
        Bundle arg = new Bundle();
        arg.putSerializable("mainActivity", mainActivity);
        frag.setArguments(arg);
        return frag;
    }

    public static DialogExample newInstance(MainActivity mainActivity, ArrayList<ItemDataExample> listWord, String title) {
        DialogExample frag = new DialogExample();
        Bundle arg = new Bundle();
        arg.putSerializable("mainActivity", mainActivity);
        arg.putSerializable("listWord", listWord);
        arg.putSerializable("title", title);
        frag.setArguments(arg);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_example, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setView(view);

        Bundle bundle = getArguments();
        mainActivity = (MainActivity) bundle.getSerializable("mainActivity");
        listWord = (ArrayList<ItemDataExample>) bundle.getSerializable("listWord");
        txTitle.setText((String) bundle.getSerializable("title"));

        ItemListExampleAdapter itemListExampleAdapter = new ItemListExampleAdapter(listWord ,mainActivity);
        rcListExample.setAdapter(itemListExampleAdapter);
        rcListExample.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void setView(View view) {
        rcListExample = view.findViewById(R.id.rc_example);
        txTitle = view.findViewById(R.id.tx_title_example);
    }
}
