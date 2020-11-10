package simpledb;

import java.io.Serializable;
import java.util.*;


/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {
	private int numfields;
	private TDItem[] tdAr;
    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        return new TDItemIterator();
    }
    
    private class TDItemIterator implements Iterator<TDItem>{
    	private int pos = 0;
    	
    	@Override
    	public boolean hasNext() {
    		return tdAr.length - 1 > pos;
    	}
    	
    	@Override
    	public TDItem next() {
    		if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return tdAr[pos++];
    	}
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
    	if (typeAr.length == 0) {
    		throw new IllegalArgumentException("typeAr must contain at least one entry.");
    	}
    	this.numfields = typeAr.length;
    	this.tdAr = new TDItem[numfields];
    	for (int i=0; i < this.numfields; i++) {
    		tdAr[i]  = new TDItem(typeAr[i], fieldAr[i]);
    	}
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
    	this(typeAr, new String[typeAr.length]);
    }
    
    public TupleDesc(TDItem[] tdAr) {
        // some code goes here
    	this.numfields = tdAr.length;
    	this.tdAr = tdAr;
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return this.numfields;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
    	if(i<0 || i>=numfields) {
    		throw new NoSuchElementException();
    	}
        return tdAr[i].fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
    	if(i<0 || i>=numfields) {
    		throw new NoSuchElementException();
    	}
        return tdAr[i].fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
    	if(name == null) {
    		throw new NoSuchElementException();
    	}
    	for(int i=0; i<this.numfields; i++) {
    		if(tdAr[i].fieldName != null && tdAr[i].fieldName.equals(name)) {
    			return i;
    		}
    	}
    	throw new NoSuchElementException();
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
    	int size = 0;
    	for(TDItem item: tdAr) {
    		size += item.fieldType.getLen();
    	}
        return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
    	TDItem[] mergetdAr = new TDItem[td1.numfields+td2.numfields];
    	System.arraycopy(td1.tdAr, 0, mergetdAr, 0, td1.numfields);
    	System.arraycopy(td2.tdAr, 0, mergetdAr, td1.numfields, td2.numfields);
        return new TupleDesc(mergetdAr);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        // some code goes here
    	//同一个引用
    	if (this == o) {
    		return true;
    	}
    	if(o instanceof TupleDesc) {
    		TupleDesc tmp = (TupleDesc)o;
    		if(tmp.numfields != this.numfields) return false;
    		for(int i=0; i < this.numfields; i++) {
    			String name1 = this.tdAr[i].fieldName;
    			String name2 = tmp.tdAr[i].fieldName;
    			boolean nameequ = (name1==null && name2==null) || (name1!=null && name1.equals(name2));
    			boolean typeequ = (this.tdAr[i].fieldType).equals(tmp.tdAr[i].fieldType);
    			if(!nameequ||!typeequ) return false;
    		}
    		return true;
    	}
        return false;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
    	String ans = "";
    	for(TDItem item : tdAr) {
    		ans += item.toString();
    	}
        return ans;
    }
}
