package com.ensa.locature.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.ensa.locature.main.FoldingCell.FoldingCellListAdapter;
import com.ensa.locature.main.FoldingCell.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * The Activity responsible for displaying the list of cars for each agency
 */
public class ListActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener{

    ListView theListView;
    ArrayList<Item> items =new ArrayList<>();
    ArrayList<Car> cars=new ArrayList<>();
    int pos;

    //private TextView mResultTextView;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Receive Intent from previous activity (MapActivity) to tell the corresponding agency from pos
        Intent intent =getIntent();
        pos = intent.getIntExtra("pos",0);

        // Get our list view
        theListView = findViewById(R.id.mainListView);

        //Instantiate the db
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child("Products");

        //Appel de la base de donnée en asyncTask, d'ou le "onPostXkyute" pour le faire executer apres le fetching de la DB.
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Car car=ds.getValue(Car.class);
                    if(car.idAgence==(pos+1)){
                        items.add(new Item(car.price, "$270", car.pname, car.category, 3, "DATE",car.date,car.image));
                        cars.add(car);
                        System.out.println(items);
                    }
                }
                // Set the list adapter and call the drawing functions
                onPostXkyute();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public void onPostXkyute(){


        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        final FoldingCellListAdapter adapter = new FoldingCellListAdapter(this, items);

        // add default btn handler for each request btn on each item if custom handler not found
        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the datepicker
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(ListActivity.this)
                        .setThemeDark();
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        // set elements to adapter
        theListView.setAdapter(adapter);

        // set on click event listener to list view
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                // toggle clicked cell state
                FoldingCell v = (FoldingCell) view;
                v.drawUnfolded(cars.get(pos).image,cars.get(pos).pname,cars.get(pos).category,cars.get(pos).rating,cars.get(pos).gas,
                        cars.get(pos).nbrDoors,cars.get(pos).transmission,cars.get(pos).seats,cars.get(pos).price);
                v.toggle(false);

                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });
    }


    // After Date has been chosen => Go to Payment
    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        //mResultTextView.setText(getString(R.string.calendar_date_picker_result_values, year, monthOfYear, dayOfMonth));
        Intent intent = new Intent(ListActivity.this, PayActivity.class);
        startActivity(intent);

    }

    public static class Car implements Serializable {

        public String category;
        public String date;
        public String gas;
        public int idAgence;
        public String image;
        public String pid;
        public String pname;
        public String price;
        public String time;
        public String nbrDoors;
        public String seats;
        public int rating;
        public String transmission;

        public Car(){}

        public Car(String category, String date,  String gas, int idAgence, String image, String pid, String pname, String price, String time, String nbrDoors, String seats, int rating, String transmission) {
            this.category = category;
            this.date = date;
            this.idAgence = idAgence;
            this.image = image;
            this.pid = pid;
            this.pname = pname;
            this.price = price;
            this.time = time;
            this.gas = gas;
            this.nbrDoors = nbrDoors;
            this.seats = seats;
            this.rating = rating;
            this.transmission = transmission;
        }
    }


}

/* Junk

      // add custom btn handler to first list item
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();


            }
        });
 */
