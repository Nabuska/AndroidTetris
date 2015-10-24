package com.example.winnabuska.tetristddpractice;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.annimon.stream.Optional;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Predicate;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Block;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Square;
import com.example.winnabuska.tetristddpractice.Control.UIActionExecutor;
import com.example.winnabuska.tetristddpractice.Control.TetrisController;

public class TetrisActivity extends AppCompatActivity {

    private UIActionExecutor uiActionExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TetrisController.initialize((Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE));
        uiActionExecutor = new UIActionExecutor();
        TetrisUI view = new TetrisUI();
        view.invalidate();
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiActionExecutor.continueExecutions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiActionExecutor.pauseExecutions();
    }

    private class TetrisUI extends View {
        public int screenWidth, screenHeight;
        private final int GRID_GAP = 5;
        private int singleGridSize;
        private Paint paint;
        private final int GRID_BACKGROUND_COLOR = Color.GRAY;
        private final int [] BLOCK_COLORS = getContext().getResources().getIntArray(R.array.block_colors);
        private int blockSideLength, borderWidth;


        public TetrisUI() {
            super(getApplicationContext());
            setKeepScreenOn(true);
            setBackgroundColor(Color.BLACK);
            paint = new Paint();
            setOnTouchListener(new TetrisOnTouchListener());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int color;
            for(int y = TetrisController.FIRST_VISIBLE_ROW; y<= TetrisController.LAST_VISIBLE_ROW; y++){
                for(int x = 0; x< TetrisController.COLUMNS; x++){
                    Optional<Square> square = TetrisController.grid[y][x];
                    if(square.isPresent())
                        color = BLOCK_COLORS[square.get().COLOR];
                    else
                        color = GRID_BACKGROUND_COLOR;
                    paint.setColor(color);
                    canvas.drawRect(getRectangle(x,y), paint);
                }
            }
            try{Thread.sleep(30);}catch (InterruptedException e){}
            invalidate();
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
            singleGridSize = (int)((1.0*(screenHeight-borderWidth))/ TetrisController.NUMBER_OF_VISIBLEROWS);
            borderWidth = (screenWidth - singleGridSize*10)/2;
            blockSideLength = singleGridSize-GRID_GAP*2;
        }

        /*TetrisOnTouchListener gives commands to UIActionExecutor by calling the 'actionExecutor.performAction(Consumer<UIActionExecutor> sideTask)'*/
        private class TetrisOnTouchListener implements OnTouchListener {
            //Last event is always either ACTION DOWN or ACTION MOVE but never ACTION UP
            private Optional<MotionEvent> lastEvent;
            private BiFunction<MotionEvent, MotionEvent, Float> touchXDifference;

            private Predicate<MotionEvent> doubleTapOnCenterOfTheScreen;
            private Predicate<MotionEvent> touchOnRightSideOfScreen;
            private Predicate<MotionEvent> touchOnLeftSideOfScreen;
            private Predicate<MotionEvent> touchOnUpperPartOfScreen;
            private Predicate<MotionEvent> clickOnRightUpperPartOfScreen;
            private Predicate<MotionEvent> clickOnLeftUpperPartOfScreen;
            private Predicate<MotionEvent> swipeFromLeftToRight;
            private Predicate<MotionEvent> swipeFromRightToLeft;
            private Predicate<MotionEvent> swipeFromUpToDown;

            public TetrisOnTouchListener(){
                lastEvent = Optional.empty();
                touchOnRightSideOfScreen = e -> e.getX() > screenWidth*(5.0/6);
                touchOnLeftSideOfScreen = e -> e.getX() < (screenWidth)/6.0;
                touchOnUpperPartOfScreen = e -> e.getY() < screenHeight*(3.0/7);
                touchXDifference = ((first, second) -> (first.getX()-second.getX()));

                swipeFromLeftToRight = e -> lastEvent.isPresent() && e.getAction() != MotionEvent.ACTION_DOWN &&
                        touchXDifference.apply(lastEvent.get(), e)<-screenHeight/10.0;
                swipeFromRightToLeft = e -> lastEvent.isPresent() && e.getAction() != MotionEvent.ACTION_DOWN &&
                        touchXDifference.apply(lastEvent.get(), e)>screenHeight/10.0;
                swipeFromUpToDown = e -> lastEvent.isPresent() && e.getAction() == MotionEvent.ACTION_MOVE &&
                        lastEvent.get().getY()-e.getY()< -screenHeight/10.0;

                clickOnRightUpperPartOfScreen = e -> e.getAction()==MotionEvent.ACTION_UP && touchOnRightSideOfScreen.test(e) &&
                        touchOnUpperPartOfScreen.test(e) && lastEvent.isPresent() && lastEvent.get().getAction() == MotionEvent.ACTION_DOWN &&
                        touchOnRightSideOfScreen.test(lastEvent.get());
                clickOnLeftUpperPartOfScreen = e -> e.getAction()==MotionEvent.ACTION_UP && touchOnLeftSideOfScreen.test(e) &&
                        touchOnUpperPartOfScreen.test(e) && lastEvent.isPresent() && lastEvent.get().getAction() == MotionEvent.ACTION_DOWN &&
                        touchOnLeftSideOfScreen.test(lastEvent.get());

                doubleTapOnCenterOfTheScreen = e -> lastEvent.isPresent() && lastEvent.get().getAction() == MotionEvent.ACTION_DOWN
                        && e.getEventTime()- lastEvent.get().getEventTime()<250 && !touchOnLeftSideOfScreen.test(e) && !touchOnRightSideOfScreen.test(e);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(doubleTapOnCenterOfTheScreen.test(event)){ //double tap
                        lastEvent = Optional.empty();
                        uiActionExecutor.performAction(te -> te.performHardDrop());
                    }
                    else
                        lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(swipeFromUpToDown.test(event)){
                    uiActionExecutor.performAction(te -> te.performSoftDrop());
                    lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(swipeFromLeftToRight.test(event)){
                    uiActionExecutor.performAction(te -> te.performHorizontalMove(Block.MOVE_DIRECTION_RIGHT));
                    lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(swipeFromRightToLeft.test(event)){
                    uiActionExecutor.performAction(te -> te.performHorizontalMove(Block.MOVE_DIRECTION_LEFT));
                    lastEvent = Optional.of(MotionEvent.obtain(event));
                }
                else if(clickOnRightUpperPartOfScreen.test(event))
                    uiActionExecutor.performAction(te -> te.performRotate(Block.CLOCKWISE));
                else if(clickOnLeftUpperPartOfScreen.test(event))
                    uiActionExecutor.performAction(te -> te.performRotate(Block.COUNTERCLOCKWISE));

                return true;
            }
        }
    }
}
