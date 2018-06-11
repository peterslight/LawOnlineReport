package com.peterstev.lawonlinereportnigeria;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Peterstev on 01/05/2018.
 * for LawOnlineReport
 */

public class MainTest {
    @Before
    public void setUp() throws Exception {
        System.out.println("Start");

    }

    @Test
    public void fragTest() throws Exception {
        System.out.println("Test 1");
    }
    @Test
    public void fragTestQ() throws Exception {
        System.out.println("Test 2");
    }


    @After
    public void tearDown() throws Exception {
        System.out.println("finish");
    }

}