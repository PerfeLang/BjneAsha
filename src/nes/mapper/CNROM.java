package nes.mapper;

import nes.MapperAdapter;
import nes.Nes;

public class CNROM extends MapperAdapter {
    private Nes nes;

    public CNROM(Nes n) {
        nes = n;
        reset();
    }

    public int mapperNo() {
        return 3;
    }

    public void reset() {
        for (int i = 0; i < 4; i++)
            nes.getMbc().mapRom(i, i);
        for (int i = 0; i < 8; i++)
            nes.getMbc().mapVrom(i, i);
    }

    public void write(short adr, byte dat) {
        for (int i = 0; i < 8; i++)
            nes.getMbc().mapVrom(i, (dat & 0xff) * 8 + i);
    }
}