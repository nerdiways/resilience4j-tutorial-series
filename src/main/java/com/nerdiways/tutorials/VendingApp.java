package com.nerdiways.tutorials;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class VendingApp {

    private final VendingMachine vendingMachine;

    private final CircuitBreaker circuitBreaker;

    private final CircuitBreakerConfig circuitBreakerConfig;

    public VendingApp(VendingMachine vendingMachine){
        this.vendingMachine = vendingMachine;
        circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .slidingWindowSize(5)
                .build();

        circuitBreaker = CircuitBreaker.of("vendingCB", circuitBreakerConfig);

    }

    public OrderResult execute(String itemName, long quantity) throws Exception{

        if(!this.vendingMachine.isDeliveryHandOk()){
            throw new VendingMachineNotOperationalException("Please the machine is un-operational as of now. Try later");
        }

        List<Item> items = this.vendingMachine.getItems();

        long availableQuantityOfRequestedItem = items.stream().filter(item -> item.getName().equalsIgnoreCase(itemName)).count();
        boolean requestedItemIsOutOfStock = availableQuantityOfRequestedItem == 0;
        boolean quantityRequestedIsMoreThanQuantityAvailable = quantity > availableQuantityOfRequestedItem ;

        if(requestedItemIsOutOfStock){
            throw new ItemOutOfStockException("Item: " + itemName + " is out of stock");
        }

        if(quantityRequestedIsMoreThanQuantityAvailable){
            throw new NotEnoughAvailableQuantityException("Not enough available quantity in stock. Item: " + itemName);
        }

        List<Item> orderedItems = items.stream().filter(item -> item.getName().equalsIgnoreCase(itemName))
                .limit(quantity).collect(Collectors.toList());
        String message = "Please take your items, Thank you!";

        orderedItems.forEach(item -> {
            vendingMachine.removeItem(item.getId());
        });

       return new OrderResult(orderedItems, message);
    }

    public Try<OrderResult> buy(Callable<OrderResult> callable){
        Callable<OrderResult> callableCircuitBreaker = circuitBreaker.decorateCallable(callable);
        return Try.ofCallable(callableCircuitBreaker);
    }

    public CircuitBreakerConfig getCircuitBreakerConfig(){
        return circuitBreakerConfig;
    }
}
