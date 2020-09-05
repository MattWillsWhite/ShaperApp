package com.example.aws.ShaperApp;

import com.example.aws.ShaperApp.Models.Pitch;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AddPitchTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    /*
    @Test
    public void addPitchWithNullTitle() {

        //Define pitch with null title
        final Pitch nullTPitch = new Pitch("",
                "Test",
                "Test" ,
                "Test",
                "Test"
                "Test",
                "Test",
                "Test",
                "Test",
                9999999999999L,
                5L);

        Assert.assertThat(String.format("Pitch Title Null test Fail.",nullTPitch),
                Utils.addPitch(nullTPitch), is(false));
    }
     */
}