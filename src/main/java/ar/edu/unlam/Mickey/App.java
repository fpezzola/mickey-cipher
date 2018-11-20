package ar.edu.unlam.Mickey;

import java.awt.EventQueue;

import ar.edu.unlam.Mickey.view.Mickey;

public class App {

		public static void main(String[] args) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						new Mickey(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

}
