package com.dawanda.db;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Simple POJO to keep the product attributes fetched from db.
 */
public class Product {
    private final int id;
    private final int sellerId;
    private final int category;
    private final String title;
    private final String description;

    @JsonCreator
    public Product(@JsonProperty("id") int id,
                   @JsonProperty("sellerId") int sellerId,
                   @JsonProperty("category") int category,
                   @JsonProperty("title") String title,
                   @JsonProperty("description") String description) {
        this.id = id;
        this.sellerId = sellerId;
        this.category = category;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getSellerId() {
        return sellerId;
    }

    public int getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", category=" + category +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", sellerId=" + sellerId +
                '}';
    }
}
