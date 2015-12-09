package com.workingagenda.democracydroid;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.workingagenda.democracydroid.feedreader.RssItem;
import com.workingagenda.democracydroid.feedreader.RssReader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //ArrayAdapter<String> AudioListAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Gather the Episode Lists

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        //Declaire some variables
        public ListView videoList;
        public ListView mList;
        //ArrayAdapter<String> VideoListAdapter;

        // Episode objects!!!
        ArrayList<Episode> episodes = new ArrayList<Episode>(10);
        // set up custom adapter with episodes
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }
        public void populateList(ArrayList<Episode> episodes) {
            mList.setAdapter(new EpisodeAdapter(getContext(), R.layout.episode_row, episodes));
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            // this needs custom adapter
            // VideoListAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.video_row);

            new GetVideoFeed().execute("http://www.democracynow.org/podcast-video.xml");
            new GetAudioFeed().execute("http://www.democracynow.org/podcast.xml"); // must be called second

            mList = (ListView) rootView.findViewById(R.id.list);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Episode e = episodes.get(i);
                    // CHANGE INTENT depending on the
                    if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                        Intent y = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getVideoUrl()));
                        startActivityForResult(y, 0); //ACTIVITY_LOAD = 0?
                    } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                        // Audio Array is broken
                        // play as a service
                        Intent y = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getAudioUrl()));
                        startActivityForResult(y, 0); //ACTIVITY_LOAD = 0?
                    }
                    /**
                     * TODO:Have the APP GALLERY play the video
                     */
                }
            });
            mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Episode e = episodes.get(i);
                    AlertDialog description = new AlertDialog.Builder(
                            rootView.getContext()).create();
                    // Get Description and Title
                    description.setTitle("The War and Peace Report");
                    description.setMessage(e.getDescription() + "\n\n" + e.getTitle());
                    //description.setIcon(R.drawable.dm_icon_small);
                    /**
                     * TODO: Share Button Context Menu
                     */
                    description.setButton("Share", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //...
                        }

                    });
                    description.show();
                    return true;
                }
            });

            return rootView;
        }
        private class GetVideoFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    RssReader rssReader = new RssReader(params[0]);
                    int j = 0;
                    for(RssItem item : rssReader.getItems()){
                        //VideoListAdapter.add(item.getTitle().substring(14));
                        // This should just be the Episode Object (class?)
                        Episode e = new Episode();
                        e.setTitle(item.getTitle());
                        e.setVideoUrl(item.getVideoUrl());
                        e.setDescription(item.getDescription());
                        e.setImageUrl(item.getImageUrl());
                        e.setUrl(item.getLink());
                        episodes.add(e);
                        j++;
                    }
                    //EpisodeAdapter episodeAdapter = new EpisodeAdapter(getContext(), R.layout.episode_row, episodes);
                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //VideoListAdapter.notifyDataSetChanged();
                // AudioListAdapter.notifyDataSetChanged();
                populateList(episodes);
            }
        }
        private class GetAudioFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    RssReader rssReader = new RssReader(params[0]);
                    int j = 0;
                    for(RssItem item : rssReader.getItems()){
                        episodes.get(j).setAudioUrl(item.getVideoUrl());
                        // Audio Feed must be called before Video Feed
                        // Otherewise the episodes objects wont be there
                        j++;
                    }
                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //VideoListAdapter.notifyDataSetChanged();
                // AudioListAdapter.notifyDataSetChanged();
                //videoList.setAdapter(VideoListAdapter);
            }
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }


}
