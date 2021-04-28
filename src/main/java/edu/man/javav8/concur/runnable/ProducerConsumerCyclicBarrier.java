package edu.man.javav8.concur.runnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Consumer;

import edu.man.javav8.model.ProductModel;
import edu.man.javav8.utils.Utils;

class ProducerBarrier implements Runnable{
	Queue<ProductModel> q;
	CyclicBarrier cb;
	Consumer<String> log;
	public ProducerBarrier(CyclicBarrier cb, Queue<ProductModel> q) {
		this.q = q;
		this.cb = cb;
		log = Utils.logger(this.getClass());
	}
	private static int id = 0;
	@Override
	public void run() {
		log.accept("Thread Started");
		while(true){
			try {
				this.log.accept("Sleeping");
				Thread.sleep(1000L);

				this.q.add(new ProductModel(++id));
				log.accept("Queue is added ["+id+"]");
				
				cb.await();
				
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
}


class ConsumerBarrier implements Runnable{
	Queue<ProductModel> q;
	CyclicBarrier cb;
	Consumer<String> log;
	public ConsumerBarrier(CyclicBarrier cb, Queue<ProductModel> q) {
		this.cb = cb;
		this.q = q;
		log = Utils.logger(this.getClass());
	}
	@Override
	public void run() {
		log.accept("Thread Started");
		while(true){
			try {
				log.accept("Awaiting for consumers, parties:="+cb.getParties());
				cb.await();
				
				ProductModel p = q.poll();
				if(p!=null) {
					log.accept("Product consumed as "+p);
				}else {
					log.accept("Nothing consumed");
				}
				Thread.sleep(100L);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
}

public class ProducerConsumerCyclicBarrier {
	public static void main(String[] args) throws Exception{
		Queue<ProductModel> q = new ConcurrentLinkedQueue<>();
		CyclicBarrier cb = new CyclicBarrier(2);
		Thread t1 = new Thread(new ConsumerBarrier(cb, q),"barrier-consumer");
		t1.start();
		
		Thread t2 = new Thread(new ProducerBarrier(cb, q),"barrier-producer");
		t2.start();
		
		new Thread(()->{
			try {
				Consumer<String> log = Utils.logger(ProducerConsumerCyclicBarrier.class);
				log.accept("Started!");
				while(true) {
					Thread.sleep(3*1000L);
					log.accept("cb parties:="+cb.getParties()+", waiting:="+cb.getNumberWaiting());
				}
			}catch(Throwable tx) {
				tx.printStackTrace();
			}
		}).start();
	}
}
