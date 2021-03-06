package bpr10.git.voodosample1;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bpr10.git.voodosample1.MainActivity;


public class MyBubbleService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;

    private int lol; //0 = PhonePe, 1 = iPhone, 2 = Samsung;
    private String name;
    private String amount;
    private String product;
    private String phone;
    private Button firstBtn;
    private Button secondBtn;
    private Button cancel;
    private TextView textView;



    public MyBubbleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.bubble_layout, null);


        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
         firstBtn = (Button) mFloatingView.findViewById(R.id.pay_now_btn);
         secondBtn = (Button) mFloatingView.findViewById(R.id.pay_later_s);
         cancel = (Button) mFloatingView.findViewById(R.id.cancel);
         textView =(TextView)mFloatingView.findViewById(R.id.poped_question);

        if (lol == 0){
            Log.i("LolValue", "onCreate: "+lol);
            textView.setText("Would you like to pay "+name+" Rs."+amount+"?");
        } else if (lol ==1 || lol ==2){

            Log.i("LolValue", "onCreate: "+lol);
            textView.setText("Would you like to check awesome deals in "+product+" available in Flipkart or Amazon?");
            firstBtn.setText("Flipkart");
            secondBtn.setText("Amazon");

        }
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });

        //Set the view while floating view is expanded.
        //Set the play button.
        if (MainActivity.lol == 1 || MainActivity.lol ==2) {

            firstBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse("https://www.flipkart.com/search?q="+product);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    launchBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchBrowser);
                    stopSelf();

                }
            });
            secondBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse("https://www.amazon.in/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords=samsung&rh=i%3Aaps%2Ck%3A"+product);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    launchBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchBrowser);
                    stopSelf();

                }
            });

        }else if (MainActivity.lol == 0){
            firstBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MyBubbleService.this, "NICE ", Toast.LENGTH_LONG).show();
                    //TODO MAKE it TO PAYMENT PAGE
                    //Enter the payment method here..
                    Uri pp = Uri.parse("phonepe://pay?mo="+phone+"&pn="+name+"&am="+amount);
                    Intent in = new Intent(Intent.ACTION_VIEW);
                    in.setData(pp);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                }
            });
            //Set the next button.
            secondBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MyBubbleService.this, "Cool! No worries.", Toast.LENGTH_LONG).show();
                    //// TODO: 08/10/17 Make things to Notification
                }
            });
        }

        //Set the pause button.
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();

            }
        });


        //Set the close button
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });


        //Open the application on thi button click
        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the application  click.
                Intent intent = new Intent(MyBubbleService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                //close the service and remove view from the view hierarchy
                stopSelf();
            }
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }
    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent,flags,startId);
        Bundle extras= intent.getExtras();

        if(extras == null) {
            Log.d("Service","null");
        } else {
            Log.d("Service","not null");
            lol = extras.getInt("lol");
            if (lol==0){
                name = extras.getString("name");
                amount = extras.getString("amount");
                phone = extras.getString("phone");

            }
            else{
                product = extras.getString("product");
                firstBtn.setText("Flipkart");
                secondBtn.setText("Amazon");
            }


        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
