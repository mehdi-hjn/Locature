package com.ensa.locature.main;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ensa.locature.main.Map.MapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;
import com.ensa.locature.main.cards.SliderAdapter;
import com.ensa.locature.main.utils.DecodeBitmapTask;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;


public class MapActivity extends AppCompatActivity {

    // Note For the Reader : The code hasn't been optimized yet, because of time issues.
    // We prioritize adding new features over optimizing previous code
    // We shall eventually address the dead code which is due to previous implementations or external templates

    private MapFragment mMapFragment;
    private int pos;
    private ArrayList<Agence> AgenceList=new ArrayList<Agence>();
    // Dynamise ASAP (Must Do)
    private final int[] pics = {R.drawable.agence1, R.drawable.agence2, R.drawable.agence3, R.drawable.agence4, R.drawable.agence5};
    // Instantiation du Slider d'Agence
    private SliderAdapter sliderAdapter;

    // Junk Attributes (Should Remove)
    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;
    private ImageSwitcher mapSwitcher;
    private TextSwitcher temperatureSwitcher;
    private TextSwitcher placeSwitcher;
    private TextSwitcher clockSwitcher;
    private TextSwitcher descriptionsSwitcher;
    private View greenDot;
    private final int[][] dotCoords = new int[5][2];
    private final int[] maps = {R.drawable.map_paris, R.drawable.map_seoul, R.drawable.map_london, R.drawable.map_beijing, R.drawable.map_greece};
    private final int[] descriptions = {R.string.text1, R.string.text2, R.string.text3, R.string.text4, R.string.text5};
    private final String[] countries = {"KENITRA", "KENITRA", "KENITRA", "KENITRA", "KENITRA"};
    private final String[] temperatures = {"21°C", "21°C", "21°C", "21°C", "21°C"};
    private final String[] places = {"Agence 1", "Agence 2", "Agence 3", "Agence 4", "Agence 5"};
    private final String[] times = {"Aug 1 - Dec 15    7:00-18:00", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00"};
    private TextView country1TextView;
    private TextView country2TextView;
    private int countryOffset1;
    private int countryOffset2;
    private long countryAnimDuration;
    private int currentPosition;
    private DecodeBitmapTask decodeMapBitmapTask;
    private DecodeBitmapTask.Listener mapLoadListener;

    // Extract to the model Layer into a separate bean (Should)
    public static class Agence implements Serializable {
        private String address;private double lat;private double lng;private String name;private String phone;private String image;
        public Agence(){}
        public Agence(String address, double lat, double lng, String name, String phone, String image) { this.address = address;this.lat = lat;this.lng = lng;this.name = name;this.phone = phone;this.image=image; }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public double getLat() {
            return lat;
        }
        public void setLat(double lat) {
            this.lat = lat;
        }
        public double getLng() {
            return lng;
        }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public void setLng(double lng) {
            this.lng = lng;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Load The Map Frag
        mMapFragment = new MapFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.map, mMapFragment).commit();

        initMap();

        // Load the Markers in the Map
        //initGreenDot();
    }


    //Loads the Markers from the database in the Map
    private void initMap(){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child("Agence");
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Agence lastAgenceVisited = new Agence();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Agence agence=ds.getValue(Agence.class);
                    AgenceList.add(agence);
                    placeMarkerInMap(agence.name,agence.address,agence.lat,agence.lng);
                }
                mMapFragment.placeCamera(AgenceList.get(0).lat,AgenceList.get(0).lng);
                sliderAdapter = new SliderAdapter(pics, 20, new OnCardClickListener(), AgenceList);

                // A replacer dans le onCreate avec des threads plus sophistiqués
                // Positionné ici juste pour assurer le fait qu'on sorte de la boucle for
                // Donc execution apres le Fetching de la DB
                initRecyclerView();
                initCountryText();
                initSwitchers();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void placeMarkerInMap(String title, String snippet, double lat, double lon) {
        if (mMapFragment != null) {
            mMapFragment.placeMarker(title, snippet, lat, lon);
        }
    }



    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });

        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        new CardSnapHelper().attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }
    }

    //Initializes the card Slider
    private void initSwitchers() {


        // This is my part, all the rest is Junk
        placeSwitcher = (TextSwitcher) findViewById(R.id.ts_place);
        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
        placeSwitcher.setCurrentText(AgenceList.get(0).name);


        //Junk Don't Read
        temperatureSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
        temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
        temperatureSwitcher.setCurrentText(temperatures[0]);
        clockSwitcher = (TextSwitcher) findViewById(R.id.ts_clock);
        clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
        clockSwitcher.setCurrentText(times[0]);
        descriptionsSwitcher = (TextSwitcher) findViewById(R.id.ts_description);
        descriptionsSwitcher.setInAnimation(this, android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        descriptionsSwitcher.setCurrentText(getString(descriptions[0]));
        mapSwitcher = (ImageSwitcher) findViewById(R.id.ts_map);
        mapSwitcher.setInAnimation(this, R.anim.fade_in);
        mapSwitcher.setOutAnimation(this, R.anim.fade_out);
        mapSwitcher.setFactory(new ImageViewFactory());
        mapSwitcher.setImageResource(maps[0]);
        mapLoadListener = new DecodeBitmapTask.Listener() {
            @Override
            public void onPostExecuted(Bitmap bitmap) {
                ((ImageView)mapSwitcher.getNextView()).setImageBitmap(bitmap);
                mapSwitcher.showNext();
            }
        };
    }


    private void initCountryText() {
        countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        countryOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
        countryOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
        country1TextView = (TextView) findViewById(R.id.tv_country_1);
        country2TextView = (TextView) findViewById(R.id.tv_country_2);

        country1TextView.setX(countryOffset1);
        country2TextView.setX(countryOffset2);
        country1TextView.setText(countries[0]);
        country2TextView.setAlpha(0f);

        country1TextView.setTypeface(Typeface.createFromAsset(getAssets(), "open-sans-extrabold.ttf"));
        country2TextView.setTypeface(Typeface.createFromAsset(getAssets(), "open-sans-extrabold.ttf"));
    }

    // This method is called when you Slide form an agency to an other in the slider
    private void onActiveCardChange() {

        final int pos = layoutManger.getActiveCardPosition();

        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }
        onActiveCardChange(pos);
    }

    // Auxiliary method to support the previous one, and handles specifc cases like extending the slider
    // Infinitely.
    private void onActiveCardChange(int pos) {
        int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        // Switch la position du cardSlider sur l'agence correspondante
        placeSwitcher.setInAnimation(MapActivity.this, animV[0]);
        placeSwitcher.setOutAnimation(MapActivity.this, animV[1]);
        placeSwitcher.setText(AgenceList.get(pos% AgenceList.size()).name);
        mMapFragment.placeCamera(AgenceList.get(pos% AgenceList.size()).lat,AgenceList.get(pos% AgenceList.size()).lng);


        showMap(maps[pos % maps.length]);

        ViewCompat.animate(greenDot)
                .translationX(dotCoords[pos % dotCoords.length][0])
                .translationY(dotCoords[pos % dotCoords.length][1])
                .start();

        currentPosition = pos;
        this.pos=pos;
    }

    // You click on a card, It sends you to the List Activity
    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {

                // Changed the Behaviour => Go to my List View
                final Intent intent = new Intent(MapActivity.this, ListActivity.class);
                intent.putExtra("pos",pos%AgenceList.size());

                //intent.putExtra(DetailsActivity.BUNDLE_IMAGE_ID, pics[activeCardPosition % pics.length]);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent);
                } else {
                    final CardView cardView = (CardView) view;
                    final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
                    final ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MapActivity.this, sharedView, "shared");
                    startActivity(intent, options.toBundle());
                }
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }
    }

    // This is junk, should remove
    private void showMap(@DrawableRes int resId) {
        if (decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }

        final int w = mapSwitcher.getWidth();
        final int h = mapSwitcher.getHeight();

        decodeMapBitmapTask = new DecodeBitmapTask(getResources(), resId, w, h, mapLoadListener);
        decodeMapBitmapTask.execute();
    }

    // Junk
    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(MapActivity.this);

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(MapActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }

    // Maybe Junk ? Idk, should see
    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            final ImageView imageView = new ImageView(MapActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final LayoutParams lp = new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);

            return imageView;
        }
    }


}
