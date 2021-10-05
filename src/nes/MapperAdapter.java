package nes;

import nes.Renderer.SoundInfo;

public abstract class MapperAdapter implements Mapper {
    public void audio(SoundInfo info) {
    }

    public void hblank(int line) {
    }

    public void reset() {
    }

    public void write(short adr, byte dat) {
    }
}