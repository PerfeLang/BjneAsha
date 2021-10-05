package ui;

import java.util.Calendar;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import nes.Nes;

public class Main extends MIDlet implements Runnable {
    public Display display = Display.getDisplay (this);
    public Command exitCommand = new Command ("Exit", Command.EXIT, 0);
    public FileBrowser fileBrowser = new FileBrowser(this);
    public AWTRenderer r = null;
            
    private Nes nes = null;
    private Thread thread;

    public void startApp() {
        display.setCurrent(fileBrowser);
        r = new AWTRenderer(this);
    }

    public void run() {
        // Workaround to weird issue where display.setCurrent takes a millisecond
        while (display.getCurrent() != r) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        final int FPS = 60;

        while (display.getCurrent() == r) {
            long start = Calendar.getInstance().getTime().getTime();
            nes.execFrame();
            //SOUNDCODE
            /*int bufStat = r.getSoundBufferState();
            if (bufStat < 0)
                break;
            if (bufStat == 0) {*/
                long elapsed = Calendar.getInstance().getTime().getTime() - start;
                long wait = (long) (1.0 / FPS - elapsed / 1e-9);
                try {
                    if (wait > 0)
                        Thread.sleep(wait);
                } catch (InterruptedException e) {
                }
                /*break;
            }*/
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    public void openRom(String file) {
        System.out.println("loading " + file);
        nes = new Nes(r);
        nes.load(file);
        display.setCurrent(r);
        thread = new Thread (this);
        thread.start();
    }

    protected void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    protected void pauseApp(){}
}