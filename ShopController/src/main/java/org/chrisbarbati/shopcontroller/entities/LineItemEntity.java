package org.chrisbarbati.shopcontroller.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    * Entity class for line items on an order
 */
public class LineItemEntity {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(LineItemEntity.class);

    private int id;

    @JsonBackReference
    private OrderEntity order;

    private int customerId;

    private int productId;

    private int quantity;

    /**
     * Default constructor
     */
    public LineItemEntity() {
    }

    /**
     * Fully parameterized constructor
     * @param id Database ID for the line item
     * @param customerId ID of the customer who ordered the product
     * @param productId ID of the product that was ordered
     * @param quantity Number of the product that was ordered
     */
    public LineItemEntity(int id, OrderEntity order, int customerId, int productId, int quantity) {
        setId(id);
        setOrder(order);
        setCustomerId(customerId);
        setProductId(productId);
        setQuantity(quantity);
    }

    /**
     * Serialize the line item to a JSON string
     *
     * @return JSON string representation of the line item
     */
    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            log.info("Error serializing LineItemEntity to JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return The ID of the line item
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The ID of the line item
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The order to which the line item belongs
     */
    public OrderEntity getOrder() {
        return order;
    }

    /**
     * @param order The order to which the line item belongs
     */
    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    /**
     * @return The ID of the customer who ordered the product
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId The ID of the customer who ordered the product
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * @return The ID of the product that was ordered
     */
    public int getProductId() {
        return productId;
    }

    /**
     * @param productId The ID of the product that was ordered
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * @return The number of the product that was ordered
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity The number of the product that was ordered
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
