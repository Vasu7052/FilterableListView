package com.example.vasu.filterableadapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

    MyCustomAdapter dataAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Generate list View from ArrayList
        displayListView();

    }

    private void displayListView() {

        //Array list of countries
        ArrayList<Country> countryList = new ArrayList<Country>();
        Country country = new Country("AFG","Afghanistan","Asia",
                "Southern and Central Asia");
        countryList.add(country);
        country = new Country("ALB","Albania","Europe","Southern Europe");
        countryList.add(country);
        country = new Country("DZA","Algeria","Africa","Northern Africa");
        countryList.add(country);
        country = new Country("ASM","American Samoa","Oceania","Polynesia");
        countryList.add(country);
        country = new Country("AND","Andorra","Europe","Southern Europe");
        countryList.add(country);
        country = new Country("AGO","Angola","Africa","Central Africa");
        countryList.add(country);
        country = new Country("AIA","Anguilla","North America","Caribbean");
        countryList.add(country);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.country_info, countryList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Country country = (Country) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        country.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Country> {

        private ArrayList<Country> originalList;
        private ArrayList<Country> countryList;
        private CountryFilter filter;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Country> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Country>();
            this.countryList.addAll(countryList);
            this.originalList = new ArrayList<Country>();
            this.originalList.addAll(countryList);
        }

        @Override
        public Filter getFilter() {
            if (filter == null){
                filter  = new CountryFilter();
            }
            return filter;
        }


        private class ViewHolder {
            TextView code;
            TextView name;
            TextView continent;
            TextView region;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.country_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.continent = (TextView) convertView.findViewById(R.id.continent);
                holder.region = (TextView) convertView.findViewById(R.id.region);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Country country = countryList.get(position);
            holder.code.setText(country.getCode());
            holder.name.setText(country.getName());
            holder.continent.setText(country.getContinent());
            holder.region.setText(country.getRegion());

            return convertView;

        }

        private class CountryFilter extends Filter
        {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<Country> filteredItems = new ArrayList<Country>();

                    for(int i = 0, l = originalList.size(); i < l; i++)
                    {
                        Country country = originalList.get(i);
                        if(country.toString().toLowerCase().contains(constraint))
                            filteredItems.add(country);
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else
                {
                    synchronized(this)
                    {
                        result.values = originalList;
                        result.count = originalList.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                countryList = (ArrayList<Country>)results.values;
                notifyDataSetChanged();
                clear();
                for(int i = 0, l = countryList.size(); i < l; i++)
                    add(countryList.get(i));
                notifyDataSetInvalidated();
            }
        }


    }
}