package edu.man.javav8.concur.runnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import edu.man.javav8.model.ProductModel;
import edu.man.javav8.utils.Utils;

class ProducerTwo implements Runnable{
	Queue<ProductModel> q;
	Semaphore s;
	Consumer log;
	public ProducerTwo(Queue<ProductModel> q, Semaphore s) {
		this.q = q;
		this.s = s;
		log = Utils.logger(this.getClass());
	}
	public static int id = 0;
	@Override
	public void run() {
		try {
			log.accept("Thread Started");
			while(true) {
				this.log.accept("Sleeping");
				Thread.sleep(1000L);
				
				q.add(new ProductModel(++id));
				log.accept("Queue is added ["+id+"]");
//				s.release();
//				log.accept("Semaphore released, avail:="+s.availablePermits());
			}
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}
}
class ConsumerTwo implements Runnable{
	Queue<ProductModel> q;
	Semaphore s;
	Consumer log;
	public ConsumerTwo(Queue<ProductModel> q, Semaphore s) {
		this.q = q;
		this.s = s;
		log = Utils.logger(this.getClass());
	}

	@Override
	public void run() {
			log.accept("Thread Started");
			while(true) {
				try {
					Thread.sleep(250);
					if(q.isEmpty()) {
						log.accept("Semaphore acquired with q empty, avail:="+s.availablePermits());
						s.acquire();
					}
					log.accept("Semaphore released, avail:="+s.availablePermits());
					ProductModel p = q.poll();
					if(p!=null) {
						log.accept("Product consumed as "+p);
					}else {
						log.accept("Nothing consumed");
					}
				}catch(Throwable t) {
					t.printStackTrace();
				}
			}
	}
}

public class ProducerConsumerSemaphore {
	public static void main(String[] args) throws Exception {
		Semaphore s = new Semaphore(1);
		Queue<ProductModel> q = new ConcurrentLinkedQueue();
		Thread consumer = new Thread(new ConsumerTwo(q, s),"semaphore-consumer");
		consumer.start();
		Thread producer = new Thread(new ProducerTwo(q, s),"semaphore-producer");
		producer.join();
		producer.start();
		(new Thread(()->{
			Consumer log=Utils.logger(ProducerConsumerSemaphore.class);
			log.accept("Started!");
			while(true) {
				try{
					Thread.sleep(12*1000L);
					log.accept("Releasing semaphore");
					s.release();
				}catch(Throwable t) {}
			}},"semaphore-release")).start();
	}
}
