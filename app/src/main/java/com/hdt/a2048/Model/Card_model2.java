package com.hdt.a2048.Model;

import android.content.Context;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hdt.a2048.R;


public class Card_model2 extends FrameLayout {

    private TextView label;
    private View background;
    private int num = 0;

    public Card_model2(Context context) {
        super(context);
        LayoutParams lp = null;

        background = new View(getContext());
        lp = new LayoutParams(-1, -1);
        lp.setMargins(10, 10, 0, 0);
        background.setBackgroundColor(getResources().getColor(
                R.color.normalCardBack));
        addView(background, lp);
        background.requestLayout();

        label = new TextView(getContext());
        label.setTextSize(28);
        label.setGravity(Gravity.CENTER);

        TextPaint tp = label.getPaint();
        tp.setFakeBoldText(true);

        lp = new LayoutParams(-1, -1);
        lp.setMargins(10, 10, 0, 0);
        addView(label, lp);
        label.requestLayout();
        setNum(0);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        label.setTextSize(28);
        if (num <= 0) {
            label.setText("");
        }
        switch (num) {
            case 0:
                label.setBackgroundResource(R.color.normalCardBack);
                break;
            case 2:
                label.setTextColor(getResources().getColor(R.color._2Font));
                label.setBackgroundResource(R.color._2Back);
                label.setText("夏");
                break;
            case 4:
                label.setTextColor(getResources().getColor(R.color._4Font));
                label.setBackgroundResource(R.color._4Back);
                label.setText("周");
                break;
            case 8:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._8Back);
                label.setText("商");
                break;
            case 16:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._16Back);
                label.setText("秦");
                break;
            case 32:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._32Back);
                label.setText("汉");
                break;
            case 64:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._64Back);
                label.setText("晋");
                break;
            case 128:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._128Back);
                label.setText("南北朝");
                break;
            case 256:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._256Back);
                label.setText("隋");
                break;
            case 512:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._512Back);
                label.setText("唐");
                break;
            case 1024:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._1024Back);
                label.setTextSize(20);
                label.setText("宋");
                break;
            case 2048:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._2048Back);
                label.setTextSize(20);
                label.setText("元");
                break;
            case 4096:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._2048Back);
                label.setTextSize(20);
                label.setText("明");
                break;
            case 8192:
                label.setTextColor(getResources().getColor(R.color.otherFont));
                label.setBackgroundResource(R.color._2048Back);
                label.setTextSize(20);
                label.setText("清");
                break;
            default:
                label.setTextColor(getResources().getColor(R.color._2Font));
                label.setBackgroundResource(R.color._2Back);
                label.setTextSize(28);
                break;
        }
    }
    public boolean equals(Card_model2 o) {
        return getNum() == o.getNum();
    }

    protected Card_model2 clone() {
        Card_model2 c = new Card_model2(getContext());
        c.setNum(getNum());
        return c;
    }

    public TextView getLabel() {
        return label;
    }
}
