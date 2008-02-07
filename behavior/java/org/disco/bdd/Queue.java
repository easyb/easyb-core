package org.disco.bdd;

import java.util.ArrayList;

public class Queue<E> {

	private ArrayList<E> list;
	
	public Queue(){
		this.list = new ArrayList<E>();
	}
	
	public void enqueue(E value){
		if(value == null){
			throw new RuntimeException("Can't enqueue a null value");
		}
		this.list.add(value);
	}
	
	public E dequeue(){
		if(this.list.isEmpty()){
			throw new RuntimeException("Nothing to dequeue");
		}else{
			return this.list.remove(0);
		}
		
	}
}
