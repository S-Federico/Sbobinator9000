package com.imotorini.sbobinator9000;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.imotorini.sbobinator9000.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class WaveformView extends View {
    private final ArrayList<Float> amplitudes = new ArrayList<>();
    private final ArrayList<RectF> spikes = new ArrayList<>();
    private final Paint paint = new Paint();
    private int maxSpikes = 0;

    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.rgb(3,218,197));
        float sw = (float) getResources().getDisplayMetrics().widthPixels;
        maxSpikes = (int) (sw /(Constants.W +Constants.RD));
        requestLayout();
    }

    public void addAmplitude(float amp){
        amplitudes.add(amp);
        spikes.clear();
        List<Float> amps = amplitudes.subList(Math.max(amplitudes.size() - maxSpikes, 0), amplitudes.size());

        updateSpikes(amps);

        invalidate();
    }

    private void updateSpikes(List<Float> amps){
        for (int i = 0; i < amps.size(); i++) {
            float normalizedAmp = (amps.get(i) / Constants.THRESHOLD) * Constants.SH;
            normalizedAmp = Math.min(Constants.SH, normalizedAmp);

            float left = (Constants.W +Constants.RD)*(i-1);
            float top = (Constants.SH /2)-(normalizedAmp/2);
            float right = left+Constants.W;
            float bottom = top + normalizedAmp;
            spikes.add(new RectF(left,top,right,bottom));
        }
    }

    public void clearWaveform() {
        amplitudes.clear();
        spikes.clear();
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (RectF rect : spikes) {
            canvas.drawRoundRect(rect, Constants.RD, Constants.RD, paint);
        }
    }
}
