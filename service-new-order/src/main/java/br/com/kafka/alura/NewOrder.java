package br.com.kafka.alura;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrder {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try(var emailDispatcher = new KafkaDispatcher<Email>()) {
                var userEmail = Math.random() + "@email.com";
                int i = 1;
                while (i < 10) {
                    var email = new Email("Thank you", "Thank you for your order!");
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userEmail, email);
                    i++;

                    var orderId = UUID.randomUUID().toString();
                    var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    var order = new Order(orderId, amount, userEmail);
                    orderDispatcher.send("ECOMMERCE_SEND_ORDER", userEmail, order);
                }
            }
        }
    }
}
