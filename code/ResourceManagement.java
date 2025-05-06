import java.io.*;
import java.util.*;

/* ResourceManagement
 *
 * Stores the information needed to decide which items to purchase for the given budget and departments
 */
public class ResourceManagement
{
  private PriorityQueue<Department> departmentPQ; /* priority queue of departments */
  private Double remainingBudget;                 /* the budget left after purchases are made (should be 0 after the constructor runs) */
  private Double budget;                          /* the total budget allocated */
  
  /* TO BE COMPLETED BY YOU
   * Fill in your name in the function below
   */  
  public static void printName( )
  {
    /* TODO : Fill in your name */
    System.out.println("This solution was completed by:");
    System.out.println("Rylan Larson");
    System.out.println("Ethan Silberberg");
  }

  /* Constructor for a ResourceManagement object
   * TODO
   * Simulates the algorithm from the pdf to determine what items are purchased
   * for the given budget and department item lists.
   */
  public ResourceManagement( String fileNames[], Double budget )
  {
    /* Create a department for each file listed in fileNames */
    this.departmentPQ = new PriorityQueue<>();
    this.budget = budget;
    this.remainingBudget = budget;

    for (String fileName : fileNames) {
      this.departmentPQ.add(new Department(fileName));
    }
    
    /* Simulate the algorithm for picking the items to purchase */
    /* Be sure to print the items out as you purchase them */
    /* Here's the part of the code I used for printing prices as items */
    //String price = String.format("$%.2f", /*Item's price*/ );
    //System.out.printf("Department of %-30s- %-30s- %30s\n", /*Department's name*/, /*Item's name*/, price );

    while(this.remainingBudget > 0 && !this.departmentPQ.isEmpty()) {
      Department department = this.departmentPQ.poll();

      // Remove remaining items that are too expensive for budget
      while(!department.itemsDesired.isEmpty() && department.itemsDesired.peek().price > this.remainingBudget) {
        department.itemsRemoved.add(department.itemsDesired.poll());
      }

      // Check if no more desired items and add scholarship of $1000 or remaining budget
      if(department.itemsDesired.isEmpty()) {
        double scholarshipAmount = Math.min(1000, remainingBudget);
        this.remainingBudget -= scholarshipAmount;

        department.priority += scholarshipAmount;
        department.itemsReceived.add(new Item("Scholarship", scholarshipAmount));

      } else { // If there are still desired items purchase the next one
        Item nextDesired = department.itemsDesired.poll();

        this.remainingBudget -= nextDesired.price;

        department.itemsReceived.add(nextDesired);
        department.priority += nextDesired.price;

        String price = String.format("$%.2f", nextDesired.price );
        System.out.printf("Purchased item for department of %-30s- %-30s- %30s\n", department.name, nextDesired.name, price );
      }

      this.departmentPQ.add(department);
    }


    // For departments that didn't get processed or still have desired items,
    // move all remaining desired items to the removed list
    for (Department dept : this.departmentPQ) {
      while (!dept.itemsDesired.isEmpty()) {
        dept.itemsRemoved.add(dept.itemsDesired.poll());
      }
    }
  } 

  /* printSummary
   * TODO
   * Print a summary of what each department received and did not receive.
   * Be sure to also print remaining items in each itemsDesired Queue.
   */      
  public void printSummary(  ){
    
    /* Here's the part of the code I used for printing prices */
    //String price = String.format("$%.2f", /*Item's price*/ );
    //System.out.printf("%-30s - %30s\n", /*Item's name*/, price );

    for (Department department : this.departmentPQ) {
      System.out.printf("Department of %s%n", department.name);
      System.out.printf("Total Spent       = $%.2f%n", department.priority);
      System.out.printf("Percent of Budget = %.2f%%%n", (department.priority / this.budget) * 100);
      System.out.println("----------------------------");
      System.out.println("ITEMS RECEIVED");
      for (Item item : department.itemsReceived) {
        String price = String.format("$%.2f", item.price );
        System.out.printf("%-30s - %30s%n", item.name, price);
      }
      System.out.println();
      System.out.println("ITEMS NOT RECEIVED");
      for (Item item : department.itemsRemoved) {
        String price = String.format("$%.2f", item.price );
        System.out.printf("%-30s - %30s%n", item.name, price);
      }
      System.out.println();
    }
  }   
}

/* Department
 *
 * Stores the information associated with a Department at the university
 */
class Department implements Comparable<Department>
{
  String name;                /* name of this department */
  Double priority;            /* total money spent on this department */
  Queue<Item> itemsDesired;   /* list of items this department wants */
  Queue<Item> itemsReceived;  /* list of items this department received */
  Queue<Item> itemsRemoved;   /* list of items that were skipped because they exceeded the remaining budget */

  /* TODO
   * Constructor to build a Department from the information in the given fileName
   */
  public Department( String fileName ){
    /* Open the fileName, create items based on the contents, and add those items to itemsDesired */
    this.itemsDesired = new LinkedList<>();
    this.itemsReceived = new LinkedList<>();
    this.itemsRemoved = new LinkedList<>();
    this.priority = 0.0;

    File file = new File(fileName);
    try (Scanner scan = new Scanner(file)) {
      if(scan.hasNextLine()) {
        this.name = scan.nextLine();
      }

      while(scan.hasNext()) {
        String itemName = scan.next();

        if (scan.hasNextDouble()) {
          Double itemPrice = scan.nextDouble();
          this.itemsDesired.add(new Item(itemName, itemPrice));
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  /*
   * Compares the data in the given Department to the data in this Department
   * Returns -1 if this Department comes first
   * Returns 0 if these Departments have equal priority
   * Returns 1 if the given Department comes first
   *
   * This function is to ensure the departments are sorted by the priority when put in the priority queue 
   */
  public int compareTo( Department dept ){
    return this.priority.compareTo( dept.priority );
  }

  public boolean equals( Department dept ){
    return this.name.compareTo( dept.name ) == 0;
  }

  @Override 
  @SuppressWarnings("unchecked") //Suppresses warning for cast
  public boolean equals(Object aThat) {
    if (this == aThat) //Shortcut the future comparisons if the locations in memory are the same
      return true;
    if (!(aThat instanceof Department))
      return false;
    Department that = (Department)aThat;
    return this.equals( that ); //Use above equals method
  }
  
  @Override
  public int hashCode() {
    return name.hashCode(); /* use the hashCode for data stored in this name */
  }

  /* Debugging tool
   * Converts this Department to a string
   */	
  @Override
  public String toString() {
    return "NAME: " + name + "\nPRIORITY: " + priority + "\nDESIRED: " + itemsDesired + "\nRECEIVED " + itemsReceived + "\nREMOVED " + itemsRemoved + "\n";
  }
}

/* Item
 *
 * Stores the information associated with an Item which is desired by a Department
 */
class Item
{
  String name;    /* name of this item */
  Double price;   /* price of this item */

  /*
   * Constructor to build a Item
   */
  public Item( String name, Double price ){
    this.name = name;
    this.price = price;
  }

  /* Debugging tool
   * Converts this Item to a string
   */		
  @Override
  public String toString() {
    return "{ " + name + ", " + price + " }";
  }
}
