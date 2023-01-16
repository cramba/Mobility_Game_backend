package de.hsrm.mi.swt02.backend.api.game.crossroad;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.boot.test.context.SpringBootTest;
import de.hsrm.mi.swt02.backend.api.game.crossroad.service.CrossroadService;
import de.hsrm.mi.swt02.backend.api.game.crossroad.service.CrossroadServiceImpl;
import de.hsrm.mi.swt02.backend.api.game.trafficLight.service.TrafficLightService;
import de.hsrm.mi.swt02.backend.domain.game.trafficLight.Light;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
    Test class for the {@link CrossroadService} class.
    This class tests the functionality of the {@link CrossroadService} class by creating instances of the class,
    creating and manipulating traffic lights, and asserting the expected behavior.
*/
@Testable
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CrossroadServiceTest {

    private CrossroadService crossroadService;

    /**
     * This method is called before each test and creates a new instance of the {@link CrossroadService} class.
     */
    @BeforeEach
    public void startUp(){
        crossroadService = new CrossroadServiceImpl();
    }

    /**
     * Test method for the traffic light state.
     * 
     * This method creates a traffic light, starts the crossroad service, and asserts that the state of the
     * traffic light changes as expected.
     *
     * @throws InterruptedException
     */
    @Test
    public void TrafficLightState() throws InterruptedException {
        TrafficLightService tl = crossroadService.createTrafficLights(1).get(0);
        crossroadService.start();
        TimeUnit.SECONDS.sleep(16);
        assertEquals(Light.YELLOW, tl.getCurrentState());
        TimeUnit.SECONDS.sleep(3);
        assertEquals(Light.RED, tl.getCurrentState());
        TimeUnit.SECONDS.sleep(15);
        assertEquals(Light.REDYELLOW, tl.getCurrentState());
        TimeUnit.SECONDS.sleep(3);
        assertEquals(Light.GREEN, tl.getCurrentState());
        crossroadService.stop();   
    }

    /**
     * This method tests if the thread is closed after calling the stop method.
     * @throws InterruptedException
    */
    @Test
    public void CloseThreadAfterStop() throws InterruptedException {
        TrafficLightService tl = crossroadService.createTrafficLights(1).get(0);
        crossroadService.start();
        Thread t = crossroadService.stop();
        assertTrue(t.isInterrupted());
    }

    /**
     * This method tests the case where the traffic light list is empty.
     * @throws InterruptedException
    */
    @Test
    public void EmptyTrafficLightList() throws InterruptedException {
        crossroadService.start();
        Thread t = crossroadService.stop();
        assertTrue(t.isInterrupted());
    }

    /**
     * This method tests the functionality of multiple traffic lights on one crossroad.
     * @throws InterruptedException
    */
    @Test
    public void MultipleTrafficLightState() throws InterruptedException {
        List<TrafficLightService> tl = crossroadService.createTrafficLights(2);
        TrafficLightService tl1 = tl.get(0); //Starts with Light.GREEN
        TrafficLightService tl2 = tl.get(1); //Starts with Light.RED
        crossroadService.start();

        assertEquals(Light.GREEN, tl1.getCurrentState());
        assertEquals(Light.RED, tl2.getCurrentState());
        TimeUnit.SECONDS.sleep(16);
        assertEquals(Light.YELLOW, tl1.getCurrentState());
        assertEquals(Light.REDYELLOW, tl2.getCurrentState());
        TimeUnit.SECONDS.sleep(3);
        assertEquals(Light.RED, tl1.getCurrentState());
        assertEquals(Light.GREEN, tl2.getCurrentState());
        TimeUnit.SECONDS.sleep(15);
        assertEquals(Light.REDYELLOW, tl1.getCurrentState());
        assertEquals(Light.YELLOW, tl2.getCurrentState());
        TimeUnit.SECONDS.sleep(3);
        assertEquals(Light.GREEN, tl1.getCurrentState());
        assertEquals(Light.RED, tl2.getCurrentState());
        crossroadService.stop();   
    }

    /**
     * This method tests the functionality of multiple crossroads.
     * @throws InterruptedException
    */
    @Test
    public void MultipleCrossroads() throws InterruptedException {
        CrossroadService crossroadService = new CrossroadServiceImpl();
        CrossroadService crossroadService2 = new CrossroadServiceImpl(); //2nd Crossroad
        CrossroadService crossroadService3 = new CrossroadServiceImpl(); //3nd Crossroad
        List<TrafficLightService> tl = crossroadService.createTrafficLights(2);
        List<TrafficLightService> tl2 = crossroadService2.createTrafficLights(3);
        List<TrafficLightService> tl3 = crossroadService3.createTrafficLights(4);
        
        crossroadService.start();
        crossroadService2.start();
        crossroadService3.start();
        Thread t = crossroadService.stop();
        TimeUnit.SECONDS.sleep(5);
        assertTrue(t.isInterrupted());
        assertFalse(crossroadService2.getThread().isInterrupted());
        assertFalse(crossroadService3.getThread().isInterrupted());
    }
}
