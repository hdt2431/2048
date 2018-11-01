package com.hdt.a2048.Utils;


import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.renderscript.Sampler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


import com.hdt.a2048.Activity.LoginActivity;
import com.hdt.a2048.Activity.MainFragment;
import com.hdt.a2048.Data.MySqlHelper;
import com.hdt.a2048.Model.Card;
import com.hdt.a2048.Model.Gamer;
import com.hdt.a2048.R;

import java.util.ArrayList;
import java.util.List;

import static com.hdt.a2048.Utils.TimeUtil.getFormatHMS;


public class GameView extends LinearLayout {

    private Context context;
    private MediaPlayer player;
    private String flag = "1";
    private ArrayList<Gamer> mList = new ArrayList<Gamer>();
    MainFragment mainFragment = MainFragment.getMainFragment();
    private Card[][] cardsMap = new Card[Config.LINES][Config.LINES];
    private int[] cards = new int[Config.LINES * Config.LINES];
    private List<Point> emptyPoints = new ArrayList<Point>();
    private static final int DATABASES_VERSION = 3;
    private Context friendContext;

    public GameView(Context context) {
        super(context);
        this.context = context;
//        player = MediaPlayer.create(context, R.raw.move);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        player = MediaPlayer.create(context, R.raw.move);
        //LayoutInflater.from(context).inflate(R.layout.fragment_main,this);
        initGameView();
    }


    //初始化Gameview
    private void initGameView() {
        for (int i = 0; i < cards.length; i++)
            cards[i] = 0;
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0xffbbada0);
        setOnTouchListener(new OnTouchListener() {

            private float startX,
                    startY,
                    offsetX,
                    offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;

                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                player.start();
                                swipeLeft();
                            } else if (offsetX > 5) {
                                player.start();
                                swipeRight();
                            }
                        } else {
                            if (offsetY < -5) {
                                player.start();
                                swipeUp();
                            } else if (offsetY > 5) {
                                player.start();
                                swipeDown();
                            }
                        }

