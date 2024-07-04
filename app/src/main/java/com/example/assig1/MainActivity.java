package com.example.assig1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bnView;
    private ViewPager viewPager;
    private SliderPagerAdapter sliderPagerAdapter;
    EdgeLightingView edgeLightingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bnView = findViewById(R.id.bnView);
        viewPager = findViewById(R.id.viewPager);
        edgeLightingView = findViewById(R.id.edgeLightingView);

        bnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.edit_light) {
                    loadFrag(new AFragment(), true);
                } else if (id == R.id.add) {
                    loadFrag(new BFragment(), false);
                } else if (id == R.id.setting) {
                    loadFrag(new CFragment(), false);
                } else {
                    loadFrag(new DFragment(), false);
                }

                return false;
            }
        });

        bnView.setSelectedItemId(R.id.edit_light);
    }

    public void loadFrag(Fragment fragment, Boolean flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag)
            ft.add(R.id.container, fragment);
        else
            ft.replace(R.id.container, fragment);
        ft.commit();
    }

    // EdgeLightingView
    public static class EdgeLightingView extends View {

        private Paint paint;
        private int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA,Color.rgb(255, 165, 0)};
        private float strokeWidth = 20f; // Change stroke width as needed
        private float[] positions;
        private int numSegments = 7;

        public EdgeLightingView(Context context) {
            super(context);
            init();
        }

        public EdgeLightingView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public EdgeLightingView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            positions = new float[numSegments];
            startAnimation();
        }

        private void startAnimation() {
            for (int i = 0; i < numSegments; i++) {
                final int index = i;
                ValueAnimator positionAnimator = ValueAnimator.ofFloat(0f, 1f);
                positionAnimator.setDuration(4000); // 4 seconds for a full rotation
                positionAnimator.setRepeatCount(ValueAnimator.INFINITE);
                positionAnimator.setRepeatMode(ValueAnimator.RESTART);
                positionAnimator.setStartDelay(i * 1000); // Stagger the start times
                positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        positions[index] = (float) animation.getAnimatedValue();
                        invalidate(); // Redraw the view
                    }
                });

                positionAnimator.start();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();
            float perimeter = 2 * (width + height);

            for (int i = 0; i < numSegments; i++) {
                paint.setColor(colors[i % colors.length]);
                float currentPos = positions[i] * perimeter;
                drawEdge(canvas, width, height, currentPos);
            }
        }

        private void drawEdge(Canvas canvas, int width, int height, float currentPos) {
            float halfEdgeLength = Math.min(width, height) / 2;

            if (currentPos < width) {
                // Top edge
                canvas.drawLine(currentPos, 0, currentPos + halfEdgeLength, 0, paint);
            } else if (currentPos < width + height) {
                // Right edge
                float adjustedPos = currentPos - width;
                canvas.drawLine(width, adjustedPos, width, adjustedPos + halfEdgeLength, paint);
            } else if (currentPos < 2 * width + height) {
                // Bottom edge
                float adjustedPos = currentPos - width - height;
                canvas.drawLine(width - adjustedPos, height, width - (adjustedPos + halfEdgeLength), height, paint);
            } else {
                // Left edge
                float adjustedPos = currentPos - 2 * width - height;
                canvas.drawLine(0, height - adjustedPos, 0, height - (adjustedPos + halfEdgeLength), paint);
            }
        }
    }
}
