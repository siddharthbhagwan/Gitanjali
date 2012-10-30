/*
 * #%L
 * SlidingMenuDemo
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Paul Grime
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package in.pixelapp.gitanjali;

import in.pixelapp.gitanjali.HorzScrollWithListMenu.ClickListenerForScrolling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Utility methods for Views.
 */
public class ViewUtils {
    private ViewUtils() {
    }

    public static void setViewWidths(View view, View[] views) {
        int w = view.getWidth();
        int h = view.getHeight();
        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            v.layout((i + 1) * w, 0, (i + 2) * w, h);
            printView("view[" + i + "]", v);
        }
    }

    public static void printView(String msg, View v) {
//        System.out.println(msg + "=" + v);
        if (null == v) {
            return;
        }
        System.out.print("[" + v.getLeft());
        System.out.print(", " + v.getTop());
        System.out.print(", w=" + v.getWidth());
        System.out.println(", h=" + v.getHeight() + "]");
        System.out.println("mw=" + v.getMeasuredWidth() + ", mh=" + v.getMeasuredHeight());
        System.out.println("scroll [" + v.getScrollX() + "," + v.getScrollY() + "]");
    }

    public static void initListView(Context context, final ListView listView, String prefix, int numItems, int layout, final MyHorizontalScrollView scrollView) {
        // By using setAdpater method in listview we an add string array in list.
        final String[] arr = new String[numItems];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = prefix + (i + 1);
        }
        
        final HorzScrollWithListMenu cHorzScrollWithListMenu = (HorzScrollWithListMenu)context;
        final String errorDesc = "Sorry, Poem yet to be Updated!";
               
        listView.setAdapter(new ArrayAdapter<String>(context, layout, arr));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                String msg = "item[" + position + "]=" + parent.getItemAtPosition(position) + "has been selected";

                
                try {
                	cHorzScrollWithListMenu.setCantoHeading("Poem " + arr[position]);
                	AssetManager am = context.getAssets();
					InputStream is = am.open("Poem" + arr[position]+".txt");
					InputStreamReader inputStreamReader = new InputStreamReader(is);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuffer canto = new StringBuffer() ;
					String line = "";		
					while((line=bufferedReader.readLine())!=null){
						canto.append(line);
					}					
					cHorzScrollWithListMenu.setCantoBody(canto.toString());
				} catch (IOException e) {
					e.printStackTrace();
					cHorzScrollWithListMenu.setCantoBody(errorDesc);
				}                
                
                //FixMe Find a better way to find and call target parent root
                View parentView = (View) view.getParent().getParent().getParent().getParent();
                ClickListenerForScrolling.menuOut =  ClickListenerForScrolling.callSlider((HorizontalScrollView)parentView, (View) listView.getParent(), true);
                      
                
            }
        });
    }

}
