package ui;

import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;//SOUNDCODE
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.SourceDataLine;
import nes.Renderer.ScreenInfo;
import nes.Renderer.SoundInfo;

public class AWTRenderer extends Canvas {
    private static final int SCREEN_WIDTH = 256;
    private static final int SCREEN_HEIGHT = 240;
    //SOUNDCODE
    /*private static final int SAMPLE_RATE = 48000;
    private static final int BPS = 16;
    private static final int CHANNELS = 2;
    private static final int BUFFER_FRAMES = 2;

    private static final int FPS = 60;
    private static final int SAMPLES_PER_FRAME = SAMPLE_RATE / FPS;*/

    private ScreenInfo scri = new ScreenInfo();
    private SoundInfo sndi = new SoundInfo();
    private int[] buf = new int[16];

    private Image image = Image.createImage(SCREEN_WIDTH,
                SCREEN_HEIGHT);
    //SOUNDCODE
    //private SourceDataLine line;
    //private int lineBufferSize;
    private final Main main;
    private Image input;
    private boolean player2;

    public AWTRenderer(Main main)  {
        this.main = main;
        setFullScreenMode(true);
        
        try
        {
            input = Image.createImage("/input.png");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        //SOUNDCODE
        /*AudioFormat format = new AudioFormat(SAMPLE_RATE, BPS, CHANNELS, true,
                        false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open();
        line.start();
        lineBufferSize = line.available();

        int bufSamples = SAMPLES_PER_FRAME;

        sndi.bps = 16;
        sndi.buf = new byte[bufSamples * (BPS / 8) * CHANNELS];
        sndi.ch = 2;
        sndi.freq = SAMPLE_RATE;
        sndi.sample = bufSamples;

        buf = new int[16];*/
    }

    public ScreenInfo requestScreen(int width, int height) {
        if (!(scri.width == width && scri.height == height)) {
            scri.width = width;
            scri.height = height;
            scri.buf = new int[width * height];
        }
        return scri;
    }

    public void outputScreen(ScreenInfo info) {
        image = Image.createRGBImage(info.buf, info.width, info.height, false);
        repaint();
    }

    protected void paint (Graphics g)
    {
        g.drawImage(image, 0, 0, Graphics.SOLID);
        g.drawImage(input, 0, 0, Graphics.SOLID);
    }

    public SoundInfo requestSound() {
        if (getSoundBufferState() <= 0)
            return sndi;
        else
            return null;
    }

    //SOUNDCODE
    public void outputSound(SoundInfo info) {
        //line.write(info.buf, 0, info.sample * (info.bps / 8) * info.ch);
    }

    //SOUNDCODE
    public int getSoundBufferState() {
        /*int rest = (lineBufferSize - line.available()) / (sndi.bps / 8)
                    / sndi.ch;
        if (rest < SAMPLES_PER_FRAME * BUFFER_FRAMES)
            return -1;
        if (rest == SAMPLES_PER_FRAME * BUFFER_FRAMES)
            return 0;*/
        return 1;
    }

    static final int radius = 41;
    static final int[] keyDef = {
        137, 340,
        189, 340,
        137, 260,
        189, 260,
        45, 259,
        45, 341,
        4, 300,
        86, 300,
        116, 210
    };

    protected void keyPressed(int keycode) {
        main.display.setCurrent (main.fileBrowser);
    }
    
    protected void pointerPressed(int x, int y) {
        input (x, y, true);
    }
    
    protected void pointerReleased(int x, int y) {
        input (x, y, false);
    }
    
    protected void input(int x, int y, boolean press) {
        for (int i = 0; i < 18; i += 2)
            if (x >= keyDef[i] && x < keyDef[i] + radius
            && y >= keyDef[i + 1] && y < keyDef[i + 1] + radius)
            {
                if (i == 16) {
                    if (press) {
                        player2 = !player2;
                    }
                }
                else {
                    int player = player2 ? 8 : 0;
                    buf[player + (i / 2)] = (press ? 1 : 0);
                }
                break;
            }
    }
    
    public int[] requestInput(int padCount, int buttonCount) {
        return buf;
    }
}