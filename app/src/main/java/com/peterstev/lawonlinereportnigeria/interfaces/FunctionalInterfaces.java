package com.peterstev.lawonlinereportnigeria.interfaces;

/**
 * Created by Peterstev on 4/14/2018.
 * for LawOnlineReport
 */

public class FunctionalInterfaces {

    public interface GetData {
        void getData();
    }

    public interface UpdateData {
        void updateData(boolean shouldUpdate);
    }

    public interface LoadPost {
        void getData(String postLink);
    }

    public interface JsoupClean {
        String clean(String html);
    }
}
