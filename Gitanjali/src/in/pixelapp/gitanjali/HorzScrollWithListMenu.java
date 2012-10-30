package in.pixelapp.gitanjali;

import in.pixelapp.gitanjali.MyHorizontalScrollView.SizeCallback;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomControls;


public class HorzScrollWithListMenu extends Activity {
    MyHorizontalScrollView scrollView;
    View menu;
    View app;
    ImageView btnSlide;
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;
    static int screenOrientation;
    TextView tvPoem,tvPoemHeading;
    static Display screenDimensions ;  
    ZoomControls Zoom;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        screenDimensions = getWindowManager().getDefaultDisplay();
        screenOrientation = getWindowManager().getDefaultDisplay().getRotation();
        System.out.println("Current Orientation is " + screenOrientation);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.horz_scroll_with_list_menu, null);
        setContentView(scrollView);

        menu = inflater.inflate(R.layout.horz_scroll_menu, null);
        app = inflater.inflate(R.layout.horz_scroll_app, null);
        ViewGroup tabBar = (ViewGroup) app.findViewById(R.id.tabBar);

        ListView listView = (ListView) menu.findViewById(R.id.list);
        ViewUtils.initListView(this, listView, "", 103, R.layout.list_item,scrollView);

        tvPoemHeading =(TextView)app.findViewById(R.id.tvPoemHeading);
        tvPoem = (TextView) app.findViewById(R.id.tvPoem);
        tvPoem.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));
        tvPoem.setTextSize(20);
        tvPoem.setText("Use the menu button to select the poem you wish to read");
        
        app.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));
        
        btnSlide = (ImageView) tabBar.findViewById(R.id.BtnSlide);
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));

        final View[] children = new View[] { menu, app };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlide));
        
        Zoom = (ZoomControls)findViewById(R.id.ZoomControls01);
        Zoom.setOnZoomInClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(tvPoem.getTextSize()<55){
					tvPoem.setTextSize(TypedValue.COMPLEX_UNIT_PX ,tvPoem.getTextSize()+ (float)1.5);
				}
			}	
		});
        
        
        Zoom.setOnZoomOutClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(tvPoem.getTextSize()>15){
					tvPoem.setTextSize(TypedValue.COMPLEX_UNIT_PX ,tvPoem.getTextSize() - (float)1.5);
				}
			}
		});
    }    	
    	

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      savedInstanceState.putString("PoemHeading", tvPoemHeading.getText().toString());
      savedInstanceState.putString("Poem", tvPoem.getText().toString());
      savedInstanceState.putFloat("PoemSize", tvPoem.getTextSize());
      if(ClickListenerForScrolling.menuOut){
    	  savedInstanceState.putBoolean("MenuState", !ClickListenerForScrolling.menuOut);
      }
      else{
    	  savedInstanceState.putBoolean("MenuState", ClickListenerForScrolling.menuOut);
      }
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      String Poem = savedInstanceState.getString("Poem");
      String PoemHeading = savedInstanceState.getString("PoemHeading");
      Boolean MenuState = savedInstanceState.getBoolean("MenuState");
      Float PoemSize = savedInstanceState.getFloat("PoemSize");
      
      tvPoem.setText(Poem);
      tvPoem.setTextSize(TypedValue.COMPLEX_UNIT_PX,PoemSize);
      tvPoemHeading.setText(PoemHeading);
      ClickListenerForScrolling.menuOut = MenuState;
    }
    
    /**
     * Helper for examples with a HSV that should be scrolled by a menu View's width.
     */
    static class ClickListenerForScrolling implements OnClickListener {
        HorizontalScrollView scrollView;
        View menu;
        /**
         * Menu must NOT be out/shown to start with.
         */
        public static boolean menuOut = false;
        
        
        public ClickListenerForScrolling(HorizontalScrollView scrollView, View menu) {
            super();
            this.scrollView = scrollView;
            this.menu = menu;
        }

        public void onClick(View v) {
        	       
        switch(v.getId()){
        
        case R.id.BtnSlide: menuOut = callSlider(scrollView,menu,menuOut);
        break;
        
        case R.id.app: if(menuOut) menuOut = callSlider(scrollView,menu,menuOut);
        
        case R.id.tvPoem: if(menuOut) menuOut = callSlider(scrollView,menu,menuOut);
        
        }
        }
        
        public static boolean callSlider(HorizontalScrollView scrollView, View menu, boolean menuOut){
    		
        	Context context = menu.getContext();
            String msg = "Slide " + new Date();
                       
            int width = screenDimensions.getWidth();
            int height = screenDimensions.getHeight();

            int menuWidth = menu.getMeasuredWidth();

            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);
            int left;
            if (!menuOut) {
                // Scroll to 0 to reveal menu
                if(screenOrientation==0)
            	left = (int)(0.625 * width);
                else{
                left = (int)(0.80 * width);	
                }
                scrollView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
                left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
            }
            menuOut = !menuOut;
            return menuOut;            
    	}
    }    
    
    /**
     * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
     * showing.
     */
    static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;
        View btnSlide;

        public SizeCallbackForMenu(View btnSlide) {
            super();
            this.btnSlide = btnSlide;
        }

        public void onGlobalLayout() {
            btnWidth = btnSlide.getMeasuredWidth();
        }

        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            final int menuIdx = 0;
            if (idx == menuIdx) {
                dims[0] = w - btnWidth;
            }
        }
    }

	public void setCantoBody(String string) {
		tvPoem.setMovementMethod(new ScrollingMovementMethod());
		tvPoem.setText(Html.fromHtml(string.replace("'", "&apos;")));
		tvPoem.setGravity(Gravity.LEFT);
	;}
	
	public void setCantoHeading(String string) {
		tvPoemHeading.setText(string);
	}
}
