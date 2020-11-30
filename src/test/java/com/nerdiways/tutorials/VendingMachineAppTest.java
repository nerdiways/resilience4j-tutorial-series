package com.nerdiways.tutorials;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VendingMachineAppTest {

    private static VendingApp vendingApp;

    @BeforeAll
    public static void setupForAllTest(){
        VendingMachine vendingMachine = new VendingMachine();
        vendingMachine.setPowerOn(true);
        vendingMachine.setDeliveryHandOk(true);

        Item sodaOne = new Item();
        sodaOne.setId(1);
        sodaOne.setName("SweetOrange");

        Item sodaTwo = new Item();
        sodaTwo.setId(2);
        sodaTwo.setName("SweetOrange");

        Item sodaThree = new Item();
        sodaThree.setId(3);
        sodaThree.setName("SweetOrange");

        List<Item> items = new ArrayList<>();
        items.add(sodaOne);
        items.add(sodaTwo);
        items.add(sodaThree);

        vendingMachine.setItems(items);

        vendingApp = new VendingApp(vendingMachine);
    }

    @Test
    @Order(1)
    public void buyItem_shouldThrowException_givenOrderQuantityIsMoreThanAvailable() {
        int minimumNumberOfCalls = vendingApp.getCircuitBreakerConfig().getMinimumNumberOfCalls();

        for(int i=1; i <= minimumNumberOfCalls; i++){
            Callable<OrderResult> orderResultCallableScenarioOne =
                    ()-> vendingApp.execute("SweetOrange", 4);

            Try<OrderResult> expectedFailedOrderResultTry = vendingApp.buy(orderResultCallableScenarioOne);

            assertTrue(expectedFailedOrderResultTry.isFailure());
            assertEquals(expectedFailedOrderResultTry.getCause().getClass(), NotEnoughAvailableQuantityException.class);
            assertEquals(expectedFailedOrderResultTry.getCause().getMessage(),
                    "Not enough available quantity in stock. Item: SweetOrange");
        }
    }

    @Test
    @Order(2)
    public void buyItem_ShouldOpenCircuitBreaker_whenThresholdIsExceeded(){
        for(int j=0; j < 3; j++){
            Callable<OrderResult> orderResultCallableScenarioTwo =
                    ()-> vendingApp.execute("SweetOrange", 4);

            Try<OrderResult> expectedCircuitBreakerOpenedOrderResultTry =
                    vendingApp.buy(orderResultCallableScenarioTwo);

            assertEquals(expectedCircuitBreakerOpenedOrderResultTry.getCause().getClass(),
                    CallNotPermittedException.class);
        }
    }

    @Test
    @Order(3)
    public void buyItem_shouldSuccessfullyExecute_whenCircuitBreakerIsHalfOpened() throws InterruptedException{
        Thread.sleep(1000);

        int numberOfPermittedCallsInHalfOpenState = vendingApp.getCircuitBreakerConfig().getPermittedNumberOfCallsInHalfOpenState();

        for(int k=0; k < numberOfPermittedCallsInHalfOpenState; k++){
            Callable<OrderResult> orderResultCallableScenarioThree =
                    ()-> vendingApp.execute("SweetOrange", 1);

            Try<OrderResult> expectedCircuitBreakerHalfOpenedStateOrderResultTry =
                    vendingApp.buy(orderResultCallableScenarioThree);

            assertTrue(expectedCircuitBreakerHalfOpenedStateOrderResultTry.isSuccess());
            assertEquals(expectedCircuitBreakerHalfOpenedStateOrderResultTry.get().getItems().size(), 1);
        }
    }
}
