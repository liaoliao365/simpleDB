package simpledb;

import java.io.*;
import java.util.*;


/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
	private File file;
	private TupleDesc tupleDesc;
	private int numPages;
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
    	this.file = f;
    	this.tupleDesc = td;
    	this.numPages =(int) file.length()/BufferPool.getPageSize();
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
    	//???
    	return file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
    	return tupleDesc;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
    	Page page = null;
    	byte[] data = new byte[BufferPool.getPageSize()];
    	try(RandomAccessFile raf = new RandomAccessFile(file, "r")){
    		int pos = pid.getPageNumber()*BufferPool.getPageSize();
    		raf.seek(pos);
    		raf.read(data, 0, data.length);
    		page = new HeapPage((HeapPageId)pid, data);
    	} catch(IOException e){
    		e.printStackTrace();
    	}
        return page;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return numPages;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new HeapFileIterator(tid);
    }

    private class HeapFileIterator implements DbFileIterator {
    	//暂时是借鉴别人的函数，这个迭代器有点看不懂
    	private int pagePos;

        private Iterator<Tuple> tuplesInPage;

        private TransactionId tid;

        public HeapFileIterator(TransactionId tid) {
            this.tid = tid;
        }

        public Iterator<Tuple> getTuplesInPage(HeapPageId pid) throws TransactionAbortedException, DbException {
            // 不能直接使用HeapFile的readPage方法，而是通过BufferPool来获得page，理由见readPage()方法的Javadoc
            HeapPage page = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
            return page.iterator();
        }

        @Override
        public void open() throws DbException, TransactionAbortedException {
            pagePos = 0;
            HeapPageId pid = new HeapPageId(getId(), pagePos);
            //加载第一页的tuples
            tuplesInPage = getTuplesInPage(pid);
        }

        @Override
        public boolean hasNext() throws DbException, TransactionAbortedException {
            if (tuplesInPage == null) {
                //说明已经被关闭
                return false;
            }
            //如果当前页还有tuple未遍历
            if (tuplesInPage.hasNext()) {
                return true;
            }
            //如果遍历完当前页，测试是否还有页未遍历
            //注意要减一，这里与for循环的一般判断逻辑（迭代变量<长度）不同，是因为我们要在接下来代码中将pagePos加1才使用
            //如果不理解，可以自己举一个例子想象运行过程
            if (pagePos < numPages() - 1) {
                pagePos++;
                HeapPageId pid = new HeapPageId(getId(), pagePos);
                tuplesInPage = getTuplesInPage(pid);
                //这时不能直接return ture，有可能返回的新的迭代器是不含有tuple的
                return tuplesInPage.hasNext();
            } else return false;
        }

        @Override
        public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException("not opened or no tuple remained");
            }
            return tuplesInPage.next();
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException {
            //直接初始化一次。。。。。
            open();
        }

        @Override
        public void close() {
            pagePos = 0;
            tuplesInPage = null;
        }
    	
    	
    }
}

