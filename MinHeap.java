/*
	min heap - implementation in arr (as we learn in "data structure" class)
	Parent(i) return floor[i/2]
	Left(i)   return 2i.
	Right(i)  return 2i + 1.
*/

public class MinHeap{

    private Node[] heap;
    private int currSize;
    private int maxsize;
	
	//constructor-empty heap
	public MinHeap (int maxSize) {
		this.maxsize = maxSize;
		this.currSize = 0;
		heap = new Node[maxSize];
	}
	
	//constructor-full heap
	public MinHeap (Node[] arr) {
		this.maxsize = arr.length;
		this.currSize = arr.length;
		heap = arr;
		createMinHeap();
		for (int i = 0; i < arr.length; i++) {
			heap[i].indexInHeap = i;
		}
	}
	
	public boolean isEmpty() {
		if(currSize==0)
			return true;
		return false;
	}
	
	public Node peek() {
		if(isEmpty())
			return null;
		return heap[0]; //without extract
	}
	
	public Node extractMin() {
		if(isEmpty())
			return null;
		Node result = heap[0];
		result.indexInHeap = -1;
		heap[0] = heap[currSize-1];
		heap[0].indexInHeap = 0;
		currSize--;
		minHeapify(0);
		return result;
	}
	
	public void insert(Node n) {
		if(maxsize<currSize+1) 
			doubleSize();
		
		currSize++;
		double value = n.value;
		n.value = Double.POSITIVE_INFINITY;
		heap[currSize-1] = n;
		heap[currSize-1].indexInHeap = currSize-1;
		decreaseKey(currSize-1, value);
	}
	
	private void doubleSize() {
		Node[] temp = new Node[maxsize*2+1];
		for (int i = 0; i < heap.length; i++)
			temp[i]=heap[i];
		maxsize*=2;
		maxsize+=1;
		heap=temp;	
	}
	
	public void decreaseKey(int index, double newVALUE){
		if(heap[index].value < newVALUE)
			System.out.println("error in decreaseKey func: newValue > oldValue");
		heap[index].value = newVALUE;
		while(index > 0 && heap[getParent(index)].value > heap[index].value) {
			swap(index, getParent(index));
			index = getParent(index);
		}
	}
	
	public void heapReverseSort() {  //from big number to small
		createMinHeap();
		for (int i = currSize; i > 0; i--) {
			swap(0, i);
			currSize--;
			minHeapify(0);
		}
	}
	
	private void swap(int a, int b){
		Node temp = heap[a];
		heap[a] = heap[b];
		heap[b] = temp;
		
		int temp1 = heap[a].indexInHeap;
		heap[a].indexInHeap=heap[b].indexInHeap;
		heap[b].indexInHeap=temp1;
	}
	
	private int getParent(int index){
		return (int)Math.floor(index/2);
	}
	
	private int getLeftChild(int index){
		return 2 * index;
	}
	
	private int getRightChild(int index){
		return 2 * index + 1;
	}
	
	private void minHeapify(int index){
		int leftChild = getLeftChild(index);
		int rightChild = getRightChild(index);
	    
	    if(leftChild<=currSize-1 && heap[leftChild].value < heap[index].value){
	    	swap(index, leftChild);
	    	minHeapify(leftChild);
	    }
	    else if(rightChild <= currSize-1 && heap[rightChild].value < heap[index].value) {
	    	swap(index, rightChild);
	    	minHeapify(rightChild);
	    }
	}
	
	private void createMinHeap() {
		for (int i = (int) Math.floor(currSize/2); i > 0; i--) {
			minHeapify(i);
		}
	}
	
	public int getSize() {
		return currSize;
	}
	
	public void clear() {
		heap= new Node[maxsize];
	    currSize=0;;
	}


}