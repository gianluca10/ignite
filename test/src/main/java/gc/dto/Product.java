package gc.dto;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
/* ID (long)
 * Name (String)
 * Price (float)
 * Quantity in stock (Integer)
 * Quantity on order (Integer)
*/

public class Product {
      /** Indexed field. Will be visible for SQL engine. */
      @QuerySqlField(index = true)
      private long id;
      /** Queryable field. Will be visible for SQL engine. */
      @QuerySqlField(index = true)
      private String name;
      /** Will NOT be visible for SQL engine. */
      private float price;
      @QuerySqlField
      private int quantity_in_stock;
      @QuerySqlField
      private int quantity_in_order;

	  public Product(long id, String name, float price, int quantity_in_stock, int quantity_in_order)
      {
        this.id = id;
        this.price = price;
        this.name = name;
        this.quantity_in_stock = quantity_in_stock;
        this.quantity_in_order = quantity_in_order;
      }
      public String toString()
      {
        return this.name;
      }
}

/*
public class Person implements Serializable
{

    @QuerySqlField(index = true)
    private String name;

    @QuerySqlField(index = true)
    private int age;

    public Person(int age, String name)
    {
        this.age = age;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
*/

