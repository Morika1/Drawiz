package com.example.drawiz.assets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.example.drawiz.logic.CallBack_openResultProtocol;
import com.example.drawiz.db.DrawingDBManager;
import com.example.drawiz.logic.CallBack_readStatusProtocol;
import com.example.drawiz.logic.CallBack_updateStatusProtocol;
import com.example.drawiz.logic.Piece;
import com.example.drawiz.logic.Room;
import com.example.drawiz.logic.Segment;
import com.example.drawiz.logic.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomDrawView extends View {

    private static final float MOVEMENT_TOLERANCE = 4;
    private float last_x, last_y;
    private int current_color;
    private int current_brushWidth;
    private Path path;
    private Paint paint;
    private ArrayList<Piece> paintPieces = new ArrayList<>();
    private Canvas canvas_board;
    private Bitmap bitmap;
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
    private CallBack_updateStatusProtocol callBack_updateStatusProtocol;
    private CallBack_readStatusProtocol callBack_readStatusProtocol;
    private UserInfo.MODE mode = UserInfo.MODE.APP;

    DrawingDBManager dbManager;

    ValueEventListener updateLisntener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getValue( ) == null && paintPieces.size()>0){
                paintPieces.remove(paintPieces.size()-1);
                invalidate();

            }
            else if(snapshot.exists()){
                convertSegmentToPiece(snapshot.getValue(Segment.class));
                invalidate();
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };




    private void convertSegmentToPiece(Segment segment) {
        paintPieces.add(new Piece().
                setColor(segment.getColor()).
                setBrush_width(segment.getBrushSize()).
                setPath(PathParser.createPathFromPathData(segment.getPathString())));

    }



    public CustomDrawView(Context context) {
        super(context, null);
    }

    public CustomDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        smooth(); // settings to make the drawing smooth and natural
        dbManager = new DrawingDBManager();
    }

    private void smooth(){
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE); // simple shapes without fill
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAlpha(0xff);
    }



    public void setCallBack_updateStatusProtocol(CallBack_updateStatusProtocol callBack_updateStatusProtocol) {
        this.callBack_updateStatusProtocol = callBack_updateStatusProtocol;
    }

    public void setCallBack_readStatusProtocol(CallBack_readStatusProtocol callBack_readStatusProtocol) {
        this.callBack_readStatusProtocol = callBack_readStatusProtocol;
    }

    //initiate bitmap
    public void initBitmap(int h, int w){
        bitmap = bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas_board = new Canvas(bitmap);

        // initial state
        current_color = Color.BLACK;
        current_brushWidth = 30;
    }

    public void setColor(int color){
        current_color = color;
    }
    public void setCurrent_brushWidth(int width){
        current_brushWidth = width;
    }
    public void undo(){
        if(paintPieces.size() > 0){
            paintPieces.remove( paintPieces.size()-1);
            callBack_readStatusProtocol.readStatus(null);
            invalidate(); // to update screen with change by recalling onDraw()
        }
        else
            MySignal.getInstance().toast("No moves to undo!");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // before drawing save canvas current state
        canvas.save();
        // create a whiteboard
        canvas_board.drawColor(Color.WHITE);

        if(mode == UserInfo.MODE.GUESSER){
            callBack_updateStatusProtocol.updateStatus();
        }
        drawOnCanvas();
        canvas.drawBitmap(bitmap, 0 , 0 , bitmapPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mode == UserInfo.MODE.GUESSER)
            return false;

        float x = event.getX();
        float y = event.getY();

        handleEvent(event, x, y);

        return true;
    }

    private void handleEvent(MotionEvent event, float x, float y){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            startBrushPos(x, y);
            invalidate();
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE){
            moveBrushPos(x,y);
            invalidate();
        }

    }

    private void moveBrushPos(float x, float y) {
        float moveX = Math.abs(x - last_x);
        float moveY = Math.abs( y - last_y);

        path.lineTo(moveX, moveY);
    }

    private void startBrushPos(float x, float y) {
        path = new Path();
        paintPieces.add(new Piece().
                setBrush_width(current_brushWidth).
                setColor(current_color).
                setPath(path));

        callBack_readStatusProtocol.readStatus(paintPieces.get(paintPieces.size()-1));

        path.reset(); //clear path after usage
        path.moveTo(x,y); // sets the start of the path to the start position of drawer


    }

    private void drawOnCanvas() {
        for(int i = 0; i< paintPieces.size(); i++){
            paint.setColor(paintPieces.get(i).getColor());
            paint.setStrokeWidth(paintPieces.get(i).getBrush_width());
            canvas_board.drawPath(paintPieces.get(i).getPath(), paint);
        }
    }

    public void setMode(UserInfo.MODE mode) {
        this.mode =mode;

    }


    public ArrayList<Piece> getPaintPieces() {
        return paintPieces;
    }

    public void updateStatus(Room room) {
        dbManager.guesserUpdatesStatus(room, updateLisntener);
    }


    public void readStatus(Piece piece, Room room) {
        dbManager.artistReadsStatus(piece, room);
    }

    public void readRoom(Room room) {
        dbManager.artistReadsRoom(room);
    }

    public void setCallBack(CallBack_openResultProtocol callBack_openResultProtocol) {
        dbManager.setCallBack_openResultProtocol(callBack_openResultProtocol);
    }
}
