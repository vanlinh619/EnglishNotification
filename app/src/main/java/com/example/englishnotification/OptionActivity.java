package com.example.englishnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.UtilContent;
import com.example.englishnotification.model.Word;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;
import java.util.Random;

public class OptionActivity extends AppCompatActivity implements Serializable {

    private ConstraintLayout ctType, ctTag, ctTagEdit, ctTypeEdit;
    private ChipGroup cgType, cgTag;
    private ImageView imTypeDirectional, imTagDirectional, imTagAdd, imTypeAdd, imTagEdit, imTagDelete,
            imTypeEdit, imTypeDelete, imBack;
    private EditText edType, edTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        MainActivity.hideSystemBar(this);

        MainActivity.loadAds(this);

        setView();

        ctType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cgType.getVisibility() == View.GONE){
                    cgType.setVisibility(View.VISIBLE);
                    imTypeDirectional.setVisibility(View.GONE);
                    imTypeAdd.setVisibility(View.VISIBLE);
                    if(cgType.getCheckedChipId() != View.NO_ID){
                        ctTypeEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    cgType.setVisibility(View.GONE);
                    imTypeDirectional.setVisibility(View.VISIBLE);
                    imTypeAdd.setVisibility(View.GONE);
                    ctTypeEdit.setVisibility(View.GONE);
                }
            }
        });

        ctTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cgTag.getVisibility() == View.GONE){
                    cgTag.setVisibility(View.VISIBLE);
                    imTagDirectional.setVisibility(View.GONE);
                    imTagAdd.setVisibility(View.VISIBLE);
                    if(cgTag.getCheckedChipId() != View.NO_ID){
                        ctTagEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    cgTag.setVisibility(View.GONE);
                    imTagDirectional.setVisibility(View.VISIBLE);
                    imTagAdd.setVisibility(View.GONE);
                    ctTagEdit.setVisibility(View.GONE);
                }
            }
        });

        for (Type type : MainActivity.types) {
            Chip chip = new Chip(this);
            chip.setText(type.name);
            chip.setCheckable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chip.isChecked()){
                        ctTypeEdit.setVisibility(View.VISIBLE);
                        edType.setHint(type.name);
                        edType.setText(type.name);
                    } else {
                        ctTypeEdit.setVisibility(View.GONE);
                    }
                }
            });
            cgType.addView(chip);
        }

        for (Tag tag : MainActivity.tags) {
            Chip chip = new Chip(this);
            chip.setText(tag.name);
            chip.setCheckable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chip.isChecked()){
                        ctTagEdit.setVisibility(View.VISIBLE);
                        edTag.setHint(tag.name);
                        edTag.setText(tag.name);
                    } else {
                        ctTagEdit.setVisibility(View.GONE);
                    }
                }
            });
            cgTag.addView(chip);
        }

        imTypeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(DialogAddEditWord.ADD_TYPE);
                dialogAddEditWord.show(fm, "fragment_edit_name");
            }
        });

        imTagAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(DialogAddEditWord.ADD_TAG);
                dialogAddEditWord.show(fm, "fragment_edit_name");
            }
        });

        imTypeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = edType.getText().toString().trim();
                String th = edType.getHint().toString().trim();
                for (int i = 0; i < MainActivity.types.size(); i++){
                    Type type = MainActivity.types.get(i);
                    if (type.name == th){
                        if(!t.equals("") && !MainActivity.typeExists(t, type.id)){
                            type.name = t;
                            MainActivity.database.updateType(type);
                            edType.setHint(type.name);
                            Chip chip = (Chip) cgType.getChildAt(i);
                            chip.setText(type.name);
                        } else {
                            if (t.equals("")){
                                Toast.makeText(OptionActivity.this, "Please fill text!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OptionActivity.this, "Type already exist!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                }
            }
        });

        imTagEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = edTag.getText().toString().trim();
                String th = edTag.getHint().toString().trim();
                for (int i = 0; i < MainActivity.tags.size(); i++){
                    Tag tag = MainActivity.tags.get(i);
                    if (tag.name == th){
                        if(!t.equals("") && !MainActivity.tagExists(t, tag.id)){
                            tag.name = t;
                            MainActivity.database.updateTag(tag);
                            edTag.setHint(tag.name);
                            Chip chip = (Chip) cgTag.getChildAt(i);
                            chip.setText(tag.name);
                        } else {
                            if (t.equals("")){
                                Toast.makeText(OptionActivity.this, "Please fill text!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OptionActivity.this, "Tag already exist!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                }
            }
        });

        imTypeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String th = edType.getHint().toString().trim();
                for (int i = 0; i < MainActivity.types.size(); i++){
                    Type type = MainActivity.types.get(i);
                    if (type.name == th){
                        if(!MainActivity.database.foreignTypeExist(type.id)) {
                            MainActivity.database.deleteType(type.id);
                            MainActivity.types.remove(type);
                            ctTypeEdit.setVisibility(View.GONE);
                            Chip chip = (Chip) cgType.getChildAt(i);
                            cgType.removeView(chip);
                        } else {
                            Toast.makeText(OptionActivity.this, "Can not delete type because already foreign type exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        imTagDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String th = edTag.getHint().toString().trim();
                for (int i = 0; i < MainActivity.tags.size(); i++){
                    Tag tag = MainActivity.tags.get(i);
                    if (tag.name == th){
//                        MainActivity.database.deleteTag(tag.id);
//                        MainActivity.database.deleteTagWordByTagId(tag.id);
//                        MainActivity.tags.remove(tag);
//                        ctTagEdit.setVisibility(View.GONE);
//                        Chip chip = (Chip) cgTag.getChildAt(i);
//                        cgTag.removeView(chip);
                        if(!MainActivity.database.foreignTagExist(tag.id)) {
                            MainActivity.database.deleteTag(tag.id);
                            MainActivity.tags.remove(tag);
                            ctTagEdit.setVisibility(View.GONE);
                            Chip chip = (Chip) cgTag.getChildAt(i);
                            cgTag.removeView(chip);
                        } else {
                            Toast.makeText(OptionActivity.this, "Can not delete tag because already foreign type exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void createNewTypeChip(){
        Type type = MainActivity.types.get(MainActivity.types.size() - 1);
        Chip chip = new Chip(this);
        chip.setText(type.name);
        chip.setCheckable(true);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chip.isChecked()){
                    ctTypeEdit.setVisibility(View.VISIBLE);
                    edType.setHint(type.name);
                    edType.setText(type.name);
                } else {
                    ctTypeEdit.setVisibility(View.GONE);
                }
            }
        });
        cgType.addView(chip);
    }

    public void createNewTagChip(){
        Tag tag = MainActivity.tags.get(MainActivity.tags.size() - 1);
        Chip chip = new Chip(this);
        chip.setText(tag.name);
        chip.setCheckable(true);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chip.isChecked()){
                    ctTagEdit.setVisibility(View.VISIBLE);
                    edTag.setHint(tag.name);
                    edTag.setText(tag.name);
                } else {
                    ctTagEdit.setVisibility(View.GONE);
                }
            }
        });
        cgTag.addView(chip);
    }

    private void setView() {
        ctType = findViewById(R.id.ct_item_type);
        cgType = findViewById(R.id.cg_type);
        imTypeDirectional = findViewById(R.id.im_type_directional);
        ctTag = findViewById(R.id.ct_item_tag);
        cgTag = findViewById(R.id.cg_tag);
        imTagDirectional = findViewById(R.id.im_tag_directional);
        edTag = findViewById(R.id.ed_tag);
        edType = findViewById(R.id.ed_type);
        ctTagEdit = findViewById(R.id.ct_tag_edit);
        ctTypeEdit = findViewById(R.id.ct_type_edit);
        imTagAdd = findViewById(R.id.im_tag_add);
        imTypeAdd = findViewById(R.id.im_type_add);
        imTagEdit = findViewById(R.id.im_tag_edit);
        imTypeEdit = findViewById(R.id.im_type_edit);
        imTagDelete = findViewById(R.id.im_tag_delete);
        imTypeDelete = findViewById(R.id.im_type_delete);
        imBack = findViewById(R.id.im_back);
    }
}