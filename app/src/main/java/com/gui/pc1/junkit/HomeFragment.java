package com.gui.pc1.junkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ListView listRss;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> pubDates;
    ArrayList<String> descriptions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

//
        listRss = (ListView) view.findViewById(R.id.listRss);
        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        pubDates = new ArrayList<String>();
        descriptions = new ArrayList<String>();

        listRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
//
        new ProcessInBack().execute();
        return view;


    }

    public InputStream getInputStream (URL url)
    {
        try{
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public class ProcessInBack extends AsyncTask <Integer, Void, Exception>
    {
        ProgressDialog progressDialog = new ProgressDialog(getContext());

        Exception exception = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading News Feed... Please Wait...");
            progressDialog.show();
        }
        //
        @Override
        protected Exception doInBackground(Integer... integers) {

            try {
                URL url = new URL("https://www.sciencedaily.com/rss/earth_climate/recycling_and_waste.xml");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                while( eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title"))
                        {
                            if (insideItem)
                            {
                                titles.add(xpp.nextText());
                            }
                        }

                        else if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (insideItem)
                            {
                                descriptions.add(xpp.nextText());
                            }
                        }

                        else if (xpp.getName().equalsIgnoreCase("pubDate"))
                        {
                            if (insideItem)
                            {
                                pubDates.add(xpp.nextText());
                            }
                        }


                        else if (xpp.getName().equalsIgnoreCase("link"))
                        {
                            if (insideItem)
                            {
                                links.add(xpp.nextText());
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }
            }

            catch (MalformedURLException e)
            {
                exception = e;
            }

            catch(XmlPullParserException e)
            {
                exception = e;
            }

            catch(IOException e)
            {
                exception = e;
            }


            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, titles );
            listRss.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }

}


