package edu.man.javav8.model;
public class ProductModel implements Comparable<ProductModel>{
	int id;
	long createdDt;

	public ProductModel(int id) {
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