package com.peterstev.lawonlinereportnigeria.fragments;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.activities.MainActivity;
import com.peterstev.lawonlinereportnigeria.offline.AppDatabase;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.utils.Constants;

/**
 * Created by Peterstev on 04/05/2018.
 * for LawOnlineReport
 */
public abstract class BaseFragment extends Fragment implements SearchView.OnQueryTextListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
//        String sql = "CREATE TABLE " + TABLE_USER + "("
//                + USERS_ID + " INTEGER PRIMARY KEY," + USERS_FULLNAME + " TEXT,"
//                + USERS_EMAIL + " TEXT UNIQUE," + USERS_PHONE + " TEXT,"
//                + USERS_DAY + " TEXT," + USERS_MONTH + " TEXT,"
//                + USERS_YEAR + " TEXT," + USERS_TIME + " TEXT" + ")";

  //  key href posttitle id
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'directory' ('id' INTEGER NOT NULL," +
                            "'key' TEXT, 'href' TEXT, 'postTitle' TEXT, PRIMARY KEY ('id') )"
                    );
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    public AppDatabase getAppDatabase(String tableName) {
        return Room.databaseBuilder(getContext(), AppDatabase.class, tableName)
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build();
    }

    private Toolbar toolbar;

    public void setUpViews(FragmentActivity activity, String toolbarTitle) {
        toolbar = ((MainActivity) activity).getToolbar();
        toolbar.setTitle(toolbarTitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(toolbar.getTitle() == null ? "" : toolbar.getTitle());
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}