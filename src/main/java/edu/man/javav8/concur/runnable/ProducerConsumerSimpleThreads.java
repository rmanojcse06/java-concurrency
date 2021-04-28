package edu.man.javav8.concur.runnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import edu.man.javav8.model.ProductModel;
import edu.man.javav8.utils.Utils;


class ProducerOne implements Runnable {
	Object lock = null;
	Queue<ProductModel> q = null;
	Consumer<String> log = null;
	public ProducerOne(Object lock, Queue<ProductModel> queue) {
		this.lock = lock;
		this.q = queue;
		this.log = Utils.logger(this.getClass());
	}
	static int id = 0;
	
	
	@SuppressWarnings("unchecked")
	public void run() {
		log.accept("Thread Started");
		while(true) {
			try {
				this.log.accept("Sleeping");
				Thread.sleep(1000L);

				this.q.add(new ProductModel(++id));
				log.accept("Queue is added ["+id+"]");
				this.log.accept("Notifying");
				synchronized(this.lock) {
					this.lock.notifyAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}

class ConsumerOne implements Runnable {
	Object lock = null;
	Queue<ProductModel> q = null;
	Consumer<String> log = null;
	public ConsumerOne(Object lock, Queue<ProductModel> queue) {
		this.lock = lock;
		this.q = queue;
		this.log = Utils.logger(this.getClass());
	}

	public void run() {
		log.accept("Thread Started");
		while(true) {
			try {
				if(this.q.isEmpty()) {
						this.log.accept("waiting");
						synchronized(this.lock) {
							this.lock.wait();
						}
				}
				ProductModel p = q.poll();
				if(p!=null) {
					log.accept("Product consumed as "+p);
				}else {
					log.accept("Nothing consumed");
				}
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
