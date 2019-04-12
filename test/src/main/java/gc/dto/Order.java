package gc.dto;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.util.concurrent.atomic.*;
             
/*
ID (String)
PersonID (long)
ProductID (long)
Quantity
*/

public class Order {
      /** Indexed field. Will be visible for SQL engine. */
      @QuerySqlField(index = true)
      private long id;
      @QuerySqlField
      private long personId;
      @QuerySqlField
      private long productId;
      private int quantity;

	  public Order(long id, long personId, long productId, int quantity)
      {
        this.id = id;
        this.quantity = quantity;
        this.personId = personId;
        this.productId = productId;
      }
      public long getId()
      {
        return id;
      }
      public long getProductId()
      {
        return productId;
      }
      public long getPersonId()
      {
        return personId;
      }
      public int getQuantity()
      {
        return quantity;
      }
      public Order(Order o)
      {
         id = o.getId();
         personId = o.getPersonId();
         productId = o.getProductId();
         quantity = o.getQuantity();
      }
}
