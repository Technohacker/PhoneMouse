package com.technohacker.java.phonemouse;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.sun.istack.internal.logging.Logger;

public class PMClientHandler extends Thread {

	private Socket client;
	private Robot mouse;
	private Logger log;

	public PMClientHandler(Socket client) {
		this.client = client;
		log = Logger.getLogger(PMClientHandler.class);
		try {
			this.mouse = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			Scanner in = new Scanner(client.getInputStream());
			String command = in.nextLine();
			//while (!((command = in.nextLine()).isEmpty())) {
				if (command.equalsIgnoreCase("SCRLUP")) {
					System.out.println("Scroll Up");
					mouse.mouseWheel(-3);

				} else if (command.equalsIgnoreCase("SCRLDN")) {
					System.out.println("Scroll Down");
					mouse.mouseWheel(3);

				} else if (command.equalsIgnoreCase("LFTPRS")) {
					System.out.println("Left Pressed");
					mouse.mousePress(InputEvent.getMaskForButton(1));

				} else if (command.equalsIgnoreCase("LFTRLS")) {
					System.out.println("Left Released");
					mouse.mouseRelease(InputEvent.getMaskForButton(1));

				} else if (command.equalsIgnoreCase("RGTPRS")) {
					System.out.println("Right Pressed");
					mouse.mousePress(InputEvent.getMaskForButton(3));

				} else if (command.equalsIgnoreCase("RGTRLS")) {
					System.out.println("Right Released");
					mouse.mouseRelease(InputEvent.getMaskForButton(3));
				} else if (command.startsWith("MOVE")) {
					Point cMousePos = MouseInfo.getPointerInfo().getLocation();
					String coords = command.substring(5);
					int dx = -(Integer.parseInt(coords.split(",")[0])) / 15;
					int dy = Integer.parseInt(coords.split(",")[1]) / 15;

					cMousePos.translate(dx, dy);
					mouse.mouseMove(cMousePos.x, cMousePos.y);
				}
				in.close();
			//}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
