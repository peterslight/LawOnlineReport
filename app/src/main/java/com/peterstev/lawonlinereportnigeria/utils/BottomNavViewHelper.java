package com.peterstev.lawonlinereportnigeria.utils;

import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by Peterstev on 12/31/2017.
 */

public class BottomNavViewHelper {
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int x = 0; x < menuView.getChildCount(); x++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(x);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Toast.makeText(view.getContext(), "Bottom Full View Unsupported", Toast.LENGTH_SHORT).show();
        } catch (IllegalAccessException e) {
            Toast.makeText(view.getContext(), "Bottom Full View Unsupported", Toast.LENGTH_SHORT).show();

        }
    }
}
