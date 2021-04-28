package edu.man.javav8.concur.runnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

class ProductModel implements Comparable<ProductModel>{
	int id;
	long createdDt;

	ProductModel(int id) {
		this.id = id;
		this.createdDt = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "ProductModel [id=" + id + ", createdDt=" + createdDt + "]";
	}
	
	public int compareTo(ProductModel o) {
		return Integer.compare(this.id,o.id);
	}
}
class Utils{
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-YYYY hh:mm:ss,SSS");
	static Consumer<String> logger(Class clazz) {
		return (m)->System.out.println("["+SDF.format(new Date(System.currentTimeMillis()))+"] :: "+(null!=clazz?clazz.getName():"")+" - "+m);
	}
}

class ProducerOne implements Runnable {
	Object lock = null;
	Queue queue = null;
	Consumer<String> log = null;
	public ProducerOne(Object lock, Queue queue) {
		this.lock = lock;
		this.queue = queue;
		this.log = Utils.logger(this.getClass());
	}
	static int id = 0;
	
	
	@SuppressWarnings("unchecked")
	public void run() {
		while(true) {
			try {
				this.log.accept("Adding into queue");
				this.queue.add(new ProductModel(++id));
				this.log.accept("Notifying");
				synchronized(this.lock) {
					this.lock.notifyAll();
				}
				this.log.accept("Sleeping");
				Thread.sleep(1000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}

class ConsumerOne implements Runnable {
	Object lock = null;
	Queue queue = null;
	Consumer<String> log = null;
	public ConsumerOne(Object lock, Queue queue) {
		this.lock = lock;
		this.queue = queue;
		this.log = Utils.logger(this.getClass());
	}

	public void run() {
		while(true) {
			try {
				if(this.queue.isEmpty()) {
						this.log.accept("waiting");
						synchronized(this.lock) {
							this.lock.wait();
						}
				}
				this.log.accept("[ConsumerOne]::consuming model => "+this.queue.remove());
				Thread.sleep(1);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}

public class ProducerConsumerSimpleThreads {

	public static void main(String[] args) {
		
		Object lock = new Object();
		Queue<ProductModel> queue = new ConcurrentLinkedQueue<>();
		Thread consumer = new Thread(new ConsumerOne(lock, queue));
		consumer.start();
		
		Thread consumer2 = new Thread(new ConsumerOne(lock, queue));
		consumer2.start();
		
		Thread producer = new Thread(new ProducerOne(lock, queue));
		try {
			producer.join();
			Thread.sleep(2000L);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		producer.start();

	}
}
