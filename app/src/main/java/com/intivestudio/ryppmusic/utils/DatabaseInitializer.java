package com.intivestudio.ryppmusic.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.intivestudio.ryppmusic.data.local.DaoAccess;
import com.intivestudio.ryppmusic.data.local.UserDAO;

import java.util.List;

import androidx.annotation.NonNull;

public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static UserDAO addUser(final AppDatabase db, UserDAO user) {
        db.userDao().insertAll(user);
        return user;
    }

    private static void populateWithTestData(AppDatabase db) {
        UserDAO user = new UserDAO();
        user.setName("Ajay");
        user.setEmail("Saini");
        user.setPhone("081");
        addUser(db, user);

        List<UserDAO> userList = db.userDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userList.size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }
    }

}
