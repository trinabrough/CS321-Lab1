import java.util.LinkedList;

/**
 * Cache stors items of generic type in a list. A cache keeps the Most Recently Used items at the front (or top) of the list.
 * New items are always added at the front (or top). Referenced (searched) items will be moved to the top of the cache. Adding an item when
 * the capacity is full drops the item stored at the end.
 * @author Trina Brough Spring 2024
 */

public class Cache<T> {
  //declare instance variable
  int capacity;
  int currentSize; 
  LinkedList<T> thisCache;

  /**Creates an empty cache with a maximum capacity */
  public Cache (int cacheCapacity){
    this.capacity = cacheCapacity;
    currentSize = 0;
    thisCache = new LinkedList<T>();
  }

  /**findItemIndex searches the cache for the element passed in and returns the index
   * @param T item: object to search for
   * @return index of object, -1 if not found
   */
    public int findItemIndex(T item){
      return thisCache.indexOf(item);
    }

    /**moveToTop moves the element stored at index by deleting the element at index and adding it to the front of the list
     * @param int index is the index of the element to move
     */
    public void moveToTop (int index){
      T elementToMove = thisCache.remove(index);
      thisCache.addFirst(elementToMove);
    }

    /**addToTop adds item to the beginning of the list
     * @param T item: element to add to beginning of list
     * Checks list capacity
     */
    public void addToTop (T item){
      thisCache.addFirst(item);
      if(thisCache.size() > capacity){
        thisCache.removeLast();
      }
    }


  /**toString prints out capacity and current size of cache (not contents due to possible large amount) */
  public String toString(){
    String returnString = "Cache capacity: " + capacity + "\n";
    returnString += "Cache current size: " + currentSize;
    return returnString;
  }

}
