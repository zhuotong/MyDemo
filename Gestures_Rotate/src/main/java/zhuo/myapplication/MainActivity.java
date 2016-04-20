package zhuo.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    boolean keep = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity_layout);

        Switch sw = (Switch) findViewById(R.id.sw);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Switch sw = (Switch)v;
               keep = !keep;
            }
        });
        ImageButton ib = (ImageButton) findViewById(R.id.ib);
        //加载需要操作的图片，这里是一张图片
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.q);

        //获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        Log.e("zhuo", width + "--" + height);
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        //旋转图片 动作
        matrix.postRotate(45);
        // 创建新的图片
        final Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                width, height, matrix, true);
        Log.e("zhuo", resizedBitmap.getWidth() + "--" + resizedBitmap.getHeight());
        //将上面创建的Bitmap转换成Drawable对象，使得其可以使用在ImageView, ImageButton中
        final BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);

        //创建一个ImageView
        final ImageView imageView = new ImageView(this);

        // 设置ImageView的图片为上面转换的图片
        imageView.setImageDrawable(new BitmapDrawable(bitmapOrg));
        Log.e("zhuo", imageView.getWidth() + "--" + imageView.getHeight());
        //将图片居中显示
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Log.e("zhuo", imageView.getWidth() + "--" + imageView.getHeight());
        LinearLayout linLayout = new LinearLayout(this);
        //将ImageView添加到布局模板中
        linLayout.addView(imageView,
                new LinearLayout.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
                )
        );

        // 设置为本activity的模板
//        setContentView(linLayout);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(bmd);
                Log.e("zhuo", imageView.getWidth() + "--" + imageView.getHeight());
                Log.e("zhuo", resizedBitmap.getWidth() + "--" + resizedBitmap.getHeight());
            }
        });

        Log.e("zhuo", resizedBitmap.getWidth() + "--" + resizedBitmap.getHeight());


        assert ib != null;
        ib.setOnTouchListener(new View.OnTouchListener() {
            int width = 0;
            int height = 0;

            int x = 0;
            int y = 0;
            int z = 0;

            int r = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("zhuo", "down:" + v.getWidth() + "--" + v.getX());
                        width = v.getWidth();
                        height = v.getHeight();

                        x = (int) event.getX();
                        y = (int) event.getY();
                        if(!keep)
                            r = 0;
                        /*RotateAnimation ra = new RotateAnimation(0f,90f,Animation.RELATIVE_TO_SELF,
                                0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                        ra.setDuration(3000);
                        ra.setFillAfter(true);
                        v.setAnimation(ra);
                        v.startAnimation(ra);*/
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getX() < 0 || event.getX() > width)
                            return false;
                        else {

                            int cx = (int) event.getX();
                            int cy = (int) event.getY();

                            if( abs(cx - x) > width/30){
                                x = cx;
                                y = cy;
                                RotateAnimation ra = new RotateAnimation(r,r++,Animation.RELATIVE_TO_SELF,
                                        0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                                ra.setDuration(1000);
                                ra.setFillAfter(true);
//                            v.setAnimation(ra);
                                v.startAnimation(ra);
                            }

//                            ImageButton im = (ImageButton)v;
//                            im.setImageBitmap(resizedBitmap);
//                            im.setBackground(bmd);


                            Log.e("zhuo", event.getX() + ":" + event.getY() + "--" + event.getRawX() + ":" + event.getRawY() + "--" + v.getWidth() + "--" + v.getX());
                        }
                            break;
                    case MotionEvent.ACTION_UP:
                        /*RotateAnimation ra = new RotateAnimation(r++,1,Animation.RELATIVE_TO_SELF,
                                0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                        ra.setDuration(1000);
                        ra.setFillAfter(true);
                            v.setAnimation(ra);
                        v.startAnimation(ra);*/
                        Log.e("zhuo", "up");
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