                        break;
                }
                return true;
            }
        });
    }

    //初始化卡片大小
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Config.CARD_WIDTH = (Math.min(w, h) - 10) / Config.LINES;

        addCards(Config.CARD_WIDTH, Config.CARD_WIDTH);
        flag = mainFragment.getFlag();
        Log.e("aaaflag", flag);
        if (flag.equals("1")) {
            startGame();
        } else if (flag.equals("2")) {
            continueGame();
        }
    }

    //添加卡片
    private void addCards(int cardWidth, int cardHeight) {

        Card c;

        LinearLayout line;
        LayoutParams lineLp;

        for (int y = 0; y < Config.LINES; y++) {
            line = new LinearLayout(getContext());
            lineLp = new LayoutParams(-1, cardHeight);
            addView(line, lineLp);
            line.requestLayout();
            for (int x = 0; x < Config.LINES; x++) {
                c = new Card(getContext());
                line.addView(c, cardWidth, cardHeight);
                //7.0以上  需要添加此行代码 否则VIEW不显示！！！！
                c.requestLayout();
                cardsMap[x][y] = c;
            }
        }

    }

    //开始游戏（也是重新开始）
    public void startGame() {


        mainFragment.clearScore();
        mainFragment.showBestScore(mainFragment.getBestScore());


        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                cardsMap[x][y].setNum(0);
                cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
            }
        }
        addRandomNum();
        addRandomNum();
        try {
            friendContext = context.createPackageContext("com.hdt.datasfor2048",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.delete("gamestate", "name = ?", new String[]{"state1"});
        values.put("cards", NumtoString(cards));
        values.put("user_score", 0);
        values.put("time", 0);
        values.put("name", "state1");
        db.insert("gamestate", "id", values);
//        Cursor cursor = db.query("gamestate", null, null, null, null, null, "user_score desc");//列名称  倒排序
//        while (cursor.moveToNext()) {
//            int nameIndex = cursor.getColumnIndex("name");
//            int scoreIndex = cursor.getColumnIndex("user_score");
//            int idIndex = cursor.getColumnIndex("id");
//            int cardsIndex = cursor.getColumnIndex("cards");
//            String name = cursor.getString(nameIndex);
//            String cards = cursor.getString(cardsIndex);
//            int score = cursor.getInt(scoreIndex);
//            int id = cursor.getInt(idIndex);
//            Log.e("bbb", name + " " + cards + " " + String.valueOf(score) + " " + String.valueOf(id));
//        }
        db.close();
    }

    public void continueGame() {

        mainFragment.setScore(queryScore());
        mainFragment.showScore();
        mainFragment.showBestScore(mainFragment.getBestScore());
        //  Log.e("cards", queryCards());
        cards = StringtoNum(queryCards());
        mainFragment.setCurrentSecond(queryTime());
        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                cardsMap[x][y].setNum(cards[y * Config.LINES + x]);
            }
        }


    }

    public void updateCards() {
        String str = NumtoString(cards);
        int score = mainFragment.getScore();
        Long currentSecond = mainFragment.getCurrentSecond();
        try {
            friendContext = context.createPackageContext("com.hdt.datasfor2048",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cards", str);
        db.update("gamestate", values, "name=?", new String[]{"state1"});
        ContentValues values2 = new ContentValues();
        values2.put("time", currentSecond);
        db.update("gamestate", values2, "name=?", new String[]{"state1"});
        ContentValues values3 = new ContentValues();
        values3.put("user_score", score);
        db.update("gamestate", values3, "name=?", new String[]{"state1"});
        db.close();
    }

    public Long queryTime() {
        try {
            friendContext = context.createPackageContext("com.hdt.datasfor2048",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        Cursor cursor = db.query("gamestate", null, null, null, null, null, "user_score desc");//列名称  倒排序
        Long time = null;
        while (cursor.moveToNext()) {
//            int nameIndex = cursor.getColumnIndex("name");
//            int scoreIndex = cursor.getColumnIndex("user_score");
//            int idIndex = cursor.getColumnIndex("id");
            int timeIndex = cursor.getColumnIndex("time");
//            String name = cursor.getString(nameIndex);
            time = cursor.getLong(timeIndex);
//            int score = cursor.getInt(scoreIndex);
//            int id = cursor.getInt(idIndex);
//            Log.e("bbb",  cards );
        }
        db.close();
        return time;
    }

    public int queryScore() {
        try {
            friendContext = context.createPackageContext("com.hdt.datasfor2048",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        Cursor cursor = db.query("gamestate", null, null, null, null, null, "user_score desc");//列名称  倒排序
        int score = 0;
        while (cursor.moveToNext()) {
//            int nameIndex = cursor.getColumnIndex("name");
//            int scoreIndex = cursor.getColumnIndex("user_score");
//            int idIndex = cursor.getColumnIndex("id");
            int scoreIndex = cursor.getColumnIndex("user_score");
//            String name = cursor.getString(nameIndex);
            score = cursor.getInt(scoreIndex);
//            int score = cursor.getInt(scoreIndex);
//            int id = cursor.getInt(idIndex);
//            Log.e("bbb",  cards );
        }
        db.close();
        return score;
    }

    public String queryCards() {
        try {
            friendContext = context.createPackageContext("com.hdt.datasfor2048",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        Cursor cursor = db.query("gamestate", null, null, null, null, null, "user_score desc");//列名称  倒排序
        String cards = null;
        while (cursor.moveToNext()) {
//            int nameIndex = cursor.getColumnIndex("name");
//            int scoreIndex = cursor.getColumnIndex("user_score");
//            int idIndex = cursor.getColumnIndex("id");
            int cardsIndex = cursor.getColumnIndex("cards");
//            String name = cursor.getString(nameIndex);
            cards = cursor.getString(cardsIndex);
//            int score = cursor.getInt(scoreIndex);
//            int id = cursor.getInt(idIndex);
//            Log.e("bbb",  cards );
        }
        db.close();
        return cards;
    }

    public void deleteCards() {
        try {
            friendContext = context.createPackageContext("com.hdt.datasfor2048",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.delete("gamestate", "name = ?", new String[]{"state1"});
        db.close();
    }


    private int[] StringtoNum(String str) {
        String[] b = str.split("-");
        int[] a = new int[b.length];
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.valueOf(b[i]);
        }
        return a;
    }

    private String NumtoString(int[] a) {
        String[] b = new String[a.length];
        for (int i = 0; i < b.length; i++) {
            if (i == b.length - 1) {
                b[i] = String.valueOf(a[i]);
            } else
                b[i] = String.valueOf(a[i]) + "-";
        }
        StringBuilder str = new StringBuilder("");
        for (String i : b
                ) {
            str.append(i);
        }
        return str.toString();
    }

    //添加随机卡片
    private void addRandomNum() {

        emptyPoints.clear();
        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                if (cardsMap[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

        if (emptyPoints.size() > 0) {

            Point p = emptyPoints.remove((int) (Math.random() * emptyPoints
                    .size()));
            cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
            cards[Config.LINES * p.y + p.x] = cardsMap[p.x][p.y].getNum();
//            updateCards();
            MainFragment.getMainFragment().getAnimLayer()
                    .createScaleTo1(cardsMap[p.x][p.y]);
        }
    }

    //向左移动
    private void swipeLeft() {


        boolean merge = false;

        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {

                for (int x1 = x + 1; x1 < Config.LINES; x1++) {
                    if (cardsMap[x1][y].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {

                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x1][y],
                                            cardsMap[x][y], x1, x, y, y);

                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x1][y].setNum(0);
                            cards[Config.LINES * y + x1] = cardsMap[x1][y].getNum();
                            x--;
                            merge = true;

                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x1][y],
                                            cardsMap[x][y], x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x1][y].setNum(0);
                            cards[Config.LINES * y + x1] = cardsMap[x1][y].getNum();
                            MainFragment.getMainFragment().addScore(
                                    cardsMap[x][y].getNum());
                            merge = true;
                            //x--;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
//        updateCards();
//        Log.e("bbb",queryCards());
    }

    //向右移动
    private void swipeRight() {


        boolean merge = false;

        for (int y = 0; y < Config.LINES; y++) {
            for (int x = Config.LINES - 1; x >= 0; x--) {

                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (cardsMap[x1][y].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x1][y],
                                            cardsMap[x][y], x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x1][y].setNum(0);
                            cards[Config.LINES * y + x1] = cardsMap[x1][y].getNum();
                            x++;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x1][y],
                                            cardsMap[x][y], x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x1][y].setNum(0);
                            cards[Config.LINES * y + x1] = cardsMap[x1][y].getNum();
                            MainFragment.getMainFragment().addScore(
                                    cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
//        updateCards();
//        Log.e("bbb",queryCards());
    }

    //向上移动
    private void swipeUp() {


        boolean merge = false;

        for (int x = 0; x < Config.LINES; x++) {
            for (int y = 0; y < Config.LINES; y++) {

                for (int y1 = y + 1; y1 < Config.LINES; y1++) {
                    if (cardsMap[x][y1].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x][y1],
                                            cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x][y1].setNum(0);
                            cards[Config.LINES * y1 + x] = cardsMap[x][y1].getNum();
                            y--;

                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x][y1],
                                            cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x][y1].setNum(0);
                            cards[Config.LINES * y1 + x] = cardsMap[x][y1].getNum();
                            MainFragment.getMainFragment().addScore(
                                    cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;

                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
//        updateCards();
//        Log.e("bbb",queryCards());
    }

    //向下移动
    private void swipeDown() {


        boolean merge = false;

        for (int x = 0; x < Config.LINES; x++) {
            for (int y = Config.LINES - 1; y >= 0; y--) {

                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (cardsMap[x][y1].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x][y1],
                                            cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x][y1].setNum(0);
                            cards[Config.LINES * y1 + x] = cardsMap[x][y1].getNum();

                            y++;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            MainFragment
                                    .getMainFragment()
                                    .getAnimLayer()
                                    .createMoveAnim(cardsMap[x][y1],
                                            cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cards[Config.LINES * y + x] = cardsMap[x][y].getNum();
                            cardsMap[x][y1].setNum(0);
                            cards[Config.LINES * y1 + x] = cardsMap[x][y1].getNum();
                            MainFragment.getMainFragment().addScore(
                                    cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

//        DialogUtils.getAddChartDialog(context, MainFragment.getMainFragment().getScore());

        if (merge) {
            addRandomNum();
            checkComplete();
        }
//        updateCards();
//        Log.e("bbb",queryCards());
    }

    //检查是否完成
    private void checkComplete() {

        boolean complete = true;

        ALL:
        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                if (cardsMap[x][y].getNum() == 0
                        || (x > 0 && cardsMap[x][y].equals(cardsMap[x - 1][y]))
                        || (x < Config.LINES - 1 && cardsMap[x][y]
                        .equals(cardsMap[x + 1][y]))
                        || (y > 0 && cardsMap[x][y].equals(cardsMap[x][y - 1]))
                        || (y < Config.LINES - 1 && cardsMap[x][y]
                        .equals(cardsMap[x][y + 1]))) {

                    complete = false;
                    break ALL;
                }
            }
        }

        if (complete) {
            String time = getFormatHMS(mainFragment.getCurrentSecond());
            try {
                friendContext = context.createPackageContext("com.hdt.datasfor2048",
                        Context.CONTEXT_IGNORE_SECURITY);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
            SQLiteDatabase db = mySqlHelper.getWritableDatabase();
            Cursor cursor = db.query("charts", null, null, null, null, null, "user_score desc");//列名称  倒排序
            while (cursor.moveToNext()) {
                int nameIndex = cursor.getColumnIndex("user_name");
                int scoreIndex = cursor.getColumnIndex("user_score");
                int idIndex = cursor.getColumnIndex("id");
                int timeIndex = cursor.getColumnIndex("time");
                String time2 = cursor.getString(timeIndex);
                String name = cursor.getString(nameIndex);
                int score = cursor.getInt(scoreIndex);
                int id = cursor.getInt(idIndex);
                mList.add(new Gamer(id, name, score, time2));
            }
            if (mList.size() == 10) {
                Gamer gamer = mList.get(mList.size() - 1);
                if (mainFragment.getScore() > gamer.getScore()) {
                    DialogUtils.getAddChartDialog(context, mainFragment.getScore(), time);
                } else {
                    DialogUtils.endDialog(context);
                }
            } else if (mList.size() < 10) {
                DialogUtils.getAddChartDialog(context, mainFragment.getScore(), time);
            }
            // DialogUtils.getAddChartDialog(context, MainFragment.getMainFragment().getScore(),time);
        }
    }


}
