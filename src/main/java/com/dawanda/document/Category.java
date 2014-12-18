package com.dawanda.document;

import com.dawanda.utils.CategoryUtils;

/**
 * Represents the metadata of the class/category.
 * <p/>
 * Created by awolny on 07/12/14.
 */
public class Category {
    private final String id;

    public Category(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return CategoryUtils.getName(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!id.equals(category.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}
