package edu.man.javav8.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class Utils{
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-YYYY hh:mm:ss,SSS");
	public static Consumer<String> logger(Class clazz) {
		return (m)->System.out.println("["+SDF.format(new Date(System.currentTimeMillis()))+"] :: "+(null!=clazz?clazz.getName():"")+" - "+m);
	}
}