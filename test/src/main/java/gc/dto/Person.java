package gc.dto;
import gc.dto.Address;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.util.concurrent.atomic.*;

public class Person {
      /** Indexed field. Will be visible for SQL engine. */
      @QuerySqlField(index = true)
      private long id;
      /** Queryable field. Will be visible for SQL engine. */
      @QuerySqlField(index = true)
      private String name;
      @QuerySqlField
      private int age;
      /** Indexed field. Will be visible for SQL engine. */
      @QuerySqlField(index = true)
      private Address address;

	  public Person(int id, String name, int age, Address address)
      {
        this.id = id;
        this.age = age;
        this.name = name;
        this.address = address;
      }
      public long getId()
      {
        return id;
      }
      public String toString()
      {
        return this.name; 
      }
      public int getAge()
      {
        return age;
      }
      public String getName()
      {
        return name;
      }
      public Person(Person p) 
	  {
         id = p.getId();
         name = p.getName();
         age = p.getAge();
	  }
}


