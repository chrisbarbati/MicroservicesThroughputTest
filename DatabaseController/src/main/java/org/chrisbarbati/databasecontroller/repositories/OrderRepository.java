package org.chrisbarbati.databasecontroller.repositories;

import org.chrisbarbati.databasecontroller.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for orders in the database
 */

@Repository
@EnableJpaRepositories
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    /**
     * Find an order by its ID
     * @param id The ID of the order
     * @return The order with the given ID
     */
    public OrderEntity findById(int id);

    /**
     * Find an order by the ID of the customer who placed it
     * @param customerId The ID of the customer who placed the order
     * @return The order placed by the given customer
     */
    public OrderEntity findByCustomerId(int customerId);

    /**
     * Find all orders
     * @return A list of all orders in the database
     */
    public List<OrderEntity> findAll();
}
