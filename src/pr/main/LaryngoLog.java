package pr.main;

import java.awt.EventQueue;

import pr.window.LaryngologFrame;

public class LaryngoLog {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				LaryngologFrame lmf = new LaryngologFrame();
				lmf.setVisible(true);
			}
		});
	}
}
