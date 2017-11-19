package edu.ecu.cs.pirateplaces;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by RabindraK on 11/18/2017.
 */
public class PiratePlaceViewModelTest {

    private PiratePlace mPiratePlace;
    private PirateBase mPirateBase;
    private PiratePlaceViewModel mPiratePlaceViewModel;


    @Before
    public void setUp() throws Exception {
        mPirateBase = mock(PirateBase.class);
        mPiratePlace = new PiratePlace();
        mPiratePlaceViewModel = new PiratePlaceViewModel(mPirateBase);
       mPiratePlaceViewModel.setPiratePlace(mPiratePlace);

    }

    @Test
    public void testPlaceName(){
        assertThat(mPiratePlaceViewModel.getPlaceName(),is(mPiratePlace.getPlaceName()));
    }

    @Test
    public void testLastVisited(){
        assertThat(mPiratePlaceViewModel.getLastVisited(),is(mPiratePlace.getLastVisited().toString()));
    }

    @Test
    public void testUpdatePiratePlace(){
        mPiratePlaceViewModel.updatePiratePlace();
        verify(mPirateBase).updatePiratePlace(mPiratePlace);
    }

    @Test
    public void testUpdatePlaceName(){
        mPiratePlaceViewModel.setPlaceName(mPiratePlace.getPlaceName());
        mPiratePlaceViewModel.updatePiratePlace();
        verify(mPirateBase).updatePiratePlace(mPiratePlace);
    }

    @Test
    public void testUpdateLastVisited(){
        mPiratePlaceViewModel.setLastVisited(mPiratePlace.getLastVisited().toString());
        mPiratePlaceViewModel.updatePiratePlace();
        verify(mPirateBase).updatePiratePlace(mPiratePlace);
    }

}