package nes;

import nes.Renderer.SoundInfo;

public interface Mapper {
    int mapperNo();

    void reset();

    void write(short adr, byte dat);

    void hblank(int line);

    void audio(SoundInfo info);
}