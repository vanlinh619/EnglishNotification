package com.ale.englishnotification;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.ale.englishnotification.model.Word;

import java.util.ArrayList;
import java.util.Random;

public class RepeatSpeakFragment extends Fragment {

    private ImageView imClose, imSound;
    private TextView txEnglish, txMeans;
    private View parent;
    private CountDownTimer countDownTimer;
    private int imageSound = 0;
    private boolean[] isRepeat;

    public RepeatSpeakFragment() {
        isRepeat = new boolean[]{false};
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repeat_speak, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setView(view);

        imClose.setOnClickListener((v) -> {
            parent.setVisibility(View.GONE);
            stopAnimationSound();
            isRepeat[0] = false;
        });
    }

    private void setView(View view) {
        parent = view;
        parent.setVisibility(View.GONE);
        imClose = view.findViewById(R.id.im_close);
        imSound = view.findViewById(R.id.im_sound);
        txEnglish = view.findViewById(R.id.tx_english);
        txMeans = view.findViewById(R.id.tx_vietnamese);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showView(ArrayList<Word> words){
        parent.setVisibility(View.VISIBLE);
        isRepeat = new boolean[]{true};
        countDownTimer = new CountDownTimer(300, 300) {
            @Override
            public void onTick(long l) {
                if(imageSound == R.drawable.sound_waves1){
                    imageSound = R.drawable.sound_waves2;
                } else {
                    imageSound = R.drawable.sound_waves1;
                }
                imSound.setImageResource(imageSound);
            }

            @Override
            public void onFinish() {
                countDownTimer.start();
            }
        }.start();

        repeat(isRepeat, words);
    }


    private void repeat(boolean[] isRepeat, ArrayList<Word> words) {
        Random random = new Random();
        int index = random.nextInt(words.size());
        Word word = MainActivity.listWord.get(index);
        parent.post((() -> {
            txEnglish.setText(word.english);
            txMeans.setText(MainActivity.meansToString(word));
        }));
        ((MainActivity) getActivity()).speak(word, () -> {
            if(isRepeat[0]){
                repeat(isRepeat, words);
            }
        });
    }

    public void stopAnimationSound(){
        countDownTimer.cancel();
    }

    public boolean[] isRepeat() {
        return isRepeat;
    }
}