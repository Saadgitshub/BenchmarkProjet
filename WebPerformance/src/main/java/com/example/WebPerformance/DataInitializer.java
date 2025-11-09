package com.example.WebPerformance;

import com.example.WebPerformance.entities.*;

import jakarta.persistence.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Configuration
public class DataInitializer {

    private static final int CAT_COUNT = 2_000;
    private static final int ITEMS_PER_CAT = 50;
    private final Random rand = new Random(42);

    @Bean
    CommandLineRunner initData(EntityManagerFactory emf) {
        return args -> {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            // Clear
            em.createQuery("DELETE FROM Item").executeUpdate();
            em.createQuery("DELETE FROM Category").executeUpdate();

            for (int c = 1; c <= CAT_COUNT; c++) {
                String code = String.format("CAT%04d", c);
                Category cat = Category.builder()
                        .code(code)
                        .name("Category " + code)
                        .build();
                em.persist(cat);

                for (int i = 1; i <= ITEMS_PER_CAT; i++) {
                    String sku = String.format("SKU%04d%04d", c, i);
                    Item item = Item.builder()
                            .sku(sku)
                            .name("Item " + sku)
                            .price(BigDecimal.valueOf(10 + rand.nextDouble() * 990))
                            .stock(1 + rand.nextInt(200))
                            .description(rand.nextBoolean() ? null : generateDesc(4000 + rand.nextInt(1000)))
                            .category(cat)
                            .build();
                    em.persist(item);
                }

                if (c % 100 == 0) {
                    em.flush();
                    em.clear();
                    System.out.println("Loaded " + c + " categories...");
                }
            }

            tx.commit();
            em.close();
            System.out.println("Data initialization complete: 2000 cats, 100000 items.");
        };
    }

    private String generateDesc(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append("Description text ").append(rand.nextInt(10000)).append(" ");
        }
        return sb.substring(0, length);
    }
}