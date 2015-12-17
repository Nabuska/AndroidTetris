package com.example.winnabuska.tetristddpractice;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.annimon.stream.Optional;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Predicate;
import com.example.winnabuska.tetristddpractice.TetrisLogic.TetrisModel;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Square;

import java.util.Observable;
import java.util.Observer;

public class TetrisActivity extends AppCompatActivity {

    private TetrisController tetrisController;
    private AlertDialog playStartDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TetrisUI view = new TetrisUI();
        TetrisModel model = new TetrisModel((Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE), view);
        view.setGrid(model.getGrid());
        tetrisController = new TetrisController(model);

        view.invalidate();
        setContentView(view);

        playStartDialog =
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Play")
                        .setPositiveButton("OK",
                                (dialog, which) -> {
                                    tetrisController.onActivityResume();
                                    dialog.dismiss();
                                })
                        .create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playStartDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tetrisController.onActivityPause();
    }

    private class TetrisUI extends View implements Observer {
        public int screenWidth, screenHeight;
        private final int GRID_GAP = 5;
        private int singleGridSize;
        private Paint paint;
        private final int GRID_BACKGROUND_COLOR = Color.GRAY;
        private final int [] BLOCK_COLORS = getContext().getResources().getIntArray(R.array.block_colors);
        private int blockSideLength, borderWidth;
        private Optional<Square>[][]grid;
        private TetrisOnTouchListener touchListener;


        public TetrisUI()  {
            super(getApplicationContext());
            setKeepScreenOn(true);
            setBackgroundColor(Color.BLACK);
            paint = new Paint();
            setOnTouchListener(touchListener = new TetrisOnTouchListener());
        }

        protected void setGrid(Optional<Square>[][]grid){
            this.grid = grid;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int color;
            for (int y = TetrisModel.FIRST_VISIBLE_ROW; y <= TetrisModel.LAST_VISIBLE_ROW; y++) {
                for (int x = 0; x < TetrisModel.COLUMNS; x++) {
                    Optional<Square> square = grid[y][x];
                    if (square.isPresent())
                        color = BLOCK_COLORS[square.get().COLOR];
                    else
                        color = GRID_BACKGROUND_COLOR;
                    paint.setColor(color);
                    canvas.drawRect(getRectangle(x, y), paint);
                }
            }
        }

        /*Creates a rectangle that is right size with the players Screen */
        private Rect getRectangle(int x, int y){
            Point p = new Point(borderWidth+singleGridSize*(x), singleGridSize*(y-2));
            return new Rect(p.x+GRID_GAP, p.y+GRID_GAP, p.x + GRID_GAP+blockSideLength, p.y+blockSideLength+GRID_GAP);
        }

        // Called back when the view is first created or its size changes.
        @Override
        public void onSizeChanged(int w, int h, int oldW, int oldH) {
            screenWidth = w;
            screenHeight = h;
            singleGridSize = (int)((1.0*(screenHeight-borderWidth))/ TetrisModel.NUMBER_OF_VISIBLEROWS);
            borderWidth = (screenWidth - singleGridSize*10)/2;
            blockSideLength = singleGridSize-GRID_GAP*2;
        }

        @Override
        public void update(Observable observable, Object gameEnd) {
            if(null!=gameEnd) {
                tetrisController.onGameOver();
                touchListener.disabled=true;
            }
            postInvalidate();
        }

        private class TetrisOnTouchListener implements OnTouchListener {
            //Last event is always either ACTION DOWN or ACTION MOVE

            private boolean disabled = false;

            private Optional<MotionEvent> lastEvent;

            private final BiFunction<MotionEvent, MotionEvent, Float> touchXDifference;

            private final Predicate<MotionEvent> doubleTapOnCenterOfTheScreen;
            private final Predicate<MotionEvent> touchOnRightSideOfScreen;
            private final Predicate<MotionEvent> touchOnLeftSideOfScreen;
            private final Predicate<MotionEvent> touchOnUpperPartOfScreen;
            private final Predicate<MotionEvent> tapOnRightUpperPartOfScreen;
            private final Predicate<MotionEvent> tapOnLeftUpperPartOfScreen;
            private final Predicate<MotionEvent> swipeFromLeftToRight;
            private final Predicate<MotionEvent> swipeFromRightToLeft;
            private final Predicate<MotionEvent> swipeFromUpToDown;

            public TetrisOnTouchListener(){
                lastEvent = Optional.empty();
                touchOnRightSideOfScreen = e -> e.getX() > screenWidth*(5.0/6);
                touchOnLeftSideOfScreen = e -> e.getX() < (screenWidth)/6.0;
                touchOnUpperPartOfScreen = e -> e.getY() < screenHeight*(3.0/7);
                touchXDifference = ((first, second) -> (first.getX()-second.getX()));

                swipeFromLeftToRight = e -> lastEvent.isPresent() && e.getAction() != MotionEvent.ACTION_DOWN &&
                        touchXDifference.apply(lastEvent.get(), e)<-screenWidth/8.0;
                swipeFromRightToLeft = e -> lastEvent.isPresent() && e.getAction() != MotionEvent.ACTION_DOWN &&
                        touchXDifference.apply(lastEvent.get(), e)>+screenWidth/8.0;
                swipeFromUpToDown = e -> lastEvent.isPresent() && e.getAction() == MotionEvent.ACTION_MOVE &&
                        lastEvent.get().getY()-e.getY()< -screenWidth/8.0;

                tapOnRightUpperPartOfScreen = e -> e.getAction()==MotionEvent.ACTION_UP && touchOnRightSideOfScreen.test(e) &&
                        touchOnUpperPartOfScreen.test(e) && lastEvent.isPresent() && lastEvent.get().getAction() == MotionEvent.ACTION_DOWN &&
                        touchOnRightSideOfScreen.test(lastEvent.get());
                tapOnLeftUpperPartOfScreen = e -> e.getAction()==MotionEvent.ACTION_UP && touchOnLeftSideOfScreen.test(e) &&
                        touchOnUpperPartOfScreen.test(e) && lastEvent.isPresent() && lastEvent.get().getAction() == MotionEvent.ACTION_DOWN &&
                        touchOnLeftSideOfScreen.test(lastEvent.get());

                doubleTapOnCenterOfTheScreen = e -> lastEvent.isPresent() && lastEvent.get().getAction() == MotionEvent.ACTION_DOWN
                        && e.getEventTime()- lastEvent.get().getEventTime()<250 && !touchOnLeftSideOfScreen.test(e) && !touchOnRightSideOfScreen.test(e);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(disabled)
                    return true;

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(doubleTapOnCenterOfTheScreen.test(event)){
                        lastEvent = Optional.empty();
                        tetrisController.onDoubleTapCenter();
                    }
                    else
                        lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(swipeFromUpToDown.test(event)){
                    tetrisController.onSwipeDown();
                    lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(swipeFromLeftToRight.test(event)){
                    tetrisController.onSwipeRight();
                    lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(swipeFromRightToLeft.test(event)){
                    tetrisController.onSwipeLeft();
                    lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(tapOnRightUpperPartOfScreen.test(event))
                    tetrisController.onTapRight();
                else if(tapOnLeftUpperPartOfScreen.test(event))
                    tetrisController.onTapLeft();

                return true;
            }
        }
    }
}
