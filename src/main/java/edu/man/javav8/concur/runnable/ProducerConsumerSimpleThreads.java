package edu.man.javav8.concur.runnable;
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


class ProducerOne implements Runnable {
	public ProducerOne() {}

	public void run() {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ConsumerOne extends Thread {
	public ConsumerOne() {}

	public void run() {
		try {
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

public class ProducerConsumerSimpleThreads {

	public static void main(String[] args) {
		
		Thread consumer = new Thread(new ConsumerOne());
		consumer.start();
		Thread producer = new Thread(new ProducerOne());
		producer.start();

	}
}
