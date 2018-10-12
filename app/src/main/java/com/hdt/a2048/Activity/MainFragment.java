package com.hdt.a2048.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdt.a2048.R;
import com.hdt.a2048.Utils.AnimLayer;
import com.hdt.a2048.Utils.GameView;


public class MainFragment extends Fragment {

    private int score = 0;
    private TextView tvScore, tvBestScore;
    private LinearLayout root = null;
    private GameView gameView;
    private AnimLayer animLayer = null;
    private String flag = "1";
    private TextView textView;


    private long currentSecond = 0;//当前毫秒数
    public static final String SP_KEY_BEST_SCORE = "bestScore";


    public static MainFragment mainFragment;

    public MainFragment() {
        mainFragment = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //首先将布局放进来 因为是fragment  所以特殊一点  这么放
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //然后获取一个容器  放中间的gameview  因为开源库的原因更改成fragment
        root = (LinearLayout) rootView.findViewById(R.id.container);

//        container.requestLayout();
        //设置颜色
        root.setBackgroundColor(0xfffaf8ef);
//        root.setBackgroundColor(0x0000);
        //初始化控件
        tvScore = (TextView) rootView.findViewById(R.id.tvScore);
        tvBestScore = (TextView) rootView.findViewById(R.id.tvBestScore);

        gameView = (GameView) rootView.findViewById(R.id.gameView);

        animLayer = (AnimLayer) rootView.findViewById(R.id.animLayer);
        textView = (TextView) rootView.findViewById(R.id.time);

        return rootView;
    }

    public static MainFragment getMainFragment() {
        return mainFragment;
    }

    public void clearScore() {
        this.score = 0;
        this.currentSecond = 0;
        showScore();
    }

    public void showScore() {
        tvScore.setText(score + "");
    }

    public void startGame() {
        gameView.startGame();
    }
    public  void contiuneGame(){
        gameView.continueGame();
    }

    public void addScore(int s) {
        score += s;
        showScore();
        int maxScore = Math.max(score, getBestScore());
        saveBestScore(maxScore);
        showBestScore(maxScore);
    }

    public void saveBestScore(int s) {

        // 获取  SharedPreferences
        SharedPreferences.Editor e = getActivity().getPreferences(getActivity().MODE_PRIVATE).edit();

        //往SharedPreferences中放东西
        e.putInt(SP_KEY_BEST_SCORE, s);

        //提交
        e.commit();
    }

    //获取最高分
    public int getBestScore() {
        return getActivity().getPreferences(getActivity().MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
    }

    public void showBestScore(int s) {
        tvBestScore.setText(s + "");
    }

    public AnimLayer getAnimLayer() {
        return animLayer;
    }

    public TextView getTextView() {
        return textView;
    }

    public int getScore() {
        return score;
    }
    public long getCurrentSecond() {
        return currentSecond;
    }

    public void setCurrentSecond(long currentSecond) {
        this.currentSecond = currentSecond;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public GameView getGameView() {
        return gameView;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
