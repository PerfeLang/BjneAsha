package nes;

public interface Renderer {
    public class ScreenInfo {
        public int[] buf;
        public int width;
        public int height;
        public int bpp;
    }

    public class SoundInfo {
        public byte[] buf;
        public int freq;
        public int bps;
        public int ch;
        public int sample;
    }
}